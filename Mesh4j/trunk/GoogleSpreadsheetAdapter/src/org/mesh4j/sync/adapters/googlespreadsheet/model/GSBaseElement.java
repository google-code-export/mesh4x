package org.mesh4j.sync.adapters.googlespreadsheet.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.IEntry;

/**
 * Provides an abstract class for various elements;
 * Subclass should provide Child Element type C
 * 
 * @author sharif
 * version 1.0, 30-03-09
 *
 */
public abstract class GSBaseElement<C> implements IGSElement<C>{
	
	protected boolean dirty = false; //flag represents element content changed
	protected boolean deleteCandidate = false; //flag represents this element is going to be deleted in next flush operation	
	protected BaseEntry<?> baseEntry; 
	
	@SuppressWarnings("unchecked")
	protected GSBaseElement parentElement;
	protected Map<String, C> childElements;
	
	protected int elementListIndex;	

	//Note:Google Spreadsheet's data row starts from it's 2nd row actually, 1st row contains the column headers. 
	
	public void setDeleteCandidate() {
		this.deleteCandidate = true;
	}
	
	public boolean isDeleteCandidate() {
		return this.deleteCandidate;
	}
	
	public void setDirty() {
		if( getParentElement() !=null )
			 parentElement.setDirty();
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
	}	
	
	/**
	 * refresh an element, 
	 * child element check can be enabled or disabled by the flag forcedChildCheck  
	 */
	@SuppressWarnings("unchecked")
	public void unsetDirty(boolean forcedChildCheck){
		//check if any of its child elements is dirty yet
		if(forcedChildCheck && childElements != null){
			for(C element: childElements.values()){
				if(((GSBaseElement)element).isDeleteCandidate())
					continue;
				else if (((GSBaseElement)element).isDirty()){					
					return;
				}
			}
		}
		
		this.dirty = false;
		
		if( getParentElement() !=null )
			 parentElement.unsetDirty();
		
	}
	
	/**
	 * refresh an element, force child element check true by default   
	 */
	public void unsetDirty(){
		unsetDirty(true);		
	}
	
	/**
	 * return the unique element id specified for that element in the spreadsheet 
	 *   
	 * @return
	 */
	public String getId(){
		if(((IEntry) this.baseEntry).getId() != null)
			return ((IEntry) this.baseEntry).getId();
		else return UUID.randomUUID().toString();
	}	

	/**
	 * return the unique element id specified for that element in the spreadsheet 
	 * 
	 * TODO: Need to review it later how to implement unique id for element
	 * each element will define its own id implementation  
	 * @return
	 */
	public String getElementId(){
		return Integer.toString(elementListIndex);
	}	
	
	public int getElementListIndex(){
		return this.elementListIndex;
	}	
	
	public BaseEntry<?> getBaseEntry(){
		return baseEntry;
	}

	public void setBaseEntry(BaseEntry<?> baseEntry){
		this.baseEntry = baseEntry;
	}
	
	@SuppressWarnings("unchecked")
	public GSBaseElement getParentElement(){
		return parentElement;
	}

	/** 
	 * return nondeleted child elements
	 */
	public Map<String, C> getChildElements() {
		return this.childElements;
	}

	/**
	 * returns a map of non-deleted child elements
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, C> getNonDeletedChildElements() {		
		Map<String, C> nonDeletedClildElements = new LinkedHashMap();

		for (String key : this.childElements.keySet()) {
			if (!((GSBaseElement) this.childElements.get(key))
					.isDeleteCandidate()) {
				nonDeletedClildElements.put(key, (C) this.childElements
						.get(key));
			}
		}

		return nonDeletedClildElements;
	}

	/**
	 * Note: this will set the element dirty 
	 */	
	public void addChildElement(String key, C element) {
		((IGSElement<?>) element).setDirty();
		this.childElements.put(key, element);
	}

	public void deleteChildElement(String key) {
		if(this.childElements.get(key) == null)  
			return;
			//throw new MeshException("Object not found with key: "+key);	 		
		((IGSElement<?>)this.childElements.get(key)).setDirty();

		if(this.childElements.get(key) instanceof GSCell) //TODO: cell is not deleted, only cell content is removed 
			((GSCell)this.childElements.get(key)).updateCellValue("");  
		else
			((IGSElement<?>)this.childElements.get(key)).setDeleteCandidate();
	}

	/**
	 * returns non deleted child element
	 * 
	 */
	@SuppressWarnings("unchecked")
	public C getChildElement(String key) {
		C child = this.childElements.get(key);
		return (child != null  && !((IGSElement)child).isDeleteCandidate()) ? child : null;
	}

	public void updateChildElement(String key, C element) {
		((IGSElement<?>)element).setDirty();
		this.childElements.put(key, element);
	}	
	
	/**
	 * this will remove the deleted entries in memory and update list index of remaining
	 */
	@SuppressWarnings("unchecked")
	public void refreshMe(){
		if (this.childElements == null || this.childElements.size() == 0)
			return;

		Map<String, C> nonDeletedClildElements = new LinkedHashMap();
		int listIndex = 1;
		
		for (String key : this.childElements.keySet()) {
			if (!((GSBaseElement) this.childElements.get(key))
					.isDeleteCandidate()) {
				
				GSBaseElement e = (GSBaseElement) this.childElements.get(key);
				e.elementListIndex = listIndex;
				
				if(e instanceof GSCell)
					nonDeletedClildElements.put(key, (C) e);
				else
					nonDeletedClildElements.put(e.getElementId(), (C) e);
				
				listIndex++;
			}
		}

		this.childElements.clear();	
		this.childElements.putAll(nonDeletedClildElements);
	}
}
