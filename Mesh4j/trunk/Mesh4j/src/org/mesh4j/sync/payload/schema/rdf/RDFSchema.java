package org.mesh4j.sync.payload.schema.rdf;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.XSD;

//TODO (JMT) RDF: FeedWriter: write content as item element (use mesh4x namespace) 
//TODO (JMT) RDF: FeedReader: read content from item elements (use mesh4x namespace)
public class RDFSchema implements IRDFSchema{

	// MODEL VARIABLES
	private String ontologyBaseUri;
	private String ontologyNameSpace;
	private String ontologyClassName;
	
	private OntModel schema;
	private OntClass domainClass;
	
	// BUSINESS METHODS
	
	public RDFSchema(Reader reader){
		Guard.argumentNotNull(reader, "reader");
		
		this.schema = ModelFactory.createOntologyModel();
		try{
			this.schema.read(reader, "");
		} catch(JenaException e){
			throw new MeshException(e);
		}
		
		ExtendedIterator it = this.schema.listClasses();
		if(it.hasNext()){
			this.domainClass = (OntClass)this.schema.listClasses().next();
		} else {
			Guard.throwsArgumentException("reader");			
		}

		String[] uri = this.domainClass.getURI().split("#");
		this.ontologyBaseUri = uri[0]+"#";
		this.ontologyClassName = this.domainClass.getLocalName();
		this.ontologyNameSpace = uri[1];
		this.schema.setNsPrefix(this.ontologyNameSpace, this.ontologyBaseUri);

	}
	
	public RDFSchema(String ontologyNameSpace, String ontologyBaseUri, String ontologyClassName){
		Guard.argumentNotNullOrEmptyString(ontologyNameSpace, "ontologyNameSpace");
		Guard.argumentNotNullOrEmptyString(ontologyBaseUri, "ontologyBaseUri");
		Guard.argumentNotNullOrEmptyString(ontologyClassName, "ontologyClassName");
		
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
	
	public void addLongProperty(String propertyName, String label, String lang) {
		this.addProperty(propertyName, label, lang, XSD.xlong);	
	}
	
	public void addDoubleProperty(String propertyName, String label, String lang) {
		this.addProperty(propertyName, label, lang, XSD.xdouble);	
	}
	
	public void addDecimalProperty(String propertyName, String label, String lang) {
		this.addProperty(propertyName, label, lang, XSD.decimal);	
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

	public String getPropertyLabel(String propertyName, String lang) {
		String propertyUri = this.ontologyBaseUri + propertyName;
		DatatypeProperty datatypeProperty = this.schema.getDatatypeProperty(propertyUri);
		if(datatypeProperty ==  null){
			return null;
		} else {
			return datatypeProperty.getLabel(lang);
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

	@SuppressWarnings("unchecked")
	public String getPropertyName(int index) {	
		List<DatatypeProperty> items = this.schema.listDatatypeProperties().toList();
		if(items.isEmpty() || items.size() <= index){
			return null;
		} else {
			return items.get(index).getLocalName();
		}
	}
	
	protected OntModel getOWLSchema() {
		return this.schema;
	}

	@Override
	public Element asInstancePlainXML(Element element, Map<String, ISchemaTypeFormat> typeFormats){
		Element rdfElement = getRDFElement(element);
		if(rdfElement == null){
			return null;
		}
		
		RDFInstance rdfInstance = this.createNewInstanceFromRDFXML(rdfElement.asXML());
		String xml = rdfInstance.asPlainXML(typeFormats);
		return XMLHelper.parseElement(xml);
	}

	private Element getRDFElement(Element element) {
		Element rdfElement;
		if(element.getName().equals("RDF")){
			rdfElement = element;
		} else {
			rdfElement = element.element("RDF");	
		}
		return rdfElement;
	}

	@Override
	public Element getInstanceFromPlainXML(String id, Element element, Map<String, ISchemaTypeFormat> typeFormats){
		RDFInstance rdfInstance = this.createNewInstanceFromPlainXML(id, element.asXML(), typeFormats);
		String xml = rdfInstance.asXML();
		return XMLHelper.parseElement(xml);
	}

	@Override
	public Element getInstanceFromXML(Element element) {
		Element rdfElement = getRDFElement(element);
		if(rdfElement == null){
			return null;
		}
		return rdfElement.createCopy();
		// TODO (JMT) RDF: improve rdf model parser
		//RDFInstance rdfInstance = this.createNewInstanceFromRDFXML(element.asXML());
		//String xml = rdfInstance.asXML();
		//return XMLHelper.parseElement(xml);
	}
	
	@Override
	public RDFInstance createNewInstance(String id) {
		RDFInstance instance = new RDFInstance(this, id);
		return instance;
	}
	
	@Override
	public RDFInstance createNewInstanceFromRDFXML(String rdfXml) {
		RDFInstance instance = RDFInstance.buildFromRDFXml(this, rdfXml);
		return instance;
	}

	@Override
	public RDFInstance createNewInstanceFromPlainXML(String id, String plainXML, Map<String, ISchemaTypeFormat> formatters){
		return RDFInstance.buildFromPlainXML(this, id, plainXML, formatters);
	}
	
	@Override
	public String asXMLText() {
		return asXML();
	}

}
