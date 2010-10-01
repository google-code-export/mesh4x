package org.mesh4j.meshes.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.validations.MeshException;

@SuppressWarnings("serial")
public class DeleteMeshAction extends AbstractAction {

	private final Mesh mesh;
	private final Component parentComponent;

	public DeleteMeshAction(Mesh mesh, Component parentComponent) {
		super("Delete mesh");
		this.mesh = mesh;
		this.parentComponent = parentComponent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			int result = JOptionPane.showConfirmDialog(parentComponent, "Are you sure you want to delete the mesh " + mesh.getName() + "?", "Delete mesh", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				ConfigurationManager.getInstance().deleteMesh(mesh);
			}
		} catch (IOException ex) {
			throw new MeshException(ex);
		}
	}

}
