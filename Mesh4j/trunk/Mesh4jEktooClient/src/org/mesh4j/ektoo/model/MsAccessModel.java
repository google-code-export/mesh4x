package org.mesh4j.ektoo.model;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class MsAccessModel extends AbstractModel 
{
	private String databaseName = null;
	private String tableName 	= null;
	
	public void setDatabaseName(String databaseName) 
	{
		firePropertyChange("databaseName", this.databaseName, this.databaseName = databaseName);	
	}
	
	public String getDatabaseName() 
	{
		return databaseName;
	}
	
	public void setTableName(String tableName) 
	{
		firePropertyChange("tableName", this.tableName, this.tableName = tableName);	
	}
	
	public String getTabletName() 
	{
		return tableName;
	}
}
