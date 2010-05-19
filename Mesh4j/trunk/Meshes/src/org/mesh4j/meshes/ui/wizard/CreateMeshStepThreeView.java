package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class CreateMeshStepThreeView extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_THREE";
	
	private CreateMeshWizardController controller;
	
	private JToggleButton epiInfoButton;
	private ButtonGroup buttonGroup;
	
	public CreateMeshStepThreeView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("<html><h2>Create a new data source</h2></html>");
		add(titleLabel, "span");
		
		JLabel subTitleLabel = new JLabel();
		subTitleLabel.setText("<html><h4>When you add a data surce to your mesh, you can see it mobile devices, " +
							  "maps, or applications. You can even allow other applications to update the data</h4></html>");
		add(subTitleLabel, "span, wrap 10");
		
		JLabel dataSourceQuestion = new JLabel();
		dataSourceQuestion.setText("<html><h4>What data source would you like to add?</h4></html>");
		add(dataSourceQuestion, "span, wrap 5");
		
		epiInfoButton = new JToggleButton();
		epiInfoButton.setText("Epi Info Data");
		ImageIcon tableIcon = new ImageIcon(ResourceManager.getTableImage());
		epiInfoButton.setIcon(tableIcon);
		JLabel tableLabel = new JLabel();
		tableLabel.setText("<html></html>");
		add(epiInfoButton, "gapright 10");
		add(tableLabel, "growx, wrap");
		
		epiInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				epiInfoButtonActionPerformed(e);
			}
		});
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(epiInfoButton);
	}
	

	private void epiInfoButtonActionPerformed(ActionEvent e) {
		controller.nextButtonPressed();
		// TODO fire a property change
		//controller.setTableDataSetType();
	}

	@Override
	public void modelPropertyChange(PropertyChangeEvent evt) {
	}

	@Override
	public String getId() {
		return ID;
	}
	
	@Override
	public String getErrorMessage() {
		return null;
		//return epiInfoButton.isSelected();
	}

}
