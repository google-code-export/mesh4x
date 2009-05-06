package org.mesh4j.ektoo.model;

import org.mesh4j.sync.validations.Guard;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class CloudModel extends AbstractModel {
	
	// MODEL VARIABLES
	private String baseUri;
	private String meshName = null;
	private String datasetName = null;

	// BUSINESS METHODS
	public CloudModel(String baseUri) {
		Guard.argumentNotNullOrEmptyString(baseUri, "baseUri");
		this.baseUri = baseUri;
	}

	public void setMeshName(String mesh) {
		firePropertyChange("meshName", this.meshName, this.meshName = mesh);
	}

	public String getMeshName() {
		return meshName;
	}

	public void setDatasetName(String dataset) {
		firePropertyChange("datasetName", this.datasetName,
				this.datasetName = dataset);
	}

	public String getDatasetName() {
		return datasetName;
	}

	public String getUri() {
		return this.baseUri + "/" + (getMeshName() != null ? getMeshName() : "")
				+ "/" + (getDatasetName() != null ? getDatasetName() : "");
	}
	
  public String toString()
  {
    return "Cloud | " + getUri();
  }	
}
