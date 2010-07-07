package org.mesh4j.meshes.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;

@SuppressWarnings("serial")
public class DataSetView extends JComponent {

	private final DataSet dataSet;
	private JTextField nameField;

	public DataSetView(DataSet dataSet) {
		this.dataSet = dataSet;
		addViewComponents();
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
		nameField = new JTextField(dataSet.getName(), 30);
		nameField.setEnabled(false);
		add(nameField, c);
		
		DefaultComboBoxModel schedulingModel = new DefaultComboBoxModel(SchedulingOption.values());
		schedulingModel.setSelectedItem(dataSet.getSchedule().getSchedulingOption());
		add(new JComboBox(schedulingModel), c);
		
		DefaultComboBoxModel syncModeModel = new DefaultComboBoxModel(SyncMode.values());
		syncModeModel.setSelectedItem(dataSet.getSchedule().getSyncMode());
		add(new JComboBox(syncModeModel), c);

		// Fillers
		c.gridx = 2; c.gridy = 0; c.weightx = 10;
		add(new JPanel(), c);
		c.gridx = 0; c.gridy = 4; c.weightx = 0; c.weighty = 10;
		add(new JPanel(), c);
	}
	
}
