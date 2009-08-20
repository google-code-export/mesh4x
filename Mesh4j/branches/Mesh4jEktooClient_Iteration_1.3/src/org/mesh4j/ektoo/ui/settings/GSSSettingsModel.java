package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.model.AbstractModel;

public class GSSSettingsModel extends AbstractModel{

	private String gUserName = "";
	private String gPassword = "";
	private Boolean createAsDefaultProp = false;
	
	
	
	
	public Boolean isCreateAsDefaultProp() {
		return createAsDefaultProp;
	}
	
	public void setCreateAsDefaultProp(Boolean createAsDefaultProp) {
		firePropertyChange(SettingsController.CREATE_PROP_AS_DEFAULT, this.createAsDefaultProp, createAsDefaultProp);
		this.createAsDefaultProp = createAsDefaultProp;
	}
	
	public String getGUserName() {
		return gUserName;
	}

	public void setGUserName(String gUserName) {
		firePropertyChange(SettingsController.USER_NAME_GOOGLE, this.gUserName, gUserName);
		this.gUserName = gUserName;
	}

	public String getGPassword() {
		return gPassword;
	}

	public void setGPassword(String gPassword) {
		firePropertyChange(SettingsController.USER_PASSWORD_GOOGLE, this.gPassword, gPassword);
		this.gPassword = gPassword;
	}

	public GSSSettingsModel(){
	}

	}
