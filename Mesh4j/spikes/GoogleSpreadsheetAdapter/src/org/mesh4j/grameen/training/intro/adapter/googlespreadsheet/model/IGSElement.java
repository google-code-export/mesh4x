package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.Map;

import com.google.gdata.data.BaseEntry;

/**
 * Provides an interface for various elements;
 * Implementation should provide Child Element type C
 *    
 * @author sharif
 * version 1.0, 30-03-09
 *
 */
public interface IGSElement<C> {
	boolean isDirty();
	boolean isDeleteCandidate();
	void setDirty();
	void setDeleteCandidate();	
	
	void unsetDirty();
	void unsetDirty(boolean forcedChildCheck);
	
	String getId();
	int getElementListIndex();
	
	@SuppressWarnings("unchecked")
	GSBaseElement getParentElement();
	Map<String, C> getChildElements(); 
	BaseEntry<?> getBaseEntry(); 	
		
	C getChildElement(String key);	
	void updateChildElement(String key, C element);
	void addChildElement(String key, C element);
	void deleteChildElement(String key);
	
}
