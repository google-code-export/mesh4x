package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.HashMap;
import java.util.Map;

import org.mesh4j.sync.model.IContent;

/**
 * 
 * @author Raju
 * @version 1.0 
 * @since 16/3/2009
 */
public class Storage {
	private Map<String,IContent> contents = null;
	
	Storage(Map<String,IContent> contents){
		this.contents = contents;
	}
	public Map<String,IContent> getStorage(){
		return this.contents;
	}
	//returns the specific row with row number
	public IContent getRow(String entityId){
		return contents.get(entityId);
	}
	//adding a new row to the storage system
	public void addRow(IContent content){
		contents.put(content.getId(), content);
	}
	//update the specific row
	public void updateRow(IContent content){
		IContent oldContent = contents.get(content.getId());
		oldContent = content;
	}
	public void deletRow(IContent content){
		contents.remove(content.getId());	
	}
}
