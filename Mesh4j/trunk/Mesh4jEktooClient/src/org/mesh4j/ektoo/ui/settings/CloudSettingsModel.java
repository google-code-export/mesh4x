package org.mesh4j.ektoo.ui.settings;

import org.mesh4j.ektoo.model.AbstractModel;

public class CloudSettingsModel extends AbstractModel{
	
	private String syncServerRootUri = "";
	private String meshName = "";
	private String datasetName = "";
	private Boolean createAsDefaultProp = false;
	
	public CloudSettingsModel(){
	}
	

	public Boolean isCreateAsDefaultProp() {
		return createAsDefaultProp;
	}

	public void setCreateAsDefaultProp(Boolean createAsDefaultProp) {
		firePropertyChange(SettingsController.CREATE_PROP_AS_DEFAULT, this.createAsDefaultProp, createAsDefaultProp);
		this.createAsDefaultProp = createAsDefaultProp;
	}
	
	public String getSyncServerRootUri() {
		return syncServerRootUri;
	}

	public void setSyncServerRootUri(String syncServerRootUri) {
		firePropertyChange(SettingsController.CLOUD_ROOT_URI, this.syncServerRootUri, syncServerRootUri);
		this.syncServerRootUri = syncServerRootUri;
	}

	public String getMeshName() {
		return meshName;
	}

	public void setMeshName(String meshName) {
		firePropertyChange(SettingsController.CLOUD_MESH_NAME, this.meshName, meshName);
		this.meshName = meshName;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		firePropertyChange(SettingsController.CLOUD_DATASET_NAME, this.datasetName, datasetName);
		this.datasetName = datasetName;
	}
	
}
