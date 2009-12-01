package org.mesh4j.meshes.model;

public class GSSheetDataSource extends DataSource {
	
	public static final String USER_NAME_PROPERTY = "gssheet_username";
	public static final String PASSWORD_PROPERTY = "gssheet_password";
	public static final String SPREADSHEET_NAME_PROPERTY = "gssheet_spreadsheet";
	public static final String WORKSHEET_NAME_PROPERTY = "gssheet_worksheet";
	public static final String UNIQUE_COLUMN_NAME_PROPERTY = "gssheet_uniquecolumn";
	
	private String userName;
	private String password;
	private String spreadsheetName;
	private String worksheetName;
	private String uniqueColumnName;
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		String oldUserName = this.userName;
		this.userName = userName;
		firePropertyChange(USER_NAME_PROPERTY, oldUserName, userName);
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		String oldPassword = this.password;
		this.password = password;
		firePropertyChange(PASSWORD_PROPERTY, oldPassword, password);
	}
	
	public String getSpreadsheetName() {
		return spreadsheetName;
	}
	
	public void setSpreadsheetName(String spreadsheetName) {
		String oldSpreadsheetName = this.spreadsheetName;
		this.spreadsheetName = spreadsheetName;
		firePropertyChange(SPREADSHEET_NAME_PROPERTY, oldSpreadsheetName, spreadsheetName);
	}
	
	public String getWorksheetName() {
		return worksheetName;
	}
	
	public void setWorksheetName(String worksheetName) {
		String oldWorksheetName = this.worksheetName;
		this.worksheetName = worksheetName;
		firePropertyChange(WORKSHEET_NAME_PROPERTY, oldWorksheetName, worksheetName);
	}
	
	public String getUniqueColumnName() {
		return uniqueColumnName;
	}
	
	public void setUniqueColumnName(String uniqueColumnName) {
		String oldUniqueColumnName = this.uniqueColumnName;
		this.uniqueColumnName = uniqueColumnName;
		firePropertyChange(UNIQUE_COLUMN_NAME_PROPERTY, oldUniqueColumnName, uniqueColumnName);
	}

}
