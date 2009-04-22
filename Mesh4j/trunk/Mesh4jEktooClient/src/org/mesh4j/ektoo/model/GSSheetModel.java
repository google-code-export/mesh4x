package org.mesh4j.ektoo.model;
/**
 * @author Bhuiyan Mohammad Iklash
 *
 */
public class GSSheetModel extends AbstractModel
{
	private String userName 				= null;
	private String userPassword 			= null;
	private String spreadsheetKey 			= null;
	private String worksheetName 				= null;
	
	// TODO (NBL) Think how we can eliminate these items from gui
	private String uniqueColumnName 		= null;
	private int uniqueColumnPosition		= -1;
	private String lastUpdatedColumnName	= null;
  private int lastUpdatedColumnPosition  = -1;
  
	public void setUserName(String userName) 
	{
		firePropertyChange("userName", this.userName, this.userName = userName);  
	}
	
	public String getUserName() 
	{
		return userName;
	}
	
	public void setUserPassword(String userPassword) 
	{
    firePropertyChange("userPassword", this.userPassword, this.userPassword = userPassword);  
	}
	
	public String getUserPassword() 
	{
		return userPassword;
	}
	
	public void setSpreadsheetKey(String spreadsheetKey) 
	{
		firePropertyChange("spreadsheetKey", this.spreadsheetKey, this.spreadsheetKey = spreadsheetKey);		
	}
	
	public String getSpreadsheetKey() 
	{
		return spreadsheetKey;
	}
	
	public void setWorksheetName(String worksheetName) 
	{
		firePropertyChange("worksheetName", this.worksheetName, this.worksheetName = worksheetName);    		
	}
	
	public String getWorksheetName() 
	{
		return worksheetName;
	}
	
	public void setUniqueColumnName(String uniqueColumnName) 
	{
		firePropertyChange("uniqueColumnName", this.uniqueColumnName, this.uniqueColumnName = uniqueColumnName);		
	}
	
	public String getUniqueColumnName() 
	{
		return uniqueColumnName;
	}
	
	public void setUniqueColumnPosition(int uniqueColumnPosition) 
	{
		firePropertyChange("uniqueColumnPosition", this.uniqueColumnPosition, this.uniqueColumnPosition = uniqueColumnPosition);		
	}
	
	public int getUniqueColumnPosition() 
	{
		return uniqueColumnPosition;
	}
	
	public void setLastUpdatedColumnName(String lastUpdatedColumnName) 
	{
		firePropertyChange("lastUpdatedColumnName", this.lastUpdatedColumnName, this.lastUpdatedColumnName = lastUpdatedColumnName);		
	}
	
	public String getLastUpdatedColumnName() 
	{
		return lastUpdatedColumnName;
	}
	
  public void setLastUpdatedColumnPosition(int lastUpdatedColumnPosition) 
  {
    firePropertyChange("lastUpdatedColumnPosition", this.lastUpdatedColumnPosition, this.lastUpdatedColumnPosition = lastUpdatedColumnPosition);    
  }
  
  public int getLastUpdatedColumnPosition() 
  {
    return lastUpdatedColumnPosition;
  }
}
