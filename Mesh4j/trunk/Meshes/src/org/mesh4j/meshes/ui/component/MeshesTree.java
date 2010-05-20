package org.mesh4j.meshes.ui.component;

import java.io.IOException;
import java.util.List;

import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.Mesh;

public class MeshesTree extends JTree {

	private static final long serialVersionUID = -4780437305410143442L;
	
	public MeshesTree() {
		setRootVisible(false);
		setCellRenderer(new MeshesTreeCellRenderer());
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		
		createNodes(root);
		
		setModel(new DefaultTreeModel(root));
		
		ToolTipManager.sharedInstance().registerComponent(this);
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
//	    DefaultMutableTreeNode oswego = new DefaultMutableTreeNode("Oswego");
//	    DefaultMutableTreeNode mesh2 = new DefaultMutableTreeNode("Mesh2");
//	    
//	    DefaultMutableTreeNode tomato = new DefaultMutableTreeNode("TomatoOutbreakSurvey");
//	    
//	    tomato.add(new DefaultMutableTreeNode("Access"));
//	    tomato.add(new DefaultMutableTreeNode("Local Excel file"));
//	    tomato.add(new DefaultMutableTreeNode("Johns Google Spreadsheet"));
//	    tomato.add(new DefaultMutableTreeNode("Map"));
//	    
//	    DefaultMutableTreeNode clinics = new DefaultMutableTreeNode("Clinics and Hospitals in Oswego");
//	    
//	    clinics.add(new DefaultMutableTreeNode("Shared KML"));
//	    
//	    oswego.add(tomato);
//	    oswego.add(clinics);
//	    
//	    top.add(oswego);
//	    top.add(mesh2);
		ConfigurationManager confMgr = new ConfigurationManager();
		List<Mesh> meshes;
		try {
			meshes = confMgr.getAllMeshes();
		} catch (IOException e) {
			throw new Error(e);
		}
		for (Mesh mesh : meshes) {
			top.add(new DefaultMutableTreeNode(mesh.getName()));
		}
	}

}
