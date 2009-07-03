package org.mesh4j.sync.payload.schema.rdf;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
			Guard.throwsArgumentException("ERROR_READING_RDFXML");
		}
		return instance;
	}
	
	protected static RDFInstance buildFromPlainXML(RDFSchema rdfSchema, String id, String plainXML, Map<String, ISchemaTypeFormat> typeFormats){
		return buildFromPlainXML(rdfSchema, id, plainXML, typeFormats, null);
	}
	
	protected static RDFInstance buildFromPlainXML(RDFSchema rdfSchema, String id, String plainXML, Map<String, ISchemaTypeFormat> typeFormats, String[] splitElements){
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		Guard.argumentNotNullOrEmptyString(id, "id");
		Guard.argumentNotNullOrEmptyString(plainXML, "plainXML");
		Guard.argumentNotNull(typeFormats, "typeFormats");
		
		Element element = XMLHelper.parseElement(plainXML);
		
		RDFInstance instance = new RDFInstance(rdfSchema, "uri:urn:"+id);
		
		Element fieldElement;
		String fieldValue;
		String dataTypeName;
		
		List<DatatypeProperty> domainProperties = rdfSchema.getDomainProperties();
		for (DatatypeProperty dataTypeProperty : domainProperties) {

			dataTypeName = dataTypeProperty.getLocalName();
			
			fieldElement = getFieldElement(dataTypeName, element, splitElements);
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
						instance.setProperty(dataTypeName, rdfSchema.cannonicaliseValue(dataTypeName, dataType.parse(fieldValue)));
					}
				}
			}
		}
		return instance;
	}
	
	private static Element getFieldElement(String dataTypeName, Element element, String[] splitElements) {
		Element fieldElement = element.element(dataTypeName);
		if(fieldElement == null && splitElements != null){
			for (String elementName : splitElements) {
				Element splitedElement = element.element(elementName);
				fieldElement = splitedElement.element(dataTypeName);
				if(fieldElement != null){
					return fieldElement;
				}
			}
		} 
		return fieldElement;
	}

	protected static RDFInstance buildFromProperties(RDFSchema rdfSchema, String id, Map<String, Object> propertyValues){
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		Guard.argumentNotNullOrEmptyString(id, "id");
		Guard.argumentNotNull(propertyValues, "propertyValues");
		
		if(propertyValues.isEmpty()){
			Guard.throwsArgumentException("propertyValues"); 
		}
		
		RDFInstance instance = new RDFInstance(rdfSchema, "uri:urn:"+id);
		
		Object fieldValue;
		String dataTypeName;
		
		List<DatatypeProperty> domainProperties = rdfSchema.getDomainProperties();
		for (DatatypeProperty dataTypeProperty : domainProperties) {

			dataTypeName = dataTypeProperty.getLocalName();
			
			fieldValue = propertyValues.get(dataTypeName);
			if(fieldValue != null){
				instance.setProperty(dataTypeName, rdfSchema.cannonicaliseValue(dataTypeName, fieldValue));
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
			cal.setTimeInMillis(((Date)value).getTime());
			propertyValue = cal;
		} else {
			propertyValue = value;
		}
		this.domainObject.addLiteral(domainObjectProperty, propertyValue);
			
	}

	public String asXML() {
		Element element = this.asElementXML();
		return XMLHelper.canonicalizeXML(element);
	}
	
	public Element asElementXML() {
		this.model.remove(this.schema.getRDFModel());
		
		StringWriter sw = new StringWriter();
		this.model.write(sw, "RDF/XML-ABBREV");
		
		this.model.add(this.schema.getRDFModel());
		
		String xml = sw.toString();
		Element element = XMLHelper.parseElement(xml);
		return XMLHelper.parseElement(XMLHelper.canonicalizeXML(element));
	}

	public String asPlainXML() {
		return asPlainXML(ISchema.EMPTY_FORMATS, null);
	}
	
	public String asPlainXML(Map<String, ISchemaTypeFormat> typeFormats) {
		return asPlainXML(typeFormats, null);
	}
	
	public String asPlainXML(Map<String, ISchemaTypeFormat> typeFormats, CompositeProperty[] compositeProperties) {

		StringBuffer sb = new StringBuffer();

		sb.append("<");
		sb.append(this.schema.getOntologyClassName());
		sb.append(">");
		
		String fieldName;
		Object fieldValue;
		
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		
		StmtIterator it = this.domainObject.listProperties();
		Statement statement;
		while(it.hasNext()){
			statement = it.nextStatement();
			
			//Resource subject = statement.getSubject();
			Property predicate = statement.getPredicate();
			RDFNode object = statement.getObject();
			
			 if(!RDF.type.getURI().equals(predicate.getURI())){
	            fieldName = predicate.getLocalName();
	            Literal literal = (Literal) object;
	            fieldValue = literal.getValue();
	            
	            ISchemaTypeFormat format = typeFormats.get(literal.getDatatypeURI());
	            if(format != null){
	            	if(fieldValue instanceof XSDDateTime){
	            		fieldValue = format.format(((XSDDateTime)fieldValue).asCalendar().getTime());
	            	} else {
	            		fieldValue = format.format(fieldValue);
	            	}
	            }
	            
	            CompositeProperty compositeProperty = getCompositeProperty(compositeProperties, fieldName);
	            if(compositeProperty == null){
	            	properties.put(fieldName, fieldValue);
	            } else {	            	
	            	compositeProperty.setPropertyValue(fieldName, fieldValue);
	            	properties.put(compositeProperty.getCompositeName(), compositeProperty);
	            }
			 }
		}
		
		for (String propName : properties.keySet()) {
			fieldValue = properties.get(propName);
			writePlainXMLProperty(sb, propName, fieldValue);
		}
		
		sb.append("</");
		sb.append(this.schema.getOntologyClassName());
		sb.append(">");

		
		Element element = XMLHelper.parseElement(sb.toString());
		return XMLHelper.canonicalizeXML(element);
	}
	
	private CompositeProperty getCompositeProperty(CompositeProperty[] compositeProperties, String fieldName) {
		if(compositeProperties == null){
			return null;
		}
		
		for (CompositeProperty compositeProperty : compositeProperties) {
			if(compositeProperty.containsPropery(fieldName)){
				return compositeProperty;
			}
		}
		return null;
	}

	private void writePlainXMLProperty(StringBuffer sb, String fieldName, Object fieldValue){
		 sb.append("<");
		 sb.append(fieldName);
		 sb.append(">");
		 
		 if(fieldValue instanceof CompositeProperty){
			 CompositeProperty compositeProperty = (CompositeProperty) fieldValue;
			 if(compositeProperty.isCompleted()){
				 for (String propName : compositeProperty.getPropertyNames()){
					 Object propValue = compositeProperty.getPropertyValue(propName);
					 writePlainXMLProperty(sb, propName, propValue);
				 }
			 }
		 } else {
			 sb.append(fieldValue);
		 }		 
		 
		 sb.append("</");
		 sb.append(fieldName);
		 sb.append(">");
	}

	public int getPropertyCount() {
		return this.getDomainProperties().size();
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
	
	public String getPropertyValueAsLexicalForm(String propertyName) {
		String propertyUri = this.schema.getOntologyBaseUri()+ propertyName;
		Property domainObjectProperty = this.model.getProperty(propertyUri);
		Literal literal = (Literal)this.domainObject.getPropertyValue(domainObjectProperty);
		if(literal == null){
			return null;
		} else {
			return literal.getLexicalForm();
		}
	}

	public String getPropertyName(int index) {
		DatatypeProperty domainObjectProperty = getDomainProperties().get(index);
		return domainObjectProperty.getLocalName();
	}

	public String getPropertyType(String propertyName) {
		return this.schema.getPropertyType(propertyName);
	}

	
	protected List<DatatypeProperty> getDomainProperties() {
		ArrayList<DatatypeProperty> domainProperties = new ArrayList<DatatypeProperty>();
		ExtendedIterator it = this.model.listDatatypeProperties();
		while(it.hasNext()){
			DatatypeProperty datatypeProperty = (DatatypeProperty)it.next();
			if(datatypeProperty.getDomain().equals(this.schema.getDomainClass())){
				domainProperties.add(datatypeProperty);
			}
		}
		return domainProperties;
	}

	public String getId() {
		String id = this.domainObject.getURI();
		if(id.startsWith("uri:urn:")){
			return id.substring("uri:urn:".length(), id.length());
		} else {
			return id;
		}
	}

	public String getPropertyLabel(String propertyName) {
		return this.schema.getPropertyLabel(propertyName);
	}
}
