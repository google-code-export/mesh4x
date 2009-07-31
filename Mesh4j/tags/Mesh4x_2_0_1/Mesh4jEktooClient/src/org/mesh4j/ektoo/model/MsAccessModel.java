package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.MsAccessUIController;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsAccessModel extends AbstractModel 
{
	// MODEL VARIABLES
	private String databaseName = null;
	private String [] tableNames = null;

	// BUSINESS METHODS
	public MsAccessModel(String databaseName){
		super();
		this.databaseName = databaseName;
	}
	
	public void setDatabaseName(String databaseName) {
		firePropertyChange(MsAccessUIController.DATABASE_NAME_PROPERTY, this.databaseName,
				this.databaseName = databaseName);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setTableNames(String [] tableNames) 
	{
		firePropertyChange( MsAccessUIController.TABLE_NAME_PROPERTY, this.tableNames,
				this.tableNames = tableNames);
	}

	public String[] getTableNames() {
		return tableNames;
	}

	public String toString(){
	     return "Ms Access | " + getDatabaseName() + " | " + getTableNames().toString();
	}	
}
