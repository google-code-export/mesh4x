package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.List;

/**
 * @author sharif
 * version 1.0, 30-03-09
 *
 */
public interface IGSElement {
	boolean isDirty();
	boolean isDeleteCandiddate();
	void setDirty();
	void setDeleteCandidate();
	
	String getId();
	IGSElement getParent();
	List <IGSElement> getChilds();	
	
	IGSElement getChildEntry(String key);	
	void updateChildEntry(String key, IGSElement element);
	void addChildEntry(IGSElement element);
	void deleteChildEntry(String key);
	
}
