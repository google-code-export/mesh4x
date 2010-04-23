package org.mesh4j.meshes.controller;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.mesh4j.meshes.ui.wizard.BaseWizardPanel;
import org.mesh4j.meshes.ui.wizard.WizardView;

public abstract class WizardController extends AbstractController {
	
	protected WizardView wizardView;
	protected List<BaseWizardPanel> wizardPanels;
	
	public WizardController(WizardView wizardView) {
		this.wizardView = wizardView;
		this.wizardPanels = new ArrayList<BaseWizardPanel>();
		
		addView(wizardView);
		wizardView.setController(this);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		
		for (BaseWizardPanel wizardPanel : wizardPanels) {
			wizardPanel.modelPropertyChange(evt);
		}
	}
	
	protected void registerWizardPanel(BaseWizardPanel panel) {
		wizardView.registerWizardPanel(panel.getId(), panel);
		wizardPanels.add(panel);
	}
	
	protected void setCurrentPanel(BaseWizardPanel panel) {
		wizardView.setCurrentPanel(panel.getId());
	}
	
	public abstract void backButtonPressed();
	
	public abstract void nextButtonPressed();
	
	public abstract void finish();

}
