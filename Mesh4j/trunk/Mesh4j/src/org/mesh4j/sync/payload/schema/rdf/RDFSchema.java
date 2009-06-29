package org.mesh4j.sync.payload.schema.rdf;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;
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

	// CONSTANTs
	private final static String MESH_METADATA_CLASS_NAME = "mesh4xMetadata";
	private final static String MESH_METADATA_IDENTIFIABLE_PROPERTY_NAME = "identifiableProperties";
	private final static String MESH_METADATA_VERSION_PROPERTY_NAME = "versionProperty";
	private final static String MESH_METADATA_GUID_PROPERTY_NAME = "guidProperties";
	
	// MODEL VARIABLES
	private String ontologyBaseUri;
	private String ontologyNameSpace;
	private String ontologyClassName;
	
	private OntModel schema;
	private OntClass domainClass;
	private OntClass meshMetadataClass;
		
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
		while(it.hasNext()){
			OntClass ontClass = (OntClass)it.next();
			if(MESH_METADATA_CLASS_NAME.equals(ontClass.getLocalName())){
				if(this.meshMetadataClass != null){
					Guard.throwsArgumentException("reader");
				}
				this.meshMetadataClass = ontClass;
			} else {
				if(this.domainClass != null){
					Guard.throwsArgumentException("reader");
				}
				this.domainClass = ontClass;
			}
		}
			
		if(this.domainClass == null){
			Guard.throwsArgumentException("reader");			
		}

		String[] uri = this.domainClass.getURI().split("#");
		this.ontologyBaseUri = uri[0]+"#";
		this.ontologyClassName = this.domainClass.getLocalName();
		this.ontologyNameSpace = uri[1];
		this.schema.setNsPrefix(this.ontologyNameSpace, this.ontologyBaseUri);
		
		if(this.meshMetadataClass == null){
			String meshNameUri = this.ontologyBaseUri + MESH_METADATA_CLASS_NAME;
			this.meshMetadataClass = schema.createClass(meshNameUri);
		}
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
		
		String meshNameUri = this.ontologyBaseUri + MESH_METADATA_CLASS_NAME;
		this.meshMetadataClass = schema.createClass(meshNameUri);
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
	
	public void addFloatProperty(String propertyName, String label, String lang) {
		this.addProperty(propertyName, label, lang, XSD.xfloat);		
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
		String xml = sw.toString();
		return XMLHelper.canonicalizeXML(xml);
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
		return this.getDomainProperties().size();
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
			
			if(IRDFSchema.XLS_DOUBLE.equals(range.getURI())){
				if(value instanceof Double){
					return value;
				} else {
					if(value instanceof Number){
						Number number = (Number) value;
						return new Double(number.doubleValue());
					} else if(value instanceof String){
						String valueAsString = (String) value;
						if(dataType.isValid(valueAsString)){
							return dataType.parse((String)value);
						} 
					}
				}
			} else if(IRDFSchema.XLS_FLOAT.equals(range.getURI())){
				if(value instanceof Float){
					return value;
				} else {
					if(value instanceof Number){
						Number number = (Number) value;
						return new Float(number.floatValue());
					} else if(value instanceof String){
						String valueAsString = (String) value;
						if(dataType.isValid(valueAsString)){
							return dataType.parse((String)value);
						} 
					}
				}
			} else if(IRDFSchema.XLS_DECIMAL.equals(range.getURI())){
				if(value instanceof BigDecimal){
					return value;
				} else {
					if(value instanceof Number){
						return new BigDecimal(value.toString());
					} else if(value instanceof String){
						String valueAsString = (String) value;
						if(dataType.isValid(valueAsString)){
							return dataType.parse((String)value);
						}
					}
				}
			} else if(IRDFSchema.XLS_LONG.equals(range.getURI())){
				if(value instanceof Long){
					return value;
				} else {
					if(value instanceof Number){
						return new Long(value.toString());
					} else if(value instanceof String){
						String valueAsString = (String) value;
						if(dataType.isValid(valueAsString)){
							return dataType.parse((String)value);
						}
					}
				}
			} else if(IRDFSchema.XLS_INTEGER.equals(range.getURI())){
				if(value instanceof Integer){
					return value;
				} else {
					if(value instanceof Number){
						return new Integer(value.toString());
					} else if(value instanceof String){
						String valueAsString = (String) value;
						if(dataType.isValid(valueAsString)){
							return dataType.parse((String)value);
						}
					}
				}
			}else {
				return dataType.cannonicalise(value);
			}
			return null;
		}
	}

	public String getPropertyName(int index) {	
		List<DatatypeProperty> items = this.getDomainProperties();
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
	public RDFInstance createNewInstanceFromPlainXML(String id, String plainXML, Map<String, ISchemaTypeFormat> formatters, String[] splitElements){
		return RDFInstance.buildFromPlainXML(this, id, plainXML, formatters, splitElements);
	}
	
	@Override
	public RDFInstance createNewInstanceFromProperties(String id, Map<String, Object> propertyValues){
		return RDFInstance.buildFromProperties(this, id, propertyValues);
	}
	
	@Override
	public boolean isCompatible(ISchema schema){
		
		if (this == schema) {
			return true;
		}
		
		if (schema == null || !(schema instanceof IRDFSchema)) {
			return false;
		}

		if (this.asXML().equalsIgnoreCase(schema.asXML())) {
			return true;
		}
		
		IRDFSchema rdfSchema = (IRDFSchema) schema;
		int size = rdfSchema.getPropertyCount();
		
		if(size != this.getPropertyCount()){
			return false;
		}
		
		if(!this.getOntologyBaseUri().equals(rdfSchema.getOntologyBaseUri())){
			return false;
		}

		if(!this.getOntologyClassName().equals(rdfSchema.getOntologyClassName())){
			return false;
		}
		
		if(!this.getOntologyNameSpace().equals(rdfSchema.getOntologyNameSpace())){
			return false;
		}
		
		for (int i = 0; i < size; i++) {
			String propName = rdfSchema.getPropertyName(i);
			String propTypeThis = this.getPropertyType(propName);
			String propTypeThat = rdfSchema.getPropertyType(propName);
			
			if( propTypeThat == null || propTypeThat.isEmpty() ) {
				//there is no property with the given name
				return false;
			} else {
				if(propTypeThat.equalsIgnoreCase(propTypeThis)){
					//this covers equality for String, Boolean and Date type property
					continue;
				}else{	//covers all numeric property
					
					//TODO (Sharif,jmt): need add the following enhancement here for numeric property type.
					// Provide adapter specific comparison;
					// say for rdf from similar type adapter it will match by one2one
					// (i.e., int2int, long2long, float2float, double2double etc)
					// but for different adapters the match might be
					// flexible to some extent (say int/long 2 int/long, float/double 2 float/double etc).
					// currently the flexibility is maximum (int/long/float/double 2 int/long/float/double) 
					
					if(IRDFSchema.XLS_INTEGER.equals(propTypeThis) 
							|| IRDFSchema.XLS_LONG.equals(propTypeThis)
							|| IRDFSchema.XLS_DOUBLE.equals(propTypeThis)
							|| IRDFSchema.XLS_DECIMAL.equals(propTypeThis)
							|| IRDFSchema.XLS_FLOAT.equals(propTypeThis)){
						
						if (!(IRDFSchema.XLS_INTEGER.equals(propTypeThat) 
								|| IRDFSchema.XLS_LONG.equals(propTypeThat)
								|| IRDFSchema.XLS_DOUBLE.equals(propTypeThat)
								|| IRDFSchema.XLS_DECIMAL.equals(propTypeThat)
								|| IRDFSchema.XLS_FLOAT.equals(propTypeThat))) return false;
					}else{
						//incompatible!
						return false;
					}
				}
			}			
		}			
		
		List<String> localIds = this.getIdentifiablePropertyNames();
		List<String> otherIds = rdfSchema.getIdentifiablePropertyNames();
		if(localIds.size() != otherIds.size()){
			return false;
		}
		
		for (int i = 0; i < localIds.size(); i++) {
			String idName = localIds.get(i);
			String otherIdName = otherIds.get(i);
			if(!idName.equals(otherIdName)){
				return false;
			}
		}		
		return true;
	}

	@Override
	public Map<String, String> getPropertiesAsLexicalFormMap(Element element) {
		HashMap<String, String> result = new HashMap<String, String>();
		RDFInstance instance = createNewInstanceFromRDFXML(element.asXML());
		if(instance != null){
			int size = this.getPropertyCount();
			for (int i = 0; i < size; i++) {
				String propertyName = this.getPropertyName(i);
				String propertyValue = instance.getPropertyValueAsLexicalForm(propertyName);
				result.put(propertyName, propertyValue);

//				if(propertyValue instanceof Date){
//					context.put(propertyName, DateHelper.formatW3CDateTime((Date)propertyValue));
//				} else {
//					context.put(propertyName, propertyValue);
//				}
			}
		}
		return result;
	}
	
	
	@Override
	public Map<String, Object> getPropertiesAsMap(Element element) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		RDFInstance instance = createNewInstanceFromRDFXML(element.asXML());
		if(instance != null){
			int size = this.getPropertyCount();
			for (int i = 0; i < size; i++) {
				String propertyName = this.getPropertyName(i);
				Object propertyValue = instance.getPropertyValue(propertyName);
				result.put(propertyName, propertyValue);
			}
		}
		return result;
	}

	public static boolean isRDF(Element schemaElement) {
		return schemaElement != null && schemaElement.getName().trim().toLowerCase().equals("rdf");
	}

	public static RDFSchema readSchema(String fileName) throws Exception {
		FileReader reader = new FileReader(fileName);
		try{
			RDFSchema rdfSchema = new RDFSchema(reader);
			return rdfSchema;
		} finally{
			reader.close();
		}
	}

	@Override
	public String getName() {
		return this.ontologyClassName;
	}

	public void setIdentifiablePropertyName(String propertyName) {
		ArrayList<String> identifiablePropertyNames = new ArrayList<String>();
		identifiablePropertyNames.add(propertyName);
		setIdentifiablePropertyNames(identifiablePropertyNames);		
	}
	
	public void setIdentifiablePropertyNames(List<String> identifiablePropertyNames) {
		StringBuffer sb = new StringBuffer();
		
		for (int j = 0; j < identifiablePropertyNames.size(); j++) {
			sb.append(identifiablePropertyNames.get(j));
			if(j < identifiablePropertyNames.size() -1){
				sb.append(",");	
			}
		}		
		
		String[] uri = this.meshMetadataClass.getURI().split("#");
		String propertyUri = uri[0] + "/" + MESH_METADATA_CLASS_NAME + "#"+ MESH_METADATA_IDENTIFIABLE_PROPERTY_NAME;
		DatatypeProperty domainProperty = this.schema.createDatatypeProperty(propertyUri);
		
		domainProperty.addDomain(this.meshMetadataClass);
		domainProperty.addRange(XSD.xstring);
		domainProperty.addComment(sb.toString(), "en");

	}
	
	public void setVersionPropertyName(String versionPropertyName) {
		if(versionPropertyName != null){
			String[] uri = this.meshMetadataClass.getURI().split("#");
			String propertyUri = uri[0] + "/" + MESH_METADATA_CLASS_NAME + "#"+ MESH_METADATA_VERSION_PROPERTY_NAME;
			DatatypeProperty domainProperty = this.schema.createDatatypeProperty(propertyUri);
			
			domainProperty.addDomain(this.meshMetadataClass);
			domainProperty.addRange(XSD.xstring);
			domainProperty.addComment(versionPropertyName, "en");
		}
	}
	
	public void setGUIDPropertyNames(List<String> guidPropertyNames) {
		StringBuffer sb = new StringBuffer();
		
		for (int j = 0; j < guidPropertyNames.size(); j++) {
			sb.append(guidPropertyNames.get(j));
			if(j < guidPropertyNames.size() -1){
				sb.append(",");	
			}
		}		
		
		String[] uri = this.meshMetadataClass.getURI().split("#");
		String propertyUri = uri[0] + "/" + MESH_METADATA_CLASS_NAME + "#"+ MESH_METADATA_GUID_PROPERTY_NAME;
		DatatypeProperty domainProperty = this.schema.createDatatypeProperty(propertyUri);
		
		domainProperty.addDomain(this.meshMetadataClass);
		domainProperty.addRange(XSD.xstring);
		domainProperty.addComment(sb.toString(), "en");

	}

	@Override
	public List<String> getIdentifiablePropertyNames() {
		ExtendedIterator it = this.schema.listDatatypeProperties();
		while(it.hasNext()){
			DatatypeProperty datatypeProperty = (DatatypeProperty)it.next();
			if(datatypeProperty.getDomain().equals(this.meshMetadataClass) && datatypeProperty.getLocalName().equals(MESH_METADATA_IDENTIFIABLE_PROPERTY_NAME)){
				String comment = datatypeProperty.getComment("en");
				String[] pks = comment.split(",");
				return Arrays.asList(pks);
			}
		}
		return new ArrayList<String>();
	}
	
	public List<String> getGUIDPropertyNames() {
		ExtendedIterator it = this.schema.listDatatypeProperties();
		while(it.hasNext()){
			DatatypeProperty datatypeProperty = (DatatypeProperty)it.next();
			if(datatypeProperty.getDomain().equals(this.meshMetadataClass) && datatypeProperty.getLocalName().equals(MESH_METADATA_GUID_PROPERTY_NAME)){
				String comment = datatypeProperty.getComment("en");
				String[] pks = comment.split(",");
				return Arrays.asList(pks);
			}
		}
		return new ArrayList<String>();
	}
	
	@Override
	public String getVersionPropertyName() {
		ExtendedIterator it = this.schema.listDatatypeProperties();
		while(it.hasNext()){
			DatatypeProperty datatypeProperty = (DatatypeProperty)it.next();
			if(datatypeProperty.getDomain().equals(this.meshMetadataClass) && datatypeProperty.getLocalName().equals(MESH_METADATA_VERSION_PROPERTY_NAME)){
				String comment = datatypeProperty.getComment("en");
				return comment;
			}
		}
		return null;
	}
	
	protected List<DatatypeProperty> getDomainProperties() {
		ArrayList<DatatypeProperty> domainProperties = new ArrayList<DatatypeProperty>();
		ExtendedIterator it = this.schema.listDatatypeProperties();
		while(it.hasNext()){
			DatatypeProperty datatypeProperty = (DatatypeProperty)it.next();
			if(datatypeProperty.getDomain().equals(this.domainClass)){
				domainProperties.add(datatypeProperty);
			}
		}
		return domainProperties;
	}

	@Override
	public boolean isGUID(String propertyName) {
		return getGUIDPropertyNames().contains(propertyName);
	}

	@Override
	public String getBaseRDFURL() {
		return this.ontologyBaseUri.substring(0, this.ontologyBaseUri.length() - (this.ontologyClassName.length() +2));
		// + 2 because add 1 for / and 1 for #
	}

	@Override
	public boolean hasCompositeId() {
		return this.getIdentifiablePropertyNames().size() > 1;
	}

}
