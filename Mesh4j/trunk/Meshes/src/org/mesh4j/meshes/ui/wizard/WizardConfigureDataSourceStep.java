package org.mesh4j.meshes.ui.wizard;

import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public class WizardConfigureDataSourceStep extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_DATA_SOURCE_CONFIG";
	
	protected CreateMeshWizardController controller;
	
	private CardLayout cardLayout;
	private Map<DataSourceType, ConfigPanel> configPanels;
	private ConfigPanel currentConfigPanel;
	
	public WizardConfigureDataSourceStep(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		this.configPanels = new HashMap<DataSourceType, ConfigPanel>();
		initComponents();
	}

	private void initComponents() {
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		for(DataSourceType type : DataSourceType.values()) {
			ConfigPanel epiInfoConfigPanel = newConfigPanel(type);
			configPanels.put(type, epiInfoConfigPanel);
			add(type.name(), epiInfoConfigPanel);
		}
	}
	
	protected ConfigPanel newConfigPanel(DataSourceType type) {
		switch(type) {
		case EPI_INFO:
			return new EpiInfoConfigPanel(controller);
		case DATABASE:
			return new DatabaseConfigPanel(controller);
		default:
			throw new IllegalStateException("No ConfigPanel for DataSourceType " + type.name());
		}
	}
	
	private void setCurrentConfig(DataSourceType type) {
		cardLayout.show(this, type.name());
		currentConfigPanel = configPanels.get(type);
	}

	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getErrorMessage() {
		return currentConfigPanel.getErrorMessage();
	}
	
	@Override
	public boolean needsValidationBeforeLeave() {
		return currentConfigPanel.needsValidationBeforeLeave();
	}
	
	@Override
	public String getErrorMessageBeforeLeave() {
		return currentConfigPanel.getErrorMessageBeforeLeave();
	}
	
	@Override
	public void showInWizard() {
		DataSourceType type = (DataSourceType) controller.getValue("dataSourceType");
		setCurrentConfig(type);
		currentConfigPanel.showInWizard();
	}
	
}
