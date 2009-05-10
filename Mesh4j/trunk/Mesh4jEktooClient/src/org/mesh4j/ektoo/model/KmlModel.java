package org.mesh4j.ektoo.model;

import org.mesh4j.ektoo.controller.KmlUIController;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class KmlModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String kmlFileName = null;

	// BUSINESS METHODS
	public KmlModel(String kmlFileName) {
		super();
		this.kmlFileName = kmlFileName;
	}
	
	public void setKmlFileName(String kmlFileName) {
		firePropertyChange(KmlUIController.FILE_NAME_PROPERTY, this.kmlFileName, this.kmlFileName = kmlFileName);
	}

	public String getKmlFileName() {
		return kmlFileName;
	}
	
	 public String toString()
	 {
	    return "KML | " + getKmlFileName();
	 }
}