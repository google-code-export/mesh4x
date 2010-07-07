package org.mesh4j.meshes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.ui.component.DataSetView;
import org.mesh4j.meshes.ui.component.MeshView;
import org.mesh4j.meshes.ui.component.MeshesTree;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class MainWindow extends JFrame {
	
	private static final long serialVersionUID = -1240237676349762846L;

	public MainWindow() {
		super();
		initialize();
	}

	private void initialize() {
		this.setSize(new Dimension(800, 560));
		this.getContentPane().setLayout(new BorderLayout());
		
		this.setIconImage(ResourceManager.getLogo());
		
		// Split panel
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.add(splitPane, BorderLayout.CENTER);
		splitPane.setDividerLocation(200);
		
		// Tree for Meshes
		MeshesTree meshesTree = new MeshesTree();
		meshesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent ev) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ev.getPath().getLastPathComponent();
				Object userObject = node.getUserObject();
				
				if (userObject instanceof Mesh) {
					splitPane.setRightComponent(new MeshView((Mesh) userObject));
				} else if (userObject instanceof DataSet) {
					splitPane.setRightComponent(new DataSetView((DataSet) userObject));
				} else {
					splitPane.setRightComponent(null);
				}
			}
		});
		splitPane.setLeftComponent(new JScrollPane(meshesTree));
		
		// Central pane with tabs
		JTabbedPane tabbedPane = new JTabbedPane();
		
		JComponent syncNowTab = makeTextPanel("Panel #1");
		tabbedPane.addTab("Sync now", syncNowTab);
		
		JComponent schedulesTab = makeTextPanel("Panel #2");
		tabbedPane.addTab("Schedules", schedulesTab);
		
		JComponent securityTab = makeTextPanel("Panel #3");
		tabbedPane.addTab("Security", securityTab);
		
		JComponent conflictsTab = makeTextPanel("Panel #4");
		tabbedPane.addTab("Conflicts", conflictsTab);
		
		JComponent historyTab = makeTextPanel("Panel #5");
		tabbedPane.addTab("History", historyTab);
		
		JComponent smsSettingsTab = makeTextPanel("Panel #6");
		tabbedPane.addTab("SMS Settings", smsSettingsTab);

		splitPane.setRightComponent(tabbedPane);

		this.setTitle("Meshes");
		this.setResizable(false);
	}
	
	protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

}
