package org.mesh4j.meshes.ui.component;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.mesh4j.meshes.model.DataSource;

@SuppressWarnings("serial")
public class DataSourceView extends JComponent {

	private final DataSource dataSource;
	private SyncLogList syncLogList;

	public DataSourceView(DataSource dataSource) {
		this.dataSource = dataSource;
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
		c.weightx = 1;
		
		// Labels
		add(new JLabel("Synchronization history:"), c);
		
		// Controls
		syncLogList = new SyncLogList();
		syncLogList.setLogEntries(dataSource.getLogEntries());
		add(new JScrollPane(syncLogList), c);
		
		// Fillers
		c.weightx = 0; c.weighty = 10;
		add(new JPanel(), c);
	}

}
