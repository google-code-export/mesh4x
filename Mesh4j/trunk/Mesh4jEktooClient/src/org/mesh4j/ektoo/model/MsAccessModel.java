package org.mesh4j.ektoo.model;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MsAccessModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String databaseName = null;
	private String tableName = null;

	// BUSINESS METHODS
	public void setDatabaseName(String databaseName) {
		firePropertyChange("databaseName", this.databaseName,
				this.databaseName = databaseName);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setTableName(String tableName) {
		firePropertyChange("tableName", this.tableName,
				this.tableName = tableName);
	}

	public String getTabletName() {
		return tableName;
	}
	
  public String toString()
  {
     return "Ms Access | " + getDatabaseName() + " | " + getTabletName();
  }	
}
