package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.KmlUIController;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class KmlModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String fileName = null;

	// BUSINESS METHODS
	public KmlModel(String fileName) {
		super();
		this.fileName = fileName;
	}
	
	public void setFileName(String fileName) {
		firePropertyChange(KmlUIController.FILE_NAME_PROPERTY, this.fileName, this.fileName = fileName);
	}

	public String getFileName() {
		return this.fileName;
	}
	
	 public String toString()
	 {
	    return "KML | " + getFileName();
	 }
}