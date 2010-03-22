package org.mesh4j.sync.adapters.feed.pfif.schema;

import java.util.List;

import org.dom4j.Element;
import org.dom4j.QName;

public interface IPfifSchema {

	public  List<QName> getAllFiled(); 
	public  List<QName> getFiledList(FIELD_TYPE field_type);
	
	public String getEntityId(PFIF_ENTITY pfif_entity);
	public List<PFIF_ENTITY> getSupportedEntityNames();
	public  FIELD_TYPE getType(Element pfifField);
	
}
