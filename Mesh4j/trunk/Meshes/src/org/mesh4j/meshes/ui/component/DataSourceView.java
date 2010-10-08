package org.mesh4j.meshes.ui.component;

import java.awt.BorderLayout;
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
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.MsAccessDataSource;
import org.mesh4j.meshes.model.SchedulingOption;
import org.mesh4j.meshes.model.SyncMode;
import org.mesh4j.sync.validations.MeshException;

@SuppressWarnings("serial")
public class DataSourceView extends EditableComponent {

	private final DataSource dataSource;
	private JComboBox schedulingComboBox;
	private JComboBox syncModeComboBox;
	private MsAccessDataSourceView dataSourceView;

	public DataSourceView(DataSource dataSource) {
		this.dataSource = dataSource;
		addViewComponents();
	}
	
	private void addViewComponents() {
		setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
		add(tabbedPane);
		
		tabbedPane.addTab("Scheduling", createSchedulingTab());
		if (dataSource instanceof MsAccessDataSource) {
			tabbedPane.addTab("Data Source", dataSourceView = new MsAccessDataSourceView((MsAccessDataSource) dataSource));
		
			dataSourceView.setEditableListener(new EditableListener() {
				@Override
				public void dirtyChanged(boolean isDirty) {
					notifyEditableListener();
				}
			});
		}
	}
	
	private JPanel createSchedulingTab() {
		JPanel panel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		
		// Labels
		
		panel.add(new JLabel("Scheduling: "), c);
		panel.add(new JLabel("Sync Mode: "), c);
		
		// Controls
		
		c.gridx = 1;
		
		DefaultComboBoxModel schedulingModel = new DefaultComboBoxModel(SchedulingOption.values());
		panel.add(schedulingComboBox = new JComboBox(schedulingModel), c);
		schedulingComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyEditableListener();
			}
		});
		
		DefaultComboBoxModel syncModeModel = new DefaultComboBoxModel(SyncMode.values());
		panel.add(syncModeComboBox = new JComboBox(syncModeModel), c);
		syncModeComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notifyEditableListener();
			}
		});

		// Fillers
		c.gridx = 2; c.gridy = 0; c.weightx = 10;
		panel.add(new JPanel(), c);
		c.gridx = 0; c.gridy = 4; c.weightx = 0; c.weighty = 10;
		panel.add(new JPanel(), c);
		
		return panel;
	}
	
	@Override
	protected void loadModel() {
		dataSourceView.loadModel();
		schedulingComboBox.setSelectedItem(dataSource.getSchedule().getSchedulingOption());
		syncModeComboBox.setSelectedItem(dataSource.getSchedule().getSyncMode());	
	}

	@Override
	public boolean isDirty() {
		return dataSourceView.isDirty() ||
			dataSource.getSchedule().getSchedulingOption() != schedulingComboBox.getSelectedItem() ||
			dataSource.getSchedule().getSyncMode() != syncModeComboBox.getSelectedItem();
	}

	@Override
	public void saveChanges() {
		dataSourceView.saveChanges();
		dataSource.getSchedule().setSchedulingOption((SchedulingOption) schedulingComboBox.getSelectedItem());
		dataSource.getSchedule().setSyncMode((SyncMode) syncModeComboBox.getSelectedItem());
		try {
			ConfigurationManager.getInstance().saveMesh(dataSource.getMesh());
		} catch (IOException e) {
			throw new MeshException(e);
		}
		notifyEditableListener();
	}
}
