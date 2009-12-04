package org.mesh4j.meshes.ui.wizard;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;

import org.mesh4j.meshes.controller.CreateMeshWizardController;

public class CreateMeshStepFiveView extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_FIVE";
	
	public static String MS_ACCESS_PANEL = "ms_access_panel";
	public static String MS_EXCEL_PANEL = "ms_excel_panel";
	
	private CreateMeshWizardController controller;
	
	private CardLayout cardLayout;
	private MsAccessConfigPanel msAccessConfigPanel;
	private MsExcelConfigPanel msExcelConfigPanel;
	
	public CreateMeshStepFiveView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		
		msAccessConfigPanel = new MsAccessConfigPanel(controller);
		msExcelConfigPanel = new MsExcelConfigPanel(controller);
		
		add(MS_ACCESS_PANEL, msAccessConfigPanel);
		add(MS_EXCEL_PANEL, msExcelConfigPanel);
	}
	
	public void setCurrentConfig(String id) {
		cardLayout.show(this, id);
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}
	
}
