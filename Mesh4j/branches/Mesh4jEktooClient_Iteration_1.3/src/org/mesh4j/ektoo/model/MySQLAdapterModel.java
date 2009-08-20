package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.MySQLUIController;

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
	private String portNo = "";
	private String databaseName = null;
	private String[] tableNames = null;

	// BUSINESS METHODS
	public void setUserName(String userName) 
	{
		firePropertyChange(MySQLUIController.USER_NAME_PROPERTY, this.userName, this.userName = userName);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserPassword(String userPassword) 
	{
		firePropertyChange(MySQLUIController.USER_PASSWORD_PROPERTY, this.userPassword,
				this.userPassword = userPassword);
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setHostName(String hostName) 
	{
		firePropertyChange(MySQLUIController.HOST_NAME_PROPERTY, this.hostName, this.hostName = hostName);
	}

	public String getHostName() {
		return hostName;
	}

	public void setPortNo(String portNo) {
		firePropertyChange(MySQLUIController.PORT_NO_PROPERTY, this.portNo, this.portNo = portNo);
	}

	public String getPortNo() {
		return portNo;
	}

	public void setDatabaseName(String databaseName) {
		firePropertyChange(MySQLUIController.DATABASE_NAME_PROPERTY, this.databaseName,
				this.databaseName = databaseName);
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setTableNames(String[] tableNames) {
		firePropertyChange(MySQLUIController.TABLE_NAME_PROPERTY, this.tableNames,
				this.tableNames = tableNames);
	}

	public String[] getTableNames() {
		return tableNames;
	}
	
  public String toString()
  {
     return "MySQL | " + getHostName() + " | " + getPortNo() + " | " + getDatabaseName() + " | " + getTableNames().toString();
  } 	
}
