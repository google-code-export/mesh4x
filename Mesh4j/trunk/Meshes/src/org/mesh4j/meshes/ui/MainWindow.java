package org.mesh4j.meshes.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSource;
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
		final JPanel viewContainer = new JPanel(new BorderLayout());
		splitPane.setRightComponent(viewContainer);
		splitPane.setDividerLocation(200);
		
		// Tree for Meshes
		MeshesTree meshesTree = new MeshesTree();
		splitPane.setLeftComponent(new JScrollPane(meshesTree));
		meshesTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent ev) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) ev.getPath().getLastPathComponent();
				Object userObject = node.getUserObject();
				
				viewContainer.removeAll();
				
				if (userObject instanceof Mesh) {	
					viewContainer.add(new MeshView((Mesh) userObject));
				} else if (userObject instanceof DataSet) {
					viewContainer.add(new DataSetView((DataSet) userObject));
				} else if (userObject instanceof DataSource) {
					// TODO: Implement DataSourceView
				}
				
				viewContainer.revalidate();
				viewContainer.repaint();
			}
		});

		this.setTitle("Meshes");
		this.setResizable(false);
	}	
}
