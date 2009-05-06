package org.mesh4j.ektoo.model;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MySQLAdapterModel extends AbstractModel 
{
	// MODEL VARIABLES
	private String userName = null;
	private String userPassword = null;
	private String hostName = null;
	private int portNo = -1;
	private String databaseName = null;
	private String tableName = null;

	// BUSINESS METHODS
	public void setUserName(String userName) 
	{
		firePropertyChange("userName", this.userName, this.userName = userName);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserPassword(String userPassword) 
	{
		firePropertyChange("userPassword", this.userPassword,
				this.userPassword = userPassword);
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setHostName(String hostName) 
	{
		firePropertyChange("hostName", this.hostName, this.hostName = hostName);
	}

	public String getHostName() {
		return hostName;
	}

	public void setPortNo(int portNo) {
		firePropertyChange("portNo", this.portNo, this.portNo = portNo);
	}

	public int getPortNo() {
		return portNo;
	}

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

	public String getTableName() {
		return tableName;
	}
	
  public String toString()
  {
     return "MySQL | " + getDatabaseName() + " | " + getTableName();
  } 	
}
