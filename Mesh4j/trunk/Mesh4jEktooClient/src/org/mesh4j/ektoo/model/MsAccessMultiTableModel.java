package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.MsAccessMultiTableUIController;

public class MsAccessMultiTableModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String databaseName = null;
	private Object[] tableNames = null;

	// BUSINESS METHODS
	public MsAccessMultiTableModel(String databaseName){
		super();
		this.databaseName = databaseName;
	}
	
	public void setDatabaseName(String databaseName) {
		firePropertyChange(MsAccessMultiTableUIController.DATABASE_NAME_PROPERTY, this.databaseName,
				this.databaseName = databaseName);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setTableNames(Object[] tableNames) 
	{
		firePropertyChange(MsAccessMultiTableUIController.TABLE_NAMES_PROPERTY, this.tableNames, this.tableNames = tableNames);
	}

	public Object[] getTabletNames() {
		return tableNames;
	}
	
  public String toString()
  {
     return "Ms Access | " + getDatabaseName() + " | " + getTabletNames();
  }	
}
