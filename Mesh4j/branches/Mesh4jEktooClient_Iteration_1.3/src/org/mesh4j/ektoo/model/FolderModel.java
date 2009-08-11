package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.FolderUIController;

public class FolderModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String folderName = null;

	// BUSINESS METHODS
	public FolderModel(String folderName) {
		super();
		this.folderName = folderName;
	}
	
	public void setFolderName(String folderName) {
		firePropertyChange(FolderUIController.FOLDER_NAME_PROPERTY, this.folderName, this.folderName = folderName);
	}

	public String getFolderName() {
		return folderName;
	}
	
	 public String toString()
	 {
	    return "FOLDER | " + getFolderName();
	 }
}