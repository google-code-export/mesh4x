package org.mesh4j.meshes.ui.component;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.mesh4j.meshes.action.CreateNewMeshAction;
import org.mesh4j.meshes.action.DeleteMeshAction;
import org.mesh4j.meshes.action.ExportDataSourceConfigurationAction;
import org.mesh4j.meshes.action.SynchronizeNowAction;
import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.AbstractModel;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.FeedRef;
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
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		
		if (!e.isPopupTrigger()) return;
		
		List<Action> actions = new ArrayList<Action>();
		
		TreePath path = getPathForLocation(e.getX(), e.getY());
		if (path == null) {
			actions.add(new CreateNewMeshAction());
		} else {
			setSelectionPath(path);
			
			Object obj = path.getLastPathComponent();
			if (obj instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
				Object nodeObject = node.getUserObject();
				
				
				if (nodeObject instanceof Mesh) {
					actions.add(new DeleteMeshAction((Mesh) nodeObject, this.getTopLevelAncestor()));
				} else if (nodeObject instanceof DataSource) {					
					actions.add(new SynchronizeNowAction((DataSource) nodeObject));
					actions.add(new ExportDataSourceConfigurationAction((DataSource) nodeObject));
				}
			}
		}
		
		if (actions.isEmpty()) return;
		
		JPopupMenu menu = new JPopupMenu();
		for (Action action : actions) {
			menu.add(action);
		}
		menu.show(this, e.getX(), e.getY());
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
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(mesh);
		
		for (DataSource dataSource : mesh.getDataSources()) {
			node.add(createNodeForDataSource(dataSource));
		}
		
		return node;
	}

	private MutableTreeNode createNodeForDataSource(DataSource dataSource) {
		DefaultMutableTreeNode node = createNode(dataSource);
		
		for (FeedRef feedRef : dataSource.getFeeds()) {
			node.add(createNodeForFeedRef(feedRef));
		}
		
		return node;
	}

	private MutableTreeNode createNodeForFeedRef(FeedRef feedRef) {
		return createNode(feedRef);
	}
	
	private DefaultMutableTreeNode createNode(AbstractModel model) {
		final DefaultMutableTreeNode node = new DefaultMutableTreeNode(model);
		
		model.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent e) {
				((DefaultTreeModel) getModel()).nodeChanged(node);
			}
		});
		
		return node;
	}

	private final class MeshListListener implements ListDataListener {
		
		private final DefaultMutableTreeNode root;

		public MeshListListener(DefaultMutableTreeNode root) {
			this.root = root;
		}

		@Override
		public void intervalRemoved(ListDataEvent e) {
			for (int i = e.getIndex0(); i<= e.getIndex1(); i++)
				root.remove(i);
			((DefaultTreeModel)getModel()).reload();
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
