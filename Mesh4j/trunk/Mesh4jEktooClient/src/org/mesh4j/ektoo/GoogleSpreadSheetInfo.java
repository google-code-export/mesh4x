package org.mesh4j.ektoo;

import org.mesh4j.sync.validations.Guard;


public class GoogleSpreadSheetInfo {

	// MODEL VARIABLES
	private String idColumnName = "";
	private int lastUpdateColumnPosition = 0;
	private int idColumnPosition = 1;
	
	private String userName = "";
	private String passWord = "";
	private String googleSpreadSheetId = "";
	
	private String sheetName = "";
	private String type = "";
	 
	
	
	// BUSINESS METHODS

	public GoogleSpreadSheetInfo(String googleSpreadSheetId,String userName,String passWord,
								   String idColumnName, int idColumnPosition,
									int lastUpdateColumnPosition,  String sheetName,String type
			)
	{
		
		Guard.argumentNotNullOrEmptyString(googleSpreadSheetId, "googleSpreadSheetId");
		// TODO (JMT) add parameters validations
		
		this.googleSpreadSheetId = googleSpreadSheetId;
		this.idColumnName = idColumnName;
		this.idColumnPosition = idColumnPosition;
		this.lastUpdateColumnPosition = lastUpdateColumnPosition;
		this.passWord = passWord;
		this.sheetName = sheetName;
		this.type = type;
		this.userName = userName;
	}
	
	public String getType() {
		return type;
	}

	public String getSheetName() {
		return sheetName;
	}
	
	public String getIdColumnName() {
		return idColumnName;
	}

	public int getLastUpdateColumnPosition() {
		return lastUpdateColumnPosition;
	}

	public int getIdColumnPosition() {
		return idColumnPosition;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public String getGoogleSpreadSheetId() {
		return googleSpreadSheetId;
	}
}
