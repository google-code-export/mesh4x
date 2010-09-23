package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class WizardChooseDataSourceTypeStep extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_DATA_SOURCE";
	
	private CreateMeshWizardController controller;
	
	public WizardChooseDataSourceTypeStep(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		add(new JLabel("<html><h2>Create a new data source</h2></html>"), "span");
		
		add(new JLabel("<html><h4>When you add a data surce to your mesh, you can see it mobile devices, " +
		  "maps, or applications. You can even allow other applications to update the data</h4></html>"), "span, wrap 10");
		
		JLabel dataSourceQuestion = new JLabel("<html><h4>What data source would you like to add?</h4></html>");
		add(dataSourceQuestion, "span, wrap 5");
		
		JToggleButton epiInfoButton = new JToggleButton("Epi Info Data", new ImageIcon(ResourceManager.getTableImage()));
		epiInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setValue("dataSourceType", DataSourceType.EPI_INFO);
				controller.nextButtonPressed();
			}
		});
		add(epiInfoButton, "gapright 10");
		
		JToggleButton databaseInfoButton = new JToggleButton("Database", new ImageIcon(ResourceManager.getTableImage()));
		databaseInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setValue("dataSourceType", DataSourceType.DATABASE);
				controller.nextButtonPressed();
			}
		});
		add(databaseInfoButton, "gapright 10");
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(epiInfoButton);
	}

	@Override
	public String getId() {
		return ID;
	}

}
