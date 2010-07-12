package org.mesh4j.meshes.ui.component;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.MsAccessDataSource;

@SuppressWarnings("serial")
public class DataSourceView extends JComponent {

	private final DataSource dataSource;
	private SyncLogList syncLogList;

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
		tabbedPane.addTab("Data Source", new MsAccessDataSourceView((MsAccessDataSource) dataSource));
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
		panel.add(new JScrollPane(syncLogList), c);
		
		// Fillers
		c.weightx = 0; c.weighty = 10;
		panel.add(new JPanel(), c);
		
		return panel;
	}

}
