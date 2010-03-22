package org.mesh4j.sync.adapters.feed.pfif.mapping;

import static org.mesh4j.sync.adapters.feed.ISyndicationFormat.ELEMENT_PAYLOAD;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.pfif.PfifUtil;
import org.mesh4j.sync.adapters.feed.pfif.model.PfifModel;
import org.mesh4j.sync.adapters.feed.pfif.schema.FIELD_TYPE;
import org.mesh4j.sync.adapters.feed.pfif.schema.IPfifSchema;
import org.mesh4j.sync.adapters.feed.pfif.schema.PfifSchema;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIF_ENTITY;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class PfifToRdfMapping extends AbstractRDFIdentifiableMapping implements IPfifToPlainXmlMapping{

	private IPfifSchema pfifSchema;
	private static final String  UTC_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	
	//TODO (raju) add syndicationformat into IPfifSchema,because IPfifSchema should know what feed(rss/atom)
	//it represents
	public PfifToRdfMapping(String pfifFile,ISyndicationFormat syndicationFormat,
			IRDFSchema rdfSchema,IPfifSchema pfifSchema) {
		super(rdfSchema);
	
		Guard.argumentNotNullOrEmptyString(pfifFile, "pfifFile");
		this.pfifSchema = pfifSchema;
	}
	
	
	public static RDFSchema  extractRDFSchema(String fileName,ISyndicationFormat syndicationFormat,String entityName,
							String[] identifiablePropertyNames,String lastUpdateColumnName, 
							String rdfURL,IPfifSchema pfifSchema) {
		
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(syndicationFormat, "syndicationFormat");
		Guard.argumentNotNullOrEmptyString(entityName, "entityName");
//		Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		Guard.argumentNotNullOrEmptyString(rdfURL, "rdfURL");
		Guard.argumentNotNull(pfifSchema, "pfifSchema");
		
		List<PfifModel> models = null;
		try {
			models = PfifUtil.getOrCreatePersonAndNoteFileIfNecessary(fileName, syndicationFormat);
		} catch (IOException e) {
			throw new MeshException(e);
		}
		
		Feed feed = null;;
		
		for(PfifModel model : models){
			if(model.getEntityName().equals(entityName)){
				feed = model.getFeed();
				break;
			}
		}
		
		
		for(Item item :feed.getItems()){
			Element contentElement = item.getContent().getPayload();
			Element payload = null;
			if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(contentElement.getName())){
				payload = contentElement;
			} else {
				payload = DocumentHelper.createElement(ELEMENT_PAYLOAD);
				payload.add(contentElement.detach());
			}
			List<Element> elementList = payload.elements();
			for(Element element : elementList){
				
				RDFSchema schema = getSchema(element,entityName,rdfURL,pfifSchema);
				//TODO there are lot of person or note element can be one pfif xml file.
				//we are extracting schema from one person or note element and ignoring others
				//we need to think about it a bit detail, should we consider extracting all node ?
				if(schema != null){
					return schema;
				}
			}
		}
		return null;
	}

	
	private static RDFSchema getSchema(Element element,String entityName,String rdfURL,IPfifSchema pfifSchema){
		if(entityName.equals("person") && 
				isPersonElement(element)){
			
			return extractRDF(element,entityName,pfifSchema.getEntityId(PFIF_ENTITY.PERSON),rdfURL,pfifSchema);
		} else if(entityName.equals("note") && 
				isNoteElement(element)){
			return extractRDF(element,entityName,pfifSchema.getEntityId(PFIF_ENTITY.NOTE),rdfURL,pfifSchema);
		}
		return null;
	}
	

	private static boolean isPersonElement(Element element){
		return element.getName().equals(PfifSchema.QNAME_PERSON.getName());
	}
	
	private static boolean isNoteElement(Element element){
		return element.getName().equals(PfifSchema.QNAME_NOTE.getName());
	}
	
	private static RDFSchema extractRDF(Element element,String entityName,String entityId,String rdfURL,IPfifSchema pfifSchema){
		
		String propertyName = "";
		RDFSchema rdfSchema = new RDFSchema(entityName, rdfURL +"/"+entityName+"#", entityName);
		
		List<Element> elementList = element.elements();
		for(Element field : elementList){
			propertyName = RDFSchema.normalizePropertyName(field.getName());
			
			if(isString(pfifSchema.getType(field))){
				rdfSchema.addStringProperty(propertyName, field.getName(), IRDFSchema.DEFAULT_LANGUAGE);	
				
			} else if(isDateTime(pfifSchema.getType(field))){
				rdfSchema.addDateTimeProperty(propertyName, field.getName(), IRDFSchema.DEFAULT_LANGUAGE);
				
			} else if(isInteger(pfifSchema.getType(field))){
				rdfSchema.addIntegerProperty(propertyName, field.getName(), IRDFSchema.DEFAULT_LANGUAGE);
				
			} else if(isBoolean(pfifSchema.getType(field))){
				rdfSchema.addBooleanProperty(propertyName, field.getName(), IRDFSchema.DEFAULT_LANGUAGE);
			}
			//System.out.println(filed.getName() + " = " + filed.getData());
		}
		List<String> list = new LinkedList<String>();
		list.add(entityId);
		rdfSchema.setIdentifiablePropertyNames(list);
		return rdfSchema;
	}
	
	
	private static boolean isString(FIELD_TYPE field_type){
		return field_type == FIELD_TYPE.STRING;
	}
	
	private static  boolean isDateTime(FIELD_TYPE field_type){
		return field_type == FIELD_TYPE.DATE_TIME;
	}
	
	private static  boolean isBoolean(FIELD_TYPE field_type){
		return field_type == FIELD_TYPE.BOOLEAN;
	}
	
	private static  boolean isInteger(FIELD_TYPE field_type){
		return field_type == FIELD_TYPE.INTEGER;
	}


	@Override
	public Element convertPfifToXML(Element pfifPayload) {
		
		String propertyName;
		Object propertyValue = null;
		
		String id = rdfSchema.getIdentifiablePropertyNames().get(0);
		
		
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		List<Element> elementList = pfifPayload.elements();
		for(Element field : elementList){
			propertyName = RDFSchema.normalizePropertyName(field.getName());
			propertyValue = field.getData();
			if(IRDFSchema.XLS_DATETIME.equals(rdfSchema.getPropertyType(propertyName))){
				Date date = DateHelper.parseDate(propertyValue.toString(), UTC_DATE_TIME_FORMAT);
				propertyValue = date;
			}
			propertyValue = rdfSchema.cannonicaliseValue(propertyName, propertyValue);
			if(propertyValue != null){
				propertyValues.put(propertyName, propertyValue);
			}
		}
		
		String idValue = propertyValues.get(id).toString();
		RDFInstance rdfInstance = rdfSchema.createNewInstanceFromProperties(idValue, propertyValues);
		return rdfInstance.asElementRDFXML();
	}


	@Override
	public Element convertXMLToPfif(Element rdfPayload) {
		
		Element element = null;
		if(this.rdfSchema.getOntologyClassName().equals("person")){
			element = DocumentHelper.createElement(PfifSchema.QNAME_PERSON);
		} else  if(this.rdfSchema.getOntologyClassName().equals("note")){
			element = DocumentHelper.createElement(PfifSchema.QNAME_NOTE);
		}
		
		RDFInstance rdfInstance = rdfSchema.createNewInstanceFromRDFXML(rdfPayload);
		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			Object propertyValue = rdfInstance.getPropertyValue(propertyName);
			if(propertyValue != null){
				if(IRDFSchema.XLS_DATETIME.equals(rdfSchema.getPropertyType(propertyName))){
					//convert into normal date time 
					if(propertyValue instanceof Date){
						SimpleDateFormat format = new SimpleDateFormat(UTC_DATE_TIME_FORMAT);
						propertyValue = format.format((Date)propertyValue);
					}
				}
				element.add(createElementByName(propertyName,propertyValue));
			}
		}
		return element;
	}
	
	 private Element createElementByName(String name,Object value){
			
			QName qName = DocumentHelper.createQName(name, DocumentHelper.createNamespace("pfif", PfifSchema.PFIF_ULR));
			Element field = null;
			
			if(pfifSchema.getAllFiled().contains(qName)){
				field =  DocumentHelper.createElement(qName);
				field.setText(value.toString());
				return field;
			}
			//TODO implement type specific pattern
//			if(pfifSchema.getStringFiledList().contains(qName)){
//				field =  DocumentHelper.createElement(qName);
//				field.setText(value.toString());
//				return field;
//			} else if(getIntegerFiledList().contains(qName)){
//				field =  DocumentHelper.createElement(qName);
//				field.setText(value.toString());
//				return field;
//			} else if(getBooleanFiledList().contains(qName)){
//				field =  DocumentHelper.createElement(qName);
//				field.setText(value.toString());
//				return field;
//			} else if(getDateTimeFieldList().contains(qName)){
//				field =  DocumentHelper.createElement(qName);
//				field.setText(value.toString());
//				return field;
//			}
			
			return null;
		}
	
}
