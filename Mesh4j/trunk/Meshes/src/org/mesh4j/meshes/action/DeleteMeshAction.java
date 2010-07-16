package org.mesh4j.meshes.action;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.mesh4j.meshes.io.ConfigurationManager;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.sync.validations.MeshException;

@SuppressWarnings("serial")
public class DeleteMeshAction extends AbstractAction {

	private final Mesh mesh;

	public DeleteMeshAction(Mesh mesh) {
		super("Delete mesh");
		this.mesh = mesh;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			ConfigurationManager.getInstance().deleteMesh(mesh);
		} catch (IOException ex) {
			throw new MeshException(ex);
		}
	}

}
