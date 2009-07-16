package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.FeedUIController;

public class ZipFeedModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String fileName = null;

	// BUSINESS METHODS
	public ZipFeedModel(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public void setFileName(String fileName) {
		firePropertyChange(FeedUIController.FILE_NAME_PROPERTY, this.fileName, this.fileName = fileName);
	}

	public String getFileName() {
		return this.fileName;
	}
	
	 public String toString()
	 {
	    return "ZIP FEED | " + getFileName();
	 }

}