package org.mesh4j.meshes.ui.wizard;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import net.miginfocom.swing.MigLayout;

import org.mesh4j.meshes.controller.CreateMeshWizardController;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;

public class CreateMeshStepSixView extends BaseWizardPanel {

	private static final long serialVersionUID = -5773369351266179486L;
	private static String ID = "STEP_SIX";
	
	private CreateMeshWizardController controller;
	
	private JRadioButton twoWaySyncButton;
	private JRadioButton sendSyncButton;
	private JRadioButton receiveSyncButton;
	private ButtonGroup buttonGroup;
	private JComboBox scheduleComboBox;
	
	public CreateMeshStepSixView(CreateMeshWizardController controller) {
		super();
		this.controller = controller;
		initComponents();
	}

	private void initComponents() {
		setLayout(new MigLayout("insets 10"));
		setSize(550, 350);
		
		JLabel titleLabel = new JLabel("<html><h2>Keep your data connected</h2></html>");
		add(titleLabel, "span");
		
		JLabel scheduleLabel = new JLabel("<html><h4>Keep your data synchronized:</h4><html>");
		add(scheduleLabel, "gapleft 30, wrap 10");
		
		scheduleComboBox = new JComboBox(new DefaultComboBoxModel(SchedulingOption.values()));
		add(scheduleComboBox, "gapleft 30, wrap 40");
		
		scheduleComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				scheduleComboBoxItemStateChanged(e);
			}
		});
		
		twoWaySyncButton = new JRadioButton();
		twoWaySyncButton.setText("Send and receive changes to data");
		twoWaySyncButton.setSelected(true);
		add(twoWaySyncButton, "gapleft 30, wrap 5");
		
		twoWaySyncButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				twoWaySyncButtonItemStateChanged(e);
			}
		});
		
		sendSyncButton = new JRadioButton();
		sendSyncButton.setText("Only send my changes");
		add(sendSyncButton, "gapleft 30, wrap 5");
		
		sendSyncButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				sendSyncButtonItemStateChanged(e);
			}
		});
		
		receiveSyncButton = new JRadioButton();
		receiveSyncButton.setText("Only get other's changes");
		add(receiveSyncButton, "gapleft 30, wrap 5");
		
		receiveSyncButton.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				receiveSyncButtonItemStateChanged(e);
			}
		});
		
		buttonGroup = new ButtonGroup();
		buttonGroup.add(twoWaySyncButton);
		buttonGroup.add(sendSyncButton);
		buttonGroup.add(receiveSyncButton);
	}
	
	private void scheduleComboBoxItemStateChanged(ItemEvent e) {
		SchedulingOption schedulingOption = (SchedulingOption) e.getItem();
		controller.changeSchedulingOption(schedulingOption);
	}
	
	private void twoWaySyncButtonItemStateChanged(ItemEvent e) {
		controller.changeSyncMode(SyncMode.SEND_AND_RECEIVE);
	}
	
	private void sendSyncButtonItemStateChanged(ItemEvent e) {
		controller.changeSyncMode(SyncMode.SEND);
	}
	
	private void receiveSyncButtonItemStateChanged(ItemEvent e) {
		controller.changeSyncMode(SyncMode.RECEIVE);
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
	}

}
