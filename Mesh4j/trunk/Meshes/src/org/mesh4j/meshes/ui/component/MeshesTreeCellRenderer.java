package org.mesh4j.meshes.ui.component;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class MeshesTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 7286261949126552775L;
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			
		setToolTipText("This is at level " + node.getLevel());
		
		return this;
	}

}
