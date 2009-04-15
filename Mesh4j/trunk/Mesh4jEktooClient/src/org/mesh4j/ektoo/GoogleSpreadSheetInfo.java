package org.mesh4j.ektoo;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;

public class GoogleSpreadSheetInfo {

	private String idColumnName = "";
	int lastUpdateColumnPosition = 0;
	int idColumnPosition = 1;
	private String userName = "";
	private String passWord = "";
	private String GOOGLE_SPREADSHEET_FIELD = "";
	private int contentWorkSheetIndex = 0;
	private int syncWorkSheetIndex = 0;
	private IIdentityProvider identityProvider = null; 
	
	public IIdentityProvider getIdentityProvider() {
		return identityProvider;
	}
	public void setIdentityProvider(IIdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	private IdGenerator idGenerator = null;
	
	public int getContentWorkSheetIndex() {
		return contentWorkSheetIndex;
	}
	public void setContentWorkSheetIndex(int contentWorkSheetIndex) {
		this.contentWorkSheetIndex = contentWorkSheetIndex;
	}
	public int getSyncWorkSheetIndex() {
		return syncWorkSheetIndex;
	}
	public void setSyncWorkSheetIndex(int syncWorkSheetIndex) {
		this.syncWorkSheetIndex = syncWorkSheetIndex;
	}
	public String getIdColumnName() {
		return idColumnName;
	}
	public void setIdColumnName(String idColumnName) {
		this.idColumnName = idColumnName;
	}
	public int getLastUpdateColumnPosition() {
		return lastUpdateColumnPosition;
	}
	public void setLastUpdateColumnPosition(int lastUpdateColumnPosition) {
		this.lastUpdateColumnPosition = lastUpdateColumnPosition;
	}
	public int getIdColumnPosition() {
		return idColumnPosition;
	}
	public void setIdColumnPosition(int idColumnPosition) {
		this.idColumnPosition = idColumnPosition;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getGOOGLE_SPREADSHEET_FIELD() {
		return GOOGLE_SPREADSHEET_FIELD;
	}
	public void setGOOGLE_SPREADSHEET_FIELD(String google_spreadsheet_field) {
		GOOGLE_SPREADSHEET_FIELD = google_spreadsheet_field;
	}
}
