package org.mesh4j.meshes.controller;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSetType;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.MsAccessDataSource;
import org.mesh4j.meshes.model.Schedule;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.meshes.ui.wizard.BaseWizardPanel;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepFiveView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepFourView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepOneView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepSevenView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepSixView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepThreeView;
import org.mesh4j.meshes.ui.wizard.CreateMeshStepTwoView;
import org.mesh4j.meshes.ui.wizard.WizardView;

public class CreateMeshWizardController extends WizardController {
	
	// MODEL
	private Mesh mesh;
	private DataSet dataSet;
	private DataSource dataSource;
	private Schedule schedule;
	
	// VIEWS
	private CreateMeshStepOneView stepOne;
	private CreateMeshStepTwoView stepTwo;
	private CreateMeshStepThreeView stepThree;
	private CreateMeshStepFourView stepFour;
	private CreateMeshStepFiveView stepFive;
	private CreateMeshStepSixView stepSix;
	private CreateMeshStepSevenView stepSeven;
	private int current;
	
	public CreateMeshWizardController(WizardView wizardView) {
		super(wizardView);
		
		// Create models
		this.mesh = new Mesh();
		this.dataSet = new DataSet();
		this.schedule = new Schedule();
		
		addModel(mesh);
		addModel(dataSet);
		addModel(schedule);
		
		// Create views
		stepOne = new CreateMeshStepOneView(this);
		stepTwo = new CreateMeshStepTwoView(this);
		stepThree = new CreateMeshStepThreeView(this);
		stepFour = new CreateMeshStepFourView(this);
		stepFive = new CreateMeshStepFiveView(this);
		stepSix = new CreateMeshStepSixView(this);
		stepSeven = new CreateMeshStepSevenView(this);
		
		current = 0;
		
		registerWizardPanel(stepOne);
		registerWizardPanel(stepTwo);
		registerWizardPanel(stepThree);
		registerWizardPanel(stepFour);
		registerWizardPanel(stepFive);
		registerWizardPanel(stepSix);
		registerWizardPanel(stepSeven);
		
		setCurrentPanel(stepOne);
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
	
	public void setMsAccessDataSource() {
		clearDataSource();
		dataSource = new MsAccessDataSource();
	}
	
	private void clearDataSource() {
		if (dataSource != null) {
			removeModel(dataSource);
		}
	}

	@Override
	public void backButtonPressed() {
		if (current > 0) {
			BaseWizardPanel backPanel = wizardPanels.get(--current);
			setCurrentPanel(backPanel);
		}
	}

	@Override
	public void nextButtonPressed() {
		if (current < wizardPanels.size() - 1) {
			BaseWizardPanel nextPanel = wizardPanels.get(++current);
			setCurrentPanel(nextPanel);
		}
	}
}
