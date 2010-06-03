package org.mesh4j.meshes.ui.component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.Mesh;

public class MeshesTree extends JTree {

	private static final long serialVersionUID = -4780437305410143442L;
	
	public MeshesTree() {
		setRootVisible(false);
		setShowsRootHandles(true);
		setCellRenderer(new MeshesTreeCellRenderer());
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		
		createNodes(root);
		
		setModel(new DefaultTreeModel(root));
		
		ToolTipManager.sharedInstance().registerComponent(this);
		
		// Subscribe for mesh changes
		ConfigurationManager.getInstance().addListDataListener(new MeshListListener(root));
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
		ConfigurationManager confMgr = ConfigurationManager.getInstance();
		List<Mesh> meshes;
		try {
			meshes = confMgr.getAllMeshes();
		} catch (IOException e) {
			throw new Error(e);
		}
		for (Mesh mesh : meshes) {
			top.add(createNodeForMesh(mesh));
		}
	}

	private MutableTreeNode createNodeForMesh(Mesh mesh) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(mesh.getName());
		
		for (DataSet dataSet : mesh.getDataSets()) {
			node.add(createNodeForDataSet(dataSet));
		}
		
		return node;
	}

	private MutableTreeNode createNodeForDataSet(final DataSet dataSet) {
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode(dataSet.getName());
		
		for (DataSource dataSource : dataSet.getDataSources()) {
			node.add(createNodeForDataSource(dataSource));
		}
		
		dataSet.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				if (e.getPropertyName() == DataSet.STATE_PROPERTY) {
					switch (dataSet.getState()) {
					case FAILED:
						node.setUserObject(dataSet.getName() + " (FAILED)");
						break;
					case NORMAL:
						node.setUserObject(dataSet.getName());
						break;
					case SYNC:
						node.setUserObject(dataSet.getName() + " (SYNCHRONIZING)");
						break;
					}
					((DefaultTreeModel) getModel()).reload(node);
				}
			}
		});
		
		return node;
	}

	private MutableTreeNode createNodeForDataSource(DataSource dataSource) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(dataSource.toString());
		return node;
		
	}

	private final class MeshListListener implements ListDataListener {
		
		private final DefaultMutableTreeNode root;

		public MeshListListener(DefaultMutableTreeNode root) {
			this.root = root;
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			// TODO Auto-generated method stub
		}

		@SuppressWarnings("unchecked")
		@Override
		public void intervalAdded(ListDataEvent e) {
			List<Mesh> meshes = (List<Mesh>) e.getSource();
			for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
				Mesh mesh = meshes.get(i);
				root.add(createNodeForMesh(mesh));
				((DefaultTreeModel)getModel()).reload();
			}
		}

		@Override
		public void contentsChanged(ListDataEvent e) {
			// TODO Auto-generated method stub
		}
	}	
}
