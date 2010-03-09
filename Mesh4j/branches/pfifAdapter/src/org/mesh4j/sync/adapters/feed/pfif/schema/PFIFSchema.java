package org.mesh4j.sync.adapters.feed.pfif.schema;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public class PFIFSchema implements IPFIFSchema{

	
	public static final String PFIF_URL_1_1 ="http://zesty.ca/pfif/1.1";
	public static final String PFIF_URL_1_2 ="http://zesty.ca/pfif/1.2";
	
	public static final String PFIF_ULR = PFIF_URL_1_2;

	
	public static final String PFIF_PREFIX = "pfif";
	public static final Namespace PFIF_NS_1_2 = DocumentHelper.createNamespace(PFIF_PREFIX, PFIF_ULR);

	public static final Namespace NS = PFIF_NS_1_2;
	public static final String PFIF_ENTITY_PERSON_ID_NAME = "person_record_id";
	public static final String PFIF_ENTITY_NOTE_ID_NAME = "note_record_id";
	
	public static final QName QNAME_PERSON = DocumentHelper.createQName("person", NS);
	public static final QName QNAME_NOTE = DocumentHelper.createQName("note", NS);
	
	public static final QName QNAME_PERSON_RECORD_ID = DocumentHelper.createQName("person_record_id", NS);
	public static final QName QNAME_ENTRY_DATE = DocumentHelper.createQName("entry_date", NS);
	public static final QName QNAME_AUTHOR_NAME = DocumentHelper.createQName("author_name", NS);
	public static final QName QNAME_AUTHOR_EMAIL = DocumentHelper.createQName("author_email", NS);
	public static final QName QNAME_AUTHOR_PHONE = DocumentHelper.createQName("author_phone", NS);
	public static final QName QNAME_SOURCE_NAME = DocumentHelper.createQName("source_name", NS);
	public static final QName QNAME_SOURCE_DATE = DocumentHelper.createQName("source_date", NS);
	public static final QName QNAME_SOURCE_URL = DocumentHelper.createQName("source_url", NS);
	public static final QName QNAME_FIRST_NAME = DocumentHelper.createQName("first_name", NS);
	
	
	public static final QName QNAME_LAST_NAME = DocumentHelper.createQName("last_name", NS);
	public static final QName QNAME_HOME_CITY = DocumentHelper.createQName("home_city", NS);
	public static final QName QNAME_HOME_STATE = DocumentHelper.createQName("home_state", NS);
	public static final QName QNAME_HOME_NEIGHBORHOOD = DocumentHelper.createQName("home_neighborhood", NS);
	public static final QName QNAME_HOME_STREET = DocumentHelper.createQName("home_street", NS);
	public static final QName QNAME_HOME_ZIP = DocumentHelper.createQName("home_zip", NS);
	public static final QName QNAME_PHOTO_URL = DocumentHelper.createQName("photo_url", NS);
	public static final QName QNAME_OTHER = DocumentHelper.createQName("other", NS);
	
	public static final QName QNAME_NOTE_RECORD_ID = DocumentHelper.createQName("note_record_id", NS);
	public static final QName QNAME_FOUND = DocumentHelper.createQName("found", NS);
	public static final QName QNAME_EMAIL_OF_FOUND_PERSON = DocumentHelper.createQName("email_of_found_person", NS);
	public static final QName QNAME_PHONE_OF_FOUND_PERSON = DocumentHelper.createQName("phone_of_found_person", NS);
	public static final QName QNAME_LAST_KNOWN_LOCATION = DocumentHelper.createQName("last_known_location", NS);
	
	public static final QName QNAME_TEXT = DocumentHelper.createQName("text", NS);
	
	
	public static final QName QNAME_SEX = DocumentHelper.createQName("sex", NS);
	public static final QName QNAME_AGE = DocumentHelper.createQName("age", NS);
	public static final QName QNAME_HOME_POSTAL_CODE = DocumentHelper.createQName("home_postal_code", NS);
	public static final QName QNAME_HOME_COUNTRY = DocumentHelper.createQName("home_country", NS);
	public static final QName QNAME_LINKED_PERSON_RECORD_ID = DocumentHelper.createQName("home_country", NS);
	public static final QName QNAME_STATUS = DocumentHelper.createQName("status", NS);
	
	
	public PFIFSchema(){}
	
	@Override
	public List<QName> getAllFiled() {
		List<QName> list = new LinkedList<QName>();
		list.addAll(getBooleanFiledList());
		list.addAll(getIntegerFiledList());
		list.addAll(getStringFiledList());
		list.addAll(getDateTimeFieldList());
		return list;
	}

	@Override
	public List<QName> getFiledList(FIELD_TYPE field_type) {
		if(field_type.equals(FIELD_TYPE.BOOLEAN)){
			return getBooleanFiledList();
		} else if(field_type.equals(FIELD_TYPE.STRING)){
			return getStringFiledList();
		} else if(field_type.equals(FIELD_TYPE.INTEGER)){
			return getIntegerFiledList();
		} else if(field_type.equals(FIELD_TYPE.DATE_TIME)){
			return getDateTimeFieldList();
		}
		return null;
	}

	private static List<QName> getBooleanFiledList(){
		List<QName> list = new LinkedList<QName>();
		list.add(QNAME_FOUND);
		return list;
	}
	
	
	private static List<QName> getIntegerFiledList(){
		List<QName> list = new LinkedList<QName>();
		list.add(QNAME_AGE);
		return list;
	}
	
	private static List<QName> getStringFiledList(){
		List<QName> list = new LinkedList<QName>();
		list.add(QNAME_PERSON_RECORD_ID);
		list.add(QNAME_AUTHOR_NAME);
		list.add(QNAME_AUTHOR_EMAIL);
		
		list.add(QNAME_SEX);
		list.add(QNAME_HOME_POSTAL_CODE);
		list.add(QNAME_HOME_COUNTRY);
		list.add(QNAME_LINKED_PERSON_RECORD_ID);
		list.add(QNAME_STATUS);
		list.add(QNAME_TEXT);
		
		list.add(QNAME_NOTE_RECORD_ID);
		list.add(QNAME_EMAIL_OF_FOUND_PERSON);
		list.add(QNAME_PHONE_OF_FOUND_PERSON);
		list.add(QNAME_LAST_KNOWN_LOCATION);
		
		list.add(QNAME_LAST_NAME);
		list.add(QNAME_HOME_CITY);
		list.add(QNAME_HOME_STATE);
		list.add(QNAME_HOME_NEIGHBORHOOD);
		list.add(QNAME_HOME_STREET);
		list.add(QNAME_HOME_ZIP);
		list.add(QNAME_PHOTO_URL);
		list.add(QNAME_OTHER);
		
		list.add(QNAME_PERSON_RECORD_ID);
	
		list.add(QNAME_AUTHOR_NAME);
		list.add(QNAME_AUTHOR_EMAIL);
		list.add(QNAME_AUTHOR_PHONE);
		list.add(QNAME_SOURCE_NAME);
		
		list.add(QNAME_SOURCE_URL);
		list.add(QNAME_FIRST_NAME);
		
		
		return list;
	}
	
	private static List<QName> getDateTimeFieldList(){
		List<QName> list = new LinkedList<QName>();
		list.add(QNAME_ENTRY_DATE);;
		list.add(QNAME_SOURCE_DATE);
		return list;
	}
	
	
	public  FIELD_TYPE getType(Element pfifField){
	
		if(getStringFiledList().contains(pfifField.getQName())){
			return FIELD_TYPE.STRING;
			
		} else if(getDateTimeFieldList().contains(pfifField.getQName())){
			return FIELD_TYPE.DATE_TIME;
			
		} else if(getIntegerFiledList().contains(pfifField.getQName())){
			return FIELD_TYPE.INTEGER;
			
		} else if(getBooleanFiledList().contains(pfifField.getQName())){
			return FIELD_TYPE.BOOLEAN;
		}
		return null;
	}

	@Override
	public String getEntityId(PFIF_ENTITY pfif_entity) {
		if(pfif_entity == PFIF_ENTITY.PERSON){
			return PFIF_ENTITY_PERSON_ID_NAME;
		} else if(pfif_entity == PFIF_ENTITY.NOTE){
			return PFIF_ENTITY_NOTE_ID_NAME;
		}	
		return null;
	}

	@Override
	public List<PFIF_ENTITY> getSupportedEntityNames() {
		List<PFIF_ENTITY> list = new LinkedList<PFIF_ENTITY>();
		list.add(PFIF_ENTITY.PERSON);
		list.add(PFIF_ENTITY.NOTE);
		return list;
	}

	
	
	
}
