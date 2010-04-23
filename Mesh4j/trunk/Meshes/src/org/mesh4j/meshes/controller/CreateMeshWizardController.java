package org.mesh4j.meshes.controller;

import java.beans.PropertyChangeEvent;
import java.io.File;

import org.mesh4j.meshes.model.CreateMeshModel;
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
	private CreateMeshModel model;
	
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
		
		// Create model
		this.model = new CreateMeshModel();
		addModel(this.model);
		
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
		registerWizardPanel(stepFive);
		registerWizardPanel(stepSix);
		registerWizardPanel(stepSeven);
		
		setCurrentPanel(stepOne);
		
		setButtonsState();
	}
	
	public void changeMeshName(String name) {
		model.setName(name);
	}
	
	public void changeMeshDescription(String description) {
		model.setDescription(description);
	}
	
	public void changeMeshPassword(String password) {
		model.setPassword(password);
	}
	
	public void changeMeshPasswordConfirmation(String passwordConfirmation) {
		model.setPasswordConfirmation(passwordConfirmation);
	}
	
	public void changeSchedulingOption(SchedulingOption schedulingOption) {
		model.setScheduling(schedulingOption);
	}
	
	public void changeSyncMode(SyncMode syncMode) {
		model.setSyncMode(syncMode);
	}
	
	public void changeEpiInfoLocation(String epiInfoLocation) {
		model.setEpiInfoLocation(epiInfoLocation);
	}
	
	public void saveConfiguration(File file) {
		// TODO serialize mesh configuration to file
	}
	
	public void finish() {
		// TODO 
	}
	
	@Override
	public void backButtonPressed() {
		BaseWizardPanel backPanel = wizardPanels.get(--current);
		setCurrentPanel(backPanel);
		setButtonsState();
	}

	@Override
	public void nextButtonPressed() {
		BaseWizardPanel nextPanel = wizardPanels.get(++current);
		setCurrentPanel(nextPanel);
		setButtonsState();
	}
	
	private void setButtonsState() {
		wizardView.setBackButtonEnabled(!isFirst());
		wizardView.setNextButtonEnabled(!isLast() && wizardPanels.get(current).valid());
		wizardView.setFinishVisible(isLast());
		wizardView.setCancelVisible(!isLast());
	}
	
	private boolean isFirst() {
		return current == 0;
	}
	
	private boolean isLast() {
		return current == (wizardPanels.size() - 1);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		wizardView.setNextButtonEnabled(wizardPanels.get(current).valid());
	}
}
