package org.mesh4j.meshes.controller;

import org.mesh4j.meshes.model.Mesh;

public class CreateMeshWizardController extends AbstractController {
	
	private Mesh mesh;
	
	public CreateMeshWizardController(Mesh mesh) {
		this.mesh = mesh;
		addModel(mesh);
	}
	
	public void changeMeshName(String name) {
		mesh.setName(name);
	}
	
	public void changeMeshDescription(String description) {
		mesh.setDescription(description);
	}
	
	public void changeMeshPassword(String password) {
		mesh.setPassword(password);
	}

}
