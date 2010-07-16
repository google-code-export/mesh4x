package org.mesh4j.meshes.ui.wizard;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Map;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public class WizardConfigureDataSourceStep extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_FIVE";
	
	public static String EPIINFO_PANEL = "epiinfo_panel";
	
	private CreateMeshWizardController controller;
	
	private CardLayout cardLayout;
	private Map<String, ConfigPanel> configPanels;
	private ConfigPanel currentConfigPanel;
	
	public WizardConfigureDataSourceStep(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		this.configPanels = new HashMap<String, ConfigPanel>();
		initComponents();
	}

	private void initComponents() {
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		ConfigPanel epiInfoConfigPanel = new EpiInfoConfigPanel(controller);
		configPanels.put(EPIINFO_PANEL, epiInfoConfigPanel);
		add(EPIINFO_PANEL, epiInfoConfigPanel);
		
		setCurrentConfig(EPIINFO_PANEL);
	}
	
	public void setCurrentConfig(String id) {
		cardLayout.show(this, id);
		currentConfigPanel = configPanels.get(id);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
		// TODO propagate event to currentConfigPanel
	}

	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getErrorMessage() {
		return currentConfigPanel.getErrorMessage();
	}
	
}
