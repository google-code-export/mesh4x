package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.LinkedHashMap;
import java.util.Map;

import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;

/**
 * @author Raju
 * @version 1.0 
 * @since 16/3/2009
 */
public class Storage {
	private Map<String,Object> contents = new LinkedHashMap<String,Object>();
	
	
	public Storage(String id,Object object){
		Guard.argumentNotNullOrEmptyString(id, "id");
		Guard.argumentNotNull(object, "object");
		contents.put(id, object);
	}
	public Storage(){
	}
	public Map<String,Object> getStorage(){
		return this.contents;
	}
	//returns the specific row with row number
	public Object getRow(String entityId){
		Guard.argumentNotNullOrEmptyString(entityId, "entityId");
		return contents.get(entityId);
	}
	//adding a new row to the storage system
	public void addRow(Object content){
		Guard.argumentNotNull(content, "content");
		contents.put(getId(content), content);
	}
	//update the specific row
	public void updateRow(String entityId,Object content){
		Guard.argumentNotNullOrEmptyString(entityId, "entityId");
		Guard.argumentNotNull(content, "content");
		deletRow(contents.get(entityId));
		addRow(entityId,content);
	}
	public void updateRow(Object content){
		Guard.argumentNotNull(content, "content");
		String id = getId(content);
		deletRow(contents.get(id));
		addRow(id,content);
	}
	//adding a new row to the storage system
	public void addRow(String entityId,Object content){
		Guard.argumentNotNull(content, "content");
		contents.put(entityId, content);
	}
	public void deletRow(Object content){
		Guard.argumentNotNull(content, "content");
		contents.remove(this.getId(content));	
	}
	
	private String getId(Object content){
		Guard.argumentNotNull(content, "content");
		String objId  = "";
		if(content instanceof IContent){
			objId = ((IContent) content).getId();
		}else if(content instanceof SyncInfo){
			objId = ((SyncInfo)content).getSyncId();
		}
		return objId;
	}
}
