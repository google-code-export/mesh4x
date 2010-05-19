package org.mesh4j.meshes.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.mesh4j.meshes.model.Mesh;

public class ConfigurationManager {

	private File settingsDirectory;
	private File configurationsDirectory;

	public ConfigurationManager() {
		initConfigurationPath();
	}

	public List<Mesh> getAllMeshes() throws IOException {

		File[] meshFiles = configurationsDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".mesh");
			}
		});

		List<Mesh> meshes = new ArrayList<Mesh>();
		for (File meshFile : meshFiles) {
			FileInputStream in = new FileInputStream(meshFile);
			try {
				Mesh mesh = MeshMarshaller.fromXml(in);
				if (mesh != null)
					meshes.add(mesh);
			} finally {
				in.close();
			}
		}

		return meshes;
	}

	public void saveMesh(Mesh mesh) throws IOException {
		File meshFile = new File(configurationsDirectory, mesh.getName() + ".mesh");
		FileOutputStream out = new FileOutputStream(meshFile);
		try {
			MeshMarshaller.toXml(mesh, out);
		} finally {
			out.close();
		}
	}

	private void initConfigurationPath() {
		String osname = System.getProperty("os.name").toLowerCase();

		if (osname.startsWith("windows")) {
			// Windows Vista added a different environment variable for local
			// settings
			String appData = System.getenv("LOCALAPPDATA");

			// Use APPDATA for Windows XP and previous versions
			if (appData == null) {
				appData = System.getenv("APPDATA");
			}
			settingsDirectory = new File(appData, "Instedd\\Meshes");
		} else {
			String userHome = System.getProperty("user.home");
			if (osname.startsWith("mac os"))
				settingsDirectory = new File(userHome, "Library/Application Support/Instedd/Meshes");
			else
				settingsDirectory = new File(userHome, ".meshes");
		}

		settingsDirectory.mkdirs();

		configurationsDirectory = new File(settingsDirectory, "conf.d");
		configurationsDirectory.mkdirs();
	}
}
