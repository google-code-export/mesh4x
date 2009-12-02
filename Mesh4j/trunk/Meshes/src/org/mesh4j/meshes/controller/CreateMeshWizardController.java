package org.mesh4j.meshes.controller;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetType;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;

public class CreateMeshWizardController extends AbstractController {
	
	private Mesh mesh;
	private DataSet dataSet;
	private Schedule schedule;
	
	public CreateMeshWizardController(Mesh mesh) {
		this.mesh = mesh;
		addModel(mesh);
	}
	
	public void changeMeshName(String name) {
		mesh.setName(name);
	}
	
	public void changeMeshDescription(String description) {
		mesh.setDescription(description);
	}
	
	public void changeMeshPassword(String password) {
		mesh.setPassword(password);
	}

	public void setTableDataSetType() {
		changeDataSetType(DataSetType.TABLE);
	}
	
	public void setMapDataSetType() {
		changeDataSetType(DataSetType.MAP);
	}
	
	public void setFilesDataSetType() {
		changeDataSetType(DataSetType.FILES);
	}
	
	public void changeDataSetName(String name) {
		dataSet.setName(name);
	}
	
	public void changeDataSetDescription(String description) {
		dataSet.setDescription(description);
	}
	
	private void changeDataSetType(DataSetType type) {
		dataSet.setType(type);
	}
	
	public void changeSchedulingOption(SchedulingOption schedulingOption) {
		schedule.setSchedulingOption(schedulingOption);
	}
	
	public void changeSyncMode(SyncMode syncMode) {
		schedule.setSyncMode(syncMode);
	}
}
