package org.mesh4j.meshes.ui.component;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.FeedRef;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.SyncState;
import org.mesh4j.meshes.ui.resource.ResourceManager;

public class MeshesTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 7286261949126552775L;
	
	private static Icon meshIcon = ResourceManager.getIcon("mesh.png");
	private static Icon dataSourceIcon = ResourceManager.getIcon("datasource.png");
	private static Icon dataSourceSynchronizingIcon = ResourceManager.getIcon("datasource_synchronizing.png");
	private static Icon dataSourceFailedIcon = ResourceManager.getIcon("datasource_failed.png");
	private static Icon feedIcon = ResourceManager.getIcon("feed.png");
	private static Icon feedFailedIcon = ResourceManager.getIcon("feed_failed.png");
	private static Icon feedSynchronizingIcon = ResourceManager.getIcon("feed_synchronizing.png");
	private static Icon feedConflictsIcon = ResourceManager.getIcon("feed_conflicts.png");
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		if (value instanceof DefaultMutableTreeNode) {
			Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
			if (userObject instanceof DataSource) {				
				DataSource dataSource = (DataSource) userObject;
				setText(getDataSourceLabel(dataSource));
				setIcon(getDataSourceIcon(dataSource));
			} else if (userObject instanceof Mesh) {
				Mesh mesh = (Mesh)userObject;
				setText(mesh.getName());
				setIcon(meshIcon);
			} else if (userObject instanceof FeedRef) {
				FeedRef feedRef = (FeedRef)userObject;
				setText(feedRef.getLocalName());
				setIcon(getFeedRefIcon(feedRef));
			}
		}
		
		return this;
	}

//	private String getDataSetLabel(DataSet dataSet) {
//		return dataSet.getName();
//	}
	
//	private Icon getDataSetIcon(DataSet dataSet) {
//		switch(dataSet.getState()) {
//		case FAILED:
//			return dataSetFailedIcon;
//		case SYNC:
//			return dataSetSynchronizingIcon;
//		}
//		return dataSetIcon;
//	}
	
	private String getDataSourceLabel(DataSource dataSource) {
		return dataSource.toString();
	}
	
	private Icon getDataSourceIcon(DataSource dataSource) {
		if (dataSource.getState() == SyncState.SYNC) {
			return dataSourceSynchronizingIcon;
		} else if (dataSource.hasFailures()) {
			return dataSourceFailedIcon;
		} else if (dataSource.hasConflicts()) {
			return dataSourceFailedIcon;
		} else {
			return dataSourceIcon;
		}
	}
	
	private Icon getFeedRefIcon(FeedRef feedRef) {
		if (feedRef.hasConflicts()) {
			return feedConflictsIcon;
		} else {
			switch (feedRef.getState()) {
				case SYNC:
					return feedSynchronizingIcon;
				case FAILED:
					return feedFailedIcon;
				default:
					return feedIcon;
			}
		}
	}
	
}
