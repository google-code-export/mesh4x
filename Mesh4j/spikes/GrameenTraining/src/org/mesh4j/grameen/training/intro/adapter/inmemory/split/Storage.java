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
	public void updateRow(Object content){
		Guard.argumentNotNull(content, "content");
		Object oldContent = contents.get(getId(content));
		oldContent = content;
	}
	public void deletRow(IContent content){
		Guard.argumentNotNull(content, "content");
		contents.remove(content.getId());	
	}
	
	private String getId(Object content){
		Guard.argumentNotNull(content, "content");
		String objId  = "";
		if(content instanceof IContent){
			objId = ((IContent) content).getId();
		}else if(content instanceof SyncInfo){
			objId = ((SyncInfo)content).getId();
		}
		return objId;
	}
}
