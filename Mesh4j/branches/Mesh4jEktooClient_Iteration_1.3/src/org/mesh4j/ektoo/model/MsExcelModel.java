package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.MsExcelUIController;

public class MsExcelModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String workbookName = null;
	private String worksheetName = null;
	private String uniqueColumnName = null;

	// BUSINESS METHODS
	
	public MsExcelModel(String workbookName){
		super();
		this.workbookName = workbookName;
	}
	
	public void setWorkbookName(String workbookName) {
		firePropertyChange(MsExcelUIController.WORKBOOK_NAME_PROPERTY, this.workbookName, this.workbookName = workbookName);
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public void setWorksheetName(String worksheetName) {
		firePropertyChange(MsExcelUIController.WORKSHEET_NAME_PROPERTY, this.worksheetName, this.worksheetName = worksheetName);
	}

	public String getWorksheetName() {
		return worksheetName;
	}

	public void setUniqueColumnName(String uniqueColumnName) {
		firePropertyChange(MsExcelUIController.UNIQUE_COLUMN_NAME_PROPERTY, this.uniqueColumnName, this.uniqueColumnName = uniqueColumnName);
	}

	@Override
	protected void fireEmptyMappingForPropertyChange(String propertyName) {
		if(!MsExcelUIController.UNIQUE_COLUMN_NAME_PROPERTY.equals(propertyName)){
			super.fireEmptyMappingForPropertyChange(propertyName);
		}
	}
	
	public String getUniqueColumnName() {
		return uniqueColumnName;
	}
	
	public String toString()
	{
	  return "Ms Excel | " + getWorkbookName() + " | " + getWorksheetName();
	}
}
