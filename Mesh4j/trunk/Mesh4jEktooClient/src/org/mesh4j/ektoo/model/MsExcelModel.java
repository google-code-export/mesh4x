package org.mesh4j.ektoo.model;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsExcelModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String workbookName = null;
	private String worksheetName = null;
	private String uniqueColumnName = null;

	// BUSINESS METHODS
	
	public void setWorkbookName(String workbookName) {
		firePropertyChange("workbookName", this.workbookName,
				this.workbookName = workbookName);
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public void setWorksheetName(String worksheetName) {
		firePropertyChange("worksheetName", this.worksheetName,
				this.worksheetName = worksheetName);
	}

	public String getWorksheetName() {
		return worksheetName;
	}

	public void setUniqueColumnName(String uniqueColumnName) {
		firePropertyChange("uniqueColumnName", this.uniqueColumnName,
				this.uniqueColumnName = uniqueColumnName);
	}

	public String getUniqueColumnName() {
		return uniqueColumnName;
	}
}
