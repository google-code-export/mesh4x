package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.model.AbstractModel;

public class GeneralSettingsModel extends AbstractModel{
	 
	private String language = "";
	private String sourcePath = "";
	private String targetPath = "";

	public GeneralSettingsModel(){
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		firePropertyChange(SettingsController.PATH_SOURCE, this.sourcePath, sourcePath);
		this.sourcePath = sourcePath;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		firePropertyChange(SettingsController.PATH_TARGET, this.targetPath, targetPath);
		this.targetPath = targetPath;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		firePropertyChange(SettingsController.LANGUAGE, this.language, language);
		this.language = language;
	}

	


	
}
