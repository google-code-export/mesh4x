package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.model.AbstractModel;

public class MySqlSettingsModel extends AbstractModel{

	private String userName = "";
	private String userPassword = "";
	private String hostName = "";
	private String portNo ;
	private String databaseName = "";
	
	
	public MySqlSettingsModel(){
		
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		firePropertyChange(SettingsController.USER_NAME_MYSQL, this.userName, userName);
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		firePropertyChange(SettingsController.USER_PASSWORD_MYSQL, this.userPassword, userPassword);
		this.userPassword = userPassword;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		firePropertyChange(SettingsController.HOST_NAME_MYSQL, this.hostName, hostName);
		this.hostName = hostName;
	}

	public String getPortNo() {
		return portNo;
	}

	public void setPortNo(String portNo) {
		firePropertyChange(SettingsController.PORT_MYSQL, this.portNo, portNo);
		this.portNo = portNo;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		firePropertyChange(SettingsController.DATABASE_NAME_MYSQL, this.databaseName, databaseName);
		this.databaseName = databaseName;
	}

	 
	
	
}
