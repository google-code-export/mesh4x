package org.mesh4j.meshes.ui.component;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.mesh4j.meshes.model.DataSet;
import org.mesh4j.meshes.model.Mesh;

public class MeshesTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 7286261949126552775L;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		if (value instanceof DefaultMutableTreeNode) {
			Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
			if (userObject instanceof DataSet)
				value = getDataSetLabel((DataSet) userObject);
			else if (userObject instanceof Mesh)
				value = ((Mesh)userObject).getName();
		}
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		return this;
	}

	private String getDataSetLabel(DataSet dataSet) {
		switch (dataSet.getState()) {
		case FAILED:
			return dataSet.getName() + " (FAILED)";
		case NORMAL:
			return dataSet.getName();
		case SYNC:
			return dataSet.getName() + " (SYNCHRONIZING)";
		}
		return null;
	}
	
}
