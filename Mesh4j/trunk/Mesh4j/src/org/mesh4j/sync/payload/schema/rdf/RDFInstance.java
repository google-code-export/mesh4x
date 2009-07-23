package org.mesh4j.sync.payload.schema.rdf;

import java.io.IOException;
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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;
import org.xml.sax.Attributes;

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
		
	protected static RDFInstance buildFromPlainXML(RDFSchema rdfSchema, String id, Element element, Map<String, ISchemaTypeFormat> typeFormats, String[] splitElements){
		Guard.argumentNotNull(rdfSchema, "rdfSchema");
		Guard.argumentNotNullOrEmptyString(id, "id");
		Guard.argumentNotNull(element, "element");
		Guard.argumentNotNull(typeFormats, "typeFormats");
		
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
				ISchemaTypeFormat format = typeFormats.get(dataTypeName);
				if(format == null){
					format = typeFormats.get(range.getURI());
				}
				
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
		model.setNsPrefix(schema.getOntologyNameSpace(), schema.getOntologyBaseClassUri());
		model.removeNsPrefix("rdfs");
	}

	public void setProperty(String propertyName, Object value) {
	
		String propertyUri = this.schema.getOntologyBaseClassUri()+ propertyName;
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

	public String asRDFXML() {
		this.model.remove(this.schema.getRDFModel());
		
		StringWriter sw = new StringWriter();
		this.model.write(sw, "RDF/XML-ABBREV");
		
		this.model.add(this.schema.getRDFModel());
		
		String xml = sw.toString();
		Element element = XMLHelper.parseElement(xml);
		
		return XMLHelper.canonicalizeXML(element);
	}
	
	public Element asElementRDFXML() {
		return XMLHelper.parseElement(asRDFXML());
	}
	
	public String asXML() {
		return asXML(ISchema.EMPTY_FORMATS, null);
	}
	
	public String asXML(Map<String, ISchemaTypeFormat> typeFormats) {
		return asXML(typeFormats, null);
	}
	
	public String asXML(Map<String, ISchemaTypeFormat> typeFormats, CompositeProperty[] compositeProperties) {
		Element element = asElementXml(typeFormats, compositeProperties);	
		return element.asXML();		
	}
	
	public Element asElementXml(Map<String, ISchemaTypeFormat> typeFormats, CompositeProperty[] compositeProperties) {
		return asElement(typeFormats, compositeProperties, true);
	}	

	public String asPlainXML() {
		return asPlainXML(ISchema.EMPTY_FORMATS, null);
	}
	
	public String asPlainXML(Map<String, ISchemaTypeFormat> typeFormats) {
		return asPlainXML(typeFormats, null);
	}
	
	public String asPlainXML(Map<String, ISchemaTypeFormat> typeFormats, CompositeProperty[] compositeProperties) {
		Element element = asElementPlainXml(typeFormats, compositeProperties);
		return element.asXML();
	}
	
	public Element asElementPlainXml(Map<String, ISchemaTypeFormat> typeFormats, CompositeProperty[] compositeProperties) {
		return asElement(typeFormats, compositeProperties, false);
	}
	
	private Element asElement(Map<String, ISchemaTypeFormat> typeFormats, CompositeProperty[] compositeProperties, boolean useNamespace) {
		try{
							
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
		            
		            ISchemaTypeFormat format = typeFormats.get(fieldName);
		            if(format == null){
		            	format = typeFormats.get(literal.getDatatypeURI());
		            }
		            
		            if(format != null){
		            	try{
			            	if(fieldValue instanceof XSDDateTime){
			            		fieldValue = format.format(((XSDDateTime)fieldValue).asCalendar().getTime());
			            	} else {
			            		fieldValue = format.format(fieldValue);
			            	}
		            	} catch (Exception e) {
							throw new MeshException(e);
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
			
			// write xml
					
			StringWriter sw = new StringWriter();
			XMLWriter xmlWriter = new XMLWriter(sw, OutputFormat.createCompactFormat());
			xmlWriter.write(DocumentHelper.createDocument());
			
			final Namespace nameSpace = DocumentHelper.createNamespace(this.schema.getOntologyNameSpace(), this.schema.getOntologyBaseRDFUrl());
			Element element = null;
			if(useNamespace){
				Attributes attr = new Attributes(){
					@Override public int getIndex(String name) {return 0;}
					@Override public int getIndex(String uri, String localName) {return 0;}
					@Override public int getLength() {return 1;}
					@Override public String getLocalName(int index) {return null;}
					@Override public String getQName(int index) {return "xmlns:"+nameSpace.getPrefix();}
					@Override public String getType(int index) {return null;}
					@Override public String getType(String name) {return null;}
					@Override public String getType(String uri, String localName) {return null;}
					@Override public String getURI(int index) {return null;}
					@Override public String getValue(int index) {return nameSpace.getURI();}
					@Override public String getValue(String name) {return null;}
					@Override public String getValue(String uri, String localName) {return null;}				
				};
				
				xmlWriter.startElement("", "", "root", attr);
			
				QName elementQName = DocumentHelper.createQName(this.schema.getOntologyClassName(), nameSpace);
				element = DocumentHelper.createElement(elementQName);
			}else{
				Attributes attrEmpty = new Attributes(){
					@Override public int getIndex(String name) {return 0;}
					@Override public int getIndex(String uri, String localName) {return 0;}
					@Override public int getLength() {return 0;}
					@Override public String getLocalName(int index) {return null;}
					@Override public String getQName(int index) {return null;}
					@Override public String getType(int index) {return null;}
					@Override public String getType(String name) {return null;}
					@Override public String getType(String uri, String localName) {return null;}
					@Override public String getURI(int index) {return null;}
					@Override public String getValue(int index) {return null;}
					@Override public String getValue(String name) {return null;}
					@Override public String getValue(String uri, String localName) {return null;}				
				};
				
				xmlWriter.startElement("", "", "root", attrEmpty);
				element = DocumentHelper.createElement(this.schema.getOntologyClassName());
			}
			
			xmlWriter.writeOpen(element);

			for (String propName : properties.keySet()) {
				fieldValue = properties.get(propName);
				writePlainXMLProperty(xmlWriter, nameSpace, propName, fieldValue, useNamespace);
			}
			
			xmlWriter.writeClose(element);
			xmlWriter.endElement("", "", "root");
			xmlWriter.flush();
			xmlWriter.close();
			
			String xml = sw.toString();
			
			Element elementPlainXml = (Element)DocumentHelper.parseText(xml).getRootElement().elements().get(0);
			String canonicalizeXml = XMLHelper.canonicalizeXML(elementPlainXml);
			return XMLHelper.parseElement(canonicalizeXml);
		}catch (Exception e) {
			throw new MeshException(e);
		}
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

	private void writePlainXMLProperty(XMLWriter xmlWriter, Namespace nameSpace, String fieldName, Object fieldValue, boolean useNamespace) throws IOException{
		
		Element element = null;
		if(useNamespace){
			element = DocumentHelper.createElement(DocumentHelper.createQName(fieldName, nameSpace));
		} else {
			element = DocumentHelper.createElement(fieldName);
		}
		
		xmlWriter.writeOpen(element);
		 
		if(fieldValue instanceof CompositeProperty){
			 CompositeProperty compositeProperty = (CompositeProperty) fieldValue;
			 if(compositeProperty.isCompleted()){
				 for (String propName : compositeProperty.getPropertyNames()){
					 Object propValue = compositeProperty.getPropertyValue(propName);
					 writePlainXMLProperty(xmlWriter, nameSpace, propName, propValue, useNamespace);
				 }
			 }
		 } else {
			 xmlWriter.write(fieldValue.toString());
		 }		 
		 
		 xmlWriter.writeClose(element);
	}

	public int getPropertyCount() {
		return this.getDomainProperties().size();
	}

	public Object getPropertyValue(String propertyName) {
		String propertyUri = this.schema.getOntologyBaseClassUri()+ propertyName;
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
		String propertyUri = this.schema.getOntologyBaseClassUri()+ propertyName;
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
		
		List<DatatypeProperty> domainProperties = new ArrayList<DatatypeProperty>();
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
