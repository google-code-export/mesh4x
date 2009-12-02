package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;

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
		
		JComboBox scheduleComboBox = new JComboBox(new DefaultComboBoxModel(SchedulingOption.values()));
		add(scheduleComboBox, "gapleft 60, wrap 10");
		
		scheduleComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				scheduleComboBoxItemStateChanged(e);
			}
		});
		
		JComboBox syncModeComboBox = new JComboBox(new DefaultComboBoxModel(SyncMode.values()));
		add(syncModeComboBox, "gapleft 60, wrap 10");
		
		syncModeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				syncModeComboBoxItemStateChanged(e);
			}
		});
		
		noSyncRadioButton = new JRadioButton();
		noSyncRadioButton.setText("Import the data and schema now but don't keed this database connected to the mesh");
		add(noSyncRadioButton, "gapleft 30");
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(syncRadioButton);
		buttonGroup.add(noSyncRadioButton);
	}
	
	private void scheduleComboBoxItemStateChanged(ItemEvent e) {
		SchedulingOption schedulingOption = (SchedulingOption) e.getItem();
		descriptor.getController().changeSchedulingOption(schedulingOption);
	}
	
	private void syncModeComboBoxItemStateChanged(ItemEvent e) {
		SyncMode syncMode = (SyncMode) e.getItem();
		descriptor.getController().changeSyncMode(syncMode);
	}

}
