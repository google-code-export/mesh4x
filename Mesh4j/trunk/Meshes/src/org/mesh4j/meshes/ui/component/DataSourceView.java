package org.mesh4j.meshes.ui.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.MsAccessDataSource;
import org.mesh4j.sync.validations.MeshException;

@SuppressWarnings("serial")
public class DataSourceView extends EditableComponent {

	private final DataSource dataSource;
	private SyncLogList syncLogList;
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
		
		tabbedPane.addTab("History", createHistoryTab());
		if (dataSource instanceof MsAccessDataSource) {
			tabbedPane.addTab("Data Source", dataSourceView = new MsAccessDataSourceView((MsAccessDataSource) dataSource));
		
			dataSourceView.setEditableListener(new EditableListener() {
				@Override
				public void dirtyChanged(boolean isDirty) {
					notifyEditableListener();
				}
			});
		}
		tabbedPane.addTab("Conflicts", new ConflictsView(dataSource));
	}

	private JPanel createHistoryTab() {
		
		JPanel panel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		panel.setLayout(layout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 10, 5, 10);
		c.anchor = GridBagConstraints.NORTH;
		c.gridy = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.weightx = 1;
		
		// Labels
		panel.add(new JLabel("Synchronization history:"), c);
		
		// Controls
		syncLogList = new SyncLogList();
		syncLogList.setLogEntries(dataSource.getLogEntries());
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		panel.add(new JScrollPane(syncLogList), c);
		
		
		// Fillers
		c.weightx = 0; c.weighty = 1;
		panel.add(new JPanel(), c);
		
		return panel;
	}

	@Override
	protected void loadModel() {
		dataSourceView.loadModel();
	}

	@Override
	public boolean isDirty() {
		return dataSourceView.isDirty();
	}

	@Override
	public void saveChanges() {
		dataSourceView.saveChanges();
		try {
			ConfigurationManager.getInstance().saveMesh(dataSource.getDataSet().getMesh());
		} catch (IOException e) {
			throw new MeshException(e);
		}
		notifyEditableListener();
	}
}
