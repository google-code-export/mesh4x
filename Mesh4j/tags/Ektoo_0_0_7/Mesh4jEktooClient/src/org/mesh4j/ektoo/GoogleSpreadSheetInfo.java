package org.mesh4j.ektoo;

import org.mesh4j.sync.validations.Guard;

public class GoogleSpreadSheetInfo {

	// MODEL VARIABLES
	private String idColumnName = "";
	private String userName = "";
	private String passWord = "";
	//private String googleSpreadSheetId = "";
	private String googleSpreadSheetName = "";

	private String sheetName = "";
	private String type = "";

	// BUSINESS METHODS

	public GoogleSpreadSheetInfo(String googleSpreadSheetName, String userName,
			String passWord, String idColumnName,String sheetName, String type) {

		Guard.argumentNotNullOrEmptyString(googleSpreadSheetName,"googleSpreadSheetName");
		Guard.argumentNotNullOrEmptyString(userName, "userName");
		Guard.argumentNotNullOrEmptyString(passWord, "passWord");		
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNullOrEmptyString(sheetName, "sheetName");
		Guard.argumentNotNullOrEmptyString(type, "type");

		//this.googleSpreadSheetId = googleSpreadSheetId;
		this.googleSpreadSheetName = googleSpreadSheetName;
		this.idColumnName = idColumnName;
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

	public String getUserName() {
		return userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public String getGoogleSpreadSheetName() {
		return googleSpreadSheetName;
	}
	
/*	public String getGoogleSpreadSheetId() {
		return googleSpreadSheetId;
	}*/
}
