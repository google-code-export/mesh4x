package org.mesh4j.meshes.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.sync.validations.MeshException;

@SuppressWarnings("serial")
public class DataSetView extends EditableComponent {

	private final DataSet dataSet;
	private JTextField nameField;
	private JComboBox schedulingComboBox;
	private JComboBox syncModeComboBox;

	public DataSetView(DataSet dataSet) {
		this.dataSet = dataSet;
		addViewComponents();
		loadModel();
	}

	private void addViewComponents() {
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		
		// Labels
		
		add(new JLabel("Name: "), c);
		add(new JLabel("Scheduling: "), c);
		add(new JLabel("Sync Mode: "), c);
		
		// Controls
		
		c.gridx = 1;
		nameField = new JTextField(30);
		nameField.setEnabled(false);
		add(nameField, c);
		
		DefaultComboBoxModel schedulingModel = new DefaultComboBoxModel(SchedulingOption.values());
		add(schedulingComboBox = new JComboBox(schedulingModel), c);
		schedulingComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyEditableListener();
			}
		});
		
		DefaultComboBoxModel syncModeModel = new DefaultComboBoxModel(SyncMode.values());
		add(syncModeComboBox = new JComboBox(syncModeModel), c);
		syncModeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyEditableListener();
			}
		});

		// Fillers
		c.gridx = 2; c.gridy = 0; c.weightx = 10;
		add(new JPanel(), c);
		c.gridx = 0; c.gridy = 4; c.weightx = 0; c.weighty = 10;
		add(new JPanel(), c);
	}
	
	@Override
	protected void loadModel() {
		nameField.setText(dataSet.getName());
		schedulingComboBox.setSelectedItem(dataSet.getSchedule().getSchedulingOption());
		syncModeComboBox.setSelectedItem(dataSet.getSchedule().getSyncMode());	
	}

	@Override
	public boolean isDirty() {
		return
			dataSet.getSchedule().getSchedulingOption() != schedulingComboBox.getSelectedItem() ||
			dataSet.getSchedule().getSyncMode() != syncModeComboBox.getSelectedItem();
	}

	@Override
	public void saveChanges() {
		dataSet.getSchedule().setSchedulingOption((SchedulingOption) schedulingComboBox.getSelectedItem());
		dataSet.getSchedule().setSyncMode((SyncMode) syncModeComboBox.getSelectedItem());
		try {
			ConfigurationManager.getInstance().saveMesh(dataSet.getMesh());
			notifyEditableListener();
		} catch (IOException e) {
			throw new MeshException(e);
		}
	}
}
