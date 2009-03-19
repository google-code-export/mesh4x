package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.HashMap;
import java.util.Map;

import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.model.IContent;

/**
 * 
 * @author Raju
 * @version 1.0 
 * @since 16/3/2009
 */
public class Storage {
	private Map<String,Object> contents = null;
	
	
	public Storage(Map<String,Object> contents){
		this.contents = contents;
	}
	public Map<String,Object> getStorage(){
		return this.contents;
	}
	//returns the specific row with row number
	public Object getRow(String entityId){
		return contents.get(entityId);
	}
	//adding a new row to the storage system
	public void addRow(Object content){
		contents.put(getId(content), content);
	}
	//update the specific row
	public void updateRow(Object content){
		Object oldContent = contents.get(getId(content));
		oldContent = content;
	}
	public void deletRow(IContent content){
		contents.remove(content.getId());	
	}
	
	private String getId(Object content){
		String objId  = "";
		if(content instanceof IContent){
			objId = ((IContent) content).getId();
		}else if(content instanceof SyncInfo){
			objId = ((SyncInfo)content).getId();
		}
		return objId;
	}
}
