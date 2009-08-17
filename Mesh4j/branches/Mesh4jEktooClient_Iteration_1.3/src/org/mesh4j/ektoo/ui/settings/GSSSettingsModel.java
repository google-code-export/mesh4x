package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.model.AbstractModel;

public class GSSSettingsModel extends AbstractModel{

	private String gUserName = "";
	private String gPassword = "";
	
	public String getGUserName() {
		return gUserName;
	}

	public void setGUserName(String userName) {
		gUserName = userName;
	}

	public String getGPassword() {
		return gPassword;
	}

	public void setGPassword(String password) {
		gPassword = password;
	}

	public GSSSettingsModel(){
	}

	}
