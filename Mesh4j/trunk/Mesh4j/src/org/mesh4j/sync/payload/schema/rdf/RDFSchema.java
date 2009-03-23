package org.mesh4j.sync.payload.schema.rdf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import org.dom4j.Element;
import org.mesh4j.sync.utils.XMLHelper;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

//TODO RDF: sync test
//TODO RDF: MSAccess rdf schema extraction
//TODO RDF: MSAccess creation from rdf schema
//TODO RDF: Hibernate mappings generation
//TODO RDF: XForms generation
//TODO RDF: XForms instance generation
//TODO RDF: javaRosa get and put xforms 
public class RDFSchema implements IRDFSchema{

	// MODEL VARIABLES
	private String ontologyBaseUri;
	private String ontologyNameSpace;
	private String ontologyClassName;
	
	private OntModel schema;
	private OntClass domainClass;
	
	// BUSINESS METHODS
	public RDFSchema(String ontologyNameSpace, String ontologyBaseUri, String ontologyClassName, Reader reader){
		this.ontologyBaseUri = ontologyBaseUri;
		this.ontologyClassName = ontologyClassName;
		this.ontologyNameSpace = ontologyNameSpace;

		this.schema = ModelFactory.createOntologyModel();
		this.schema.setNsPrefix(this.ontologyNameSpace, this.ontologyBaseUri);
	
		this.schema.read(reader, "");
		
		String classNameUri = this.ontologyBaseUri + this.ontologyClassName;
		this.domainClass = schema.getOntClass(classNameUri);
		if(this.domainClass == null){
			this.domainClass = schema.createClass(classNameUri);
		}
	}
	
	public RDFSchema(String ontologyNameSpace, String ontologyBaseUri, String ontologyClassName){
		this.ontologyBaseUri = ontologyBaseUri;
		this.ontologyClassName = ontologyClassName;
		this.ontologyNameSpace = ontologyNameSpace;

		this.schema = ModelFactory.createOntologyModel();
		this.schema.setNsPrefix(this.ontologyNameSpace, this.ontologyBaseUri);
	
		String classNameUri = this.ontologyBaseUri + this.ontologyClassName;
		this.domainClass = schema.createClass(classNameUri);
	}
	
	public void addStringProperty(String propertyName, String label, String lang){
		this.addProperty(propertyName, label, lang, XSD.xstring);
	}
	
	public void addIntegerProperty(String propertyName, String label, String lang){
		this.addProperty(propertyName, label, lang, XSD.integer);
	}
	
	public void addBooleanProperty(String propertyName, String label, String lang){
		this.addProperty(propertyName, label, lang, XSD.xboolean);
	}
	public void addDateTimeProperty(String propertyName, String label, String lang){
		this.addProperty(propertyName, label, lang, XSD.dateTime);
	}
	
	private void addProperty(String propertyName, String label, String lang, Resource xsd){
		String propertyUri = this.ontologyBaseUri + propertyName;
		DatatypeProperty domainProperty = this.schema.createDatatypeProperty(propertyUri);
		
		domainProperty.addDomain(domainClass);
		domainProperty.addRange(xsd);
		domainProperty.addLabel(label, lang);
	}
	
	@Override
	public String asXML(){
		StringWriter sw = new StringWriter();
		this.schema.write(sw, "RDF/XML-ABBREV");
		return sw.toString();
	}

	@Override
	public RDFInstance createNewInstance(String id) {
		RDFInstance instance = new RDFInstance(this, id);
		return instance;
	}
	
	@Override
	public RDFInstance createNewInstance(String id, String rdfXml) {
		RDFInstance instance = new RDFInstance(this, id, rdfXml);
		return instance;
	}

	@Override
	public RDFInstance createNewInstance(String id, String plainXML, String idColumnName) throws Exception {
		Element element = XMLHelper.parseElement(plainXML);
		
		RDFInstance instance = createNewInstance(id);
		
		Element fieldElement;
		String fieldValue;
		String dataTypeName;
		
		DatatypeProperty dataTypeProperty;
		ExtendedIterator it = this.schema.listDatatypeProperties();
		while(it.hasNext()){
			dataTypeProperty = (DatatypeProperty)it.next();

			dataTypeName = dataTypeProperty.getLocalName();
			
			fieldElement = element.element(dataTypeName);
			if(fieldElement != null){
				fieldValue = fieldElement.getText();
				
				OntResource range = dataTypeProperty.getRange();
				
				RDFDatatype dataType = TypeMapper.getInstance().getTypeByName(range.getURI());
				if(dataType.isValid(fieldValue)){
					instance.setProperty(dataTypeName, dataType.parse(fieldValue));
				}
			}
		}
		return instance;
	}
	
	public String getOntologyBaseUri() {
		return ontologyBaseUri;
	}

	public String getOntologyNameSpace() {
		return ontologyNameSpace;
	}

	public String getOntologyClassName() {
		return ontologyClassName;
	}

	protected OntClass getDomainClass() {
		return domainClass;
	}

	protected OntModel getRDFModel() {
		return this.schema;
	}

	public void write(String rdfFileName) throws IOException{
		FileWriter writer = new FileWriter(rdfFileName);
		this.schema.write(writer);
	}

	public int getPropertyCount() {
		return this.schema.listDatatypeProperties().toSet().size();
	}

	public String getPropertyType(String propertyName) {
		String propertyUri = this.ontologyBaseUri + propertyName;
		DatatypeProperty datatypeProperty = this.schema.getDatatypeProperty(propertyUri);
		if(datatypeProperty ==  null){
			return null;
		} else {
			OntResource range = datatypeProperty.getRange();
			return range.getURI();
		}
	}

	public Object cannonicaliseValue(String propertyName, Object value) {
		String propertyUri = this.ontologyBaseUri + propertyName;
		DatatypeProperty datatypeProperty = this.schema.getDatatypeProperty(propertyUri);
		if(datatypeProperty ==  null){
			return null;
		} else {
			OntResource range = datatypeProperty.getRange();
			RDFDatatype dataType = TypeMapper.getInstance().getTypeByName(range.getURI());
			return dataType.cannonicalise(value);
		}
	}

	public String getPropertyName(int index) {		
		return ((DatatypeProperty)this.schema.listDatatypeProperties().toList().get(index)).getLocalName();
	}

	// ISchemaResolver methods
	@Override
	public Element getSchema() {
		return XMLHelper.parseElement(this.asXML());
	}
}
