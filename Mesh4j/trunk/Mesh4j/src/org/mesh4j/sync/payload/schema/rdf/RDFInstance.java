package org.mesh4j.sync.payload.schema.rdf;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.datatypes.xsd.XSDDateTime;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.JenaException;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

public class RDFInstance {
	
	private final static Log LOGGER = LogFactory.getLog(RDFInstance.class);
		
	// MODEL VARIABLES
	private RDFSchema schema;
	
	private OntModel model;
	private Individual domainObject;
	
	// BUSINESS MODEL

	private RDFInstance(RDFSchema schema){		
		Guard.argumentNotNull(schema, "schema");
		initializeBaseModel(schema);		
	}
	
	protected RDFInstance(RDFSchema schema, String id) {
		this(schema);		
		Guard.argumentNotNullOrEmptyString(id, "id");
		
		if(!(id.startsWith("http:") || id.startsWith("uri:urn:"))){
			Guard.throwsArgumentException("id", id);	
		}
		
		this.domainObject = model.createIndividual(id, schema.getDomainClass());
	}
	
	protected static RDFInstance buildFromRDFXml(RDFSchema schema, String rdfXml) {
		Guard.argumentNotNullOrEmptyString(rdfXml, "rdfXml");
		
		RDFInstance instance = new RDFInstance(schema);		

		StringReader sr = new StringReader(rdfXml);
		try{
			instance.model.read(sr, "");
		}catch (JenaException e) {
			throw new MeshException(e);
		}
		
		ExtendedIterator it = instance.model.listIndividuals(schema.getDomainClass());
		if(it.hasNext()){
			instance.domainObject = (Individual)it.next();
		} else {
			Guard.throwsArgumentException("rdfXml");
		}
		return instance;
	}
	
	protected static RDFInstance buildFromPlainXML(RDFSchema rdfSchema, String id, String plainXML, Map<String, ISchemaTypeFormat> typeFormats){
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		Guard.argumentNotNullOrEmptyString(id, "id");
		Guard.argumentNotNullOrEmptyString(plainXML, "plainXML");
		Guard.argumentNotNull(typeFormats, "typeFormats");
		
		Element element = XMLHelper.parseElement(plainXML);
		
		RDFInstance instance = new RDFInstance(rdfSchema, "uri:urn:"+id);
		
		Element fieldElement;
		String fieldValue;
		String dataTypeName;
		
		DatatypeProperty dataTypeProperty;
		ExtendedIterator it = rdfSchema.getOWLSchema().listDatatypeProperties();
		while(it.hasNext()){
			dataTypeProperty = (DatatypeProperty)it.next();

			dataTypeName = dataTypeProperty.getLocalName();
			
			fieldElement = element.element(dataTypeName);
			if(fieldElement != null){
				fieldValue = fieldElement.getText();
				
				OntResource range = dataTypeProperty.getRange();
				ISchemaTypeFormat format = typeFormats.get(range.getURI());
				if(format != null){
					try{
						instance.setProperty(dataTypeName, format.parseObject(fieldValue));
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				}else {
					RDFDatatype dataType = TypeMapper.getInstance().getTypeByName(range.getURI());
					if(dataType.isValid(fieldValue)){
						instance.setProperty(dataTypeName, dataType.cannonicalise(dataType.parse(fieldValue)));
					} else {
						LOGGER.info("RDF: invalid value. Property: " + dataTypeName + " Type: " + dataType.getURI() + " value: " + fieldValue);
					}
				}
			}
		}
		return instance;
	}
	
	private void initializeBaseModel(RDFSchema schema) {
		this.schema = schema;
		
        this.model = ModelFactory.createOntologyModel();
		model.add(schema.getRDFModel());
		model.setNsPrefix(schema.getOntologyNameSpace(), schema.getOntologyBaseUri());
		model.removeNsPrefix("rdfs");
	}

	public void setProperty(String propertyName, Object value) {
	
		String propertyUri = this.schema.getOntologyBaseUri()+ propertyName;
		Property domainObjectProperty = this.model.getProperty(propertyUri);
		if(this.domainObject.hasProperty(domainObjectProperty)){
			RDFNode oldValue = this.domainObject.getPropertyValue(domainObjectProperty);
			this.domainObject.removeProperty(domainObjectProperty, oldValue);
		}
		
		Object propertyValue = null;
		if(value instanceof Date){
			Calendar cal = Calendar.getInstance();
			cal.setTime((Date)value);
			propertyValue = cal;
		} else {
			propertyValue = value;
		}
		this.domainObject.addLiteral(domainObjectProperty, propertyValue);
			
	}

	public String asXML() {
		this.model.remove(this.schema.getRDFModel());
		
		StringWriter sw = new StringWriter();
		this.model.write(sw, "RDF/XML-ABBREV");
		
		this.model.add(this.schema.getRDFModel());
		return XMLHelper.canonicalizeXML(sw.toString());
	}

	public String asPlainXML() {
		return asPlainXML(ISchema.EMPTY_FORMATS);
	}
	
	public String asPlainXML(Map<String, ISchemaTypeFormat> typeFormats) {

		StringBuffer sb = new StringBuffer();

		sb.append("<");
		sb.append(this.schema.getOntologyClassName());
		sb.append(">");
	 
		
		StmtIterator it = this.domainObject.listProperties();
		Statement statement;
		while(it.hasNext()){
			statement = it.nextStatement();
			
			//Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();
			
			 if(!RDF.type.getURI().equals(predicate.getURI())){
	            String fieldName = predicate.getLocalName();
	            Literal literal = (Literal) object;
	            Object fieldValue = literal.getValue();
	            
	            ISchemaTypeFormat format = typeFormats.get(literal.getDatatypeURI());
	            if(format != null){
	            	if(fieldValue instanceof XSDDateTime){
	            		fieldValue = format.format(((XSDDateTime)fieldValue).asCalendar().getTime());
	            	} else {
	            		fieldValue = format.format(fieldValue);
	            	}
	            }
	            
           		writePlainXMLProperty(sb, fieldName, fieldValue);
			 }
		}
		
		sb.append("</");
		sb.append(this.schema.getOntologyClassName());
		sb.append(">");

		
		Element element = XMLHelper.parseElement(sb.toString());
		return XMLHelper.canonicalizeXML(element);
	}
	
	private void writePlainXMLProperty(StringBuffer sb, String fieldName, Object fieldValue){
		 sb.append("<");
		 sb.append(fieldName);
		 sb.append(">");
		 sb.append(fieldValue);
		 sb.append("</");
		 sb.append(fieldName);
		 sb.append(">");
	}

	public int getPropertyCount() {
		return this.model.listDatatypeProperties().toSet().size();
	}

	public Object getPropertyValue(String propertyName) {
		String propertyUri = this.schema.getOntologyBaseUri()+ propertyName;
		Property domainObjectProperty = this.model.getProperty(propertyUri);
		Literal literal = (Literal)this.domainObject.getPropertyValue(domainObjectProperty);
		if(literal == null){
			return null;
		} else {
			Object result = literal.getValue();
			if(result instanceof XSDDateTime){
				return ((XSDDateTime) result).asCalendar().getTime();
			} else {
				return result;
			}
		}
	}

	public String getPropertyName(int index) {
		DatatypeProperty domainObjectProperty = (DatatypeProperty)this.model.listDatatypeProperties().toList().get(index);
		return domainObjectProperty.getLocalName();
	}

	public String getPropertyType(String propertyName) {
		return this.schema.getPropertyType(propertyName);
	}

}
