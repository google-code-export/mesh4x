package org.mesh4j.meshes.server;

import org.mesh4j.meshes.model.Mesh;

public interface IMeshServer {

	boolean areValid(String email, String password);

	void createMesh(Mesh mesh, String email, String password);

	boolean meshExists(String meshName, String email, String password);

	boolean createAccount(String email, String password);

}
