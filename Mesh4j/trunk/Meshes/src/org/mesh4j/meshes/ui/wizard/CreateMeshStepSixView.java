package org.mesh4j.meshes.ui.wizard;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

public class CreateMeshStepSixView extends JPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	
	private WizardPanelDescriptor descriptor;
	
	private JRadioButton syncRadioButton;
	private JRadioButton noSyncRadioButton;
	private ButtonGroup buttonGroup;
	
	public CreateMeshStepSixView(WizardPanelDescriptor descriptor) {
		super();
		this.descriptor = descriptor;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("Keep your data connected");
		add(titleLabel, "span, wrap 20");
		
		syncRadioButton = new JRadioButton();
		syncRadioButton.setText("Keep data synchronized");
		add(syncRadioButton, "gapleft 30, wrap 5");
		
		JLabel syncSubTitle = new JLabel();
		syncSubTitle.setText("Remember to leave your files in the same folder it is now so we can find it later");
		add(syncSubTitle, "gapleft 50, wrap 10");
		
		JComboBox scheduleComboBox = new JComboBox();
		add(scheduleComboBox, "gapleft 60, wrap 10");
		
		JComboBox j = new JComboBox();
		add(j, "gapleft 60, wrap 10");
		
		noSyncRadioButton = new JRadioButton();
		noSyncRadioButton.setText("Import the data and schema now but don't keed this database connected to the mesh");
		add(noSyncRadioButton, "gapleft 30");
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(syncRadioButton);
		buttonGroup.add(noSyncRadioButton);
	}

}
