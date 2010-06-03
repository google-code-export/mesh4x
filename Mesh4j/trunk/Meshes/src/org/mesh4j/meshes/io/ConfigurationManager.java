package org.mesh4j.meshes.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.mesh4j.meshes.model.Mesh;

public class ConfigurationManager {

	private File settingsDirectory;
	private File configurationsDirectory;
	private File runtimeDirectory;
	private List<Mesh> meshes;
	private List<ListDataListener> listDataListeners = new ArrayList<ListDataListener>();
	
	private static ConfigurationManager instance = new ConfigurationManager();

	private ConfigurationManager() {
		initConfigurationPath();
	}
	
	public static ConfigurationManager getInstance() {
		return instance;
	}

	public List<Mesh> getAllMeshes() throws IOException {

		if (meshes != null)
			return meshes;
		
		File[] meshFiles = configurationsDirectory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".mesh");
			}
		});

		meshes = new ArrayList<Mesh>();
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

	/**
	 * Saves the mesh and returns the file where it was saved.
	 */
	public File saveMesh(Mesh mesh) throws IOException {
		File meshFile = new File(configurationsDirectory, mesh.getName() + ".mesh");
		FileOutputStream out = new FileOutputStream(meshFile);
		try {
			MeshMarshaller.toXml(mesh, out);
		} finally {
			out.close();
		}
		
		// Notify the listeners about the change
		List<Mesh> currentMeshes = getAllMeshes();
		int meshIndex = currentMeshes.indexOf(mesh);
		if (meshIndex >= 0) {
			for (ListDataListener listener : listDataListeners) {
				listener.contentsChanged(new ListDataEvent(currentMeshes, ListDataEvent.CONTENTS_CHANGED, meshIndex, meshIndex));
			}
		} else {
			currentMeshes.add(mesh);
			meshIndex = currentMeshes.indexOf(mesh);
			for (ListDataListener listener : listDataListeners) {
				listener.intervalAdded(new ListDataEvent(currentMeshes, ListDataEvent.INTERVAL_ADDED, meshIndex, meshIndex));
			}
		}
		
		return meshFile;
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
		
		runtimeDirectory = new File(settingsDirectory, "runtime");
		runtimeDirectory.mkdirs();
	}

	public File getRuntimeDirectory(Mesh mesh) {
		File meshDirectory = new File(runtimeDirectory, mesh.getName());
		meshDirectory.mkdirs();
		return meshDirectory;
	}
	
	public void addListDataListener(ListDataListener listener) {
		listDataListeners.add(listener);
	}
	
	public void removeListDataListener(ListDataListener listener) {
		listDataListeners.remove(listener);
	}
}
