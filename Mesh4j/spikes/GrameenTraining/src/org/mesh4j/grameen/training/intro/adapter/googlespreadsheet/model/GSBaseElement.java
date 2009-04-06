package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model;

import java.util.Map;
import java.util.UUID;

import org.mesh4j.sync.validations.MeshException;

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
	
	public boolean isDeleteCandiddate() {
		return this.deleteCandidate;
	}
	
	public void setDirty() {
		if( getParentElement() !=null )
			((IGSElement<?>) parentElement).setDirty();
		this.dirty = true;
	}

	public boolean isDirty() {
		return this.dirty;
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

	@SuppressWarnings("unchecked")
	public GSBaseElement getParentElement(){
		return parentElement;
	}

	public Map<String, C> getChildElements() {
		return this.childElements;
	}

	/**
	 * Note: this will set the element dirty 
	 */	
	public void addChildElement(String key, C element) {
		((IGSElement<?>) element).setDirty();
		this.childElements.put(key, element);
	}

	public void deleteChildElement(String key) {
		if(this.childElements.get(key) == null) //TODO: think later abt it 
			throw new MeshException("Object not found with key: "+key);	 		
		((IGSElement<?>)this.childElements.get(key)).setDirty();

		if(this.childElements.get(key) instanceof GSCell) //TODO: cell is not deleted, only cell content is removed 
			((GSCell)this.childElements.get(key)).updateCellValue("");  
		else
			((IGSElement<?>)this.childElements.get(key)).setDeleteCandidate();
	}

	public C getChildElement(String key) {
		return this.childElements.get(key);
	}

	public void updateChildElement(String key, C element) {
		((IGSElement<?>)element).setDirty();
		this.childElements.put(key, element);
	}	
	
	public abstract void refreshMe();
	
}
