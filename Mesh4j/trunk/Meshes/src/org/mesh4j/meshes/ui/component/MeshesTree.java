package org.mesh4j.meshes.ui.component;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreeSelectionModel;

public class MeshesTree extends JTree {

	private static final long serialVersionUID = -4780437305410143442L;
	
	public MeshesTree() {
		setRootVisible(false);
		setCellRenderer(new MeshesTreeCellRenderer());
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
}
