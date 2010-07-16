package org.mesh4j.meshes.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Logger;
import org.mesh4j.meshes.filefilters.EpiInfoFileFilter;
import org.mesh4j.meshes.model.DataSource;
import org.mesh4j.meshes.model.EpiInfoDataSource;
import org.mesh4j.meshes.model.Mesh;
import org.mesh4j.meshes.model.MeshVisitor;

public class ConfigurationManager {
	
	private final static Logger LOGGER = Logger.getLogger(ConfigurationManager.class);

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
			Mesh mesh = MeshMarshaller.fromXml(meshFile);
			if (mesh != null)
				meshes.add(mesh);
		}

		return meshes;
	}

	/**
	 * Saves the mesh and returns the file where it was saved.
	 */
	public File saveMesh(Mesh mesh) throws IOException {
		File meshFile = new File(configurationsDirectory, mesh.getName() + ".mesh");
		MeshMarshaller.toXml(mesh, meshFile);
		
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
	
	public void importFile(String fileName) {
		try {
			final Mesh mesh = MeshMarshaller.fromXml(new File(fileName));
			final Map<String, String> resolvedFilenames = new HashMap<String, String>();
			final boolean[] canceled = { false };
			
			// Resolve external resources
			mesh.accept(new MeshVisitor() {
				@Override
				public boolean visit(EpiInfoDataSource dataSource) {
					if (canceled[0]) return false;
					
					String fileName = dataSource.getFileName();
					String resolvedFilename = resolvedFilenames.get(fileName);
					if (resolvedFilename == null) {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setDialogTitle("Please choose the location of the MDB file " + new File(fileName).getName());
						fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
						fileChooser.setFileFilter(new EpiInfoFileFilter());
						fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
						fileChooser.setSelectedFile(new File(fileName));
						int returnVal = fileChooser.showOpenDialog(null);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selectedFile = fileChooser.getSelectedFile();
							resolvedFilename = selectedFile.getAbsolutePath();
							resolvedFilenames.put(fileName, resolvedFilename);
						}
					}
					if (resolvedFilename == null) {
						canceled[0] = true;
					} else {
						dataSource.setFileName(resolvedFilename);
					}
					return false;
				}
			});
			
			if (!canceled[0]) {
				saveMesh(mesh);
			}
		} catch (IOException e) {
			LOGGER.error("Failed to import file " + fileName, e);
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
		
		runtimeDirectory = new File(settingsDirectory, "runtime");
		runtimeDirectory.mkdirs();
	}

	public File getRuntimeDirectory(DataSource dataSource) {
		File meshDirectory = new File(runtimeDirectory, dataSource.getDataSet().getMesh().getName());
		File feedDirectory = new File(meshDirectory, dataSource.getDataSet().getName());
		File dataSourceFileDirectory = new File(feedDirectory, dataSource.getId());
		dataSourceFileDirectory.mkdirs();
		return dataSourceFileDirectory;
	}
	
	public void addListDataListener(ListDataListener listener) {
		listDataListeners.add(listener);
	}
	
	public void removeListDataListener(ListDataListener listener) {
		listDataListeners.remove(listener);
	}

	public void deleteMesh(Mesh mesh) throws IOException {
		File meshFile = new File(configurationsDirectory, mesh.getName() + ".mesh");
		meshFile.delete();
		
		// Notify the listeners about the change
		List<Mesh> currentMeshes = getAllMeshes();
		int meshIndex = currentMeshes.indexOf(mesh);
		if (meshIndex >= 0) {
			currentMeshes.remove(meshIndex);
			for (ListDataListener listener : listDataListeners) {
				listener.intervalRemoved(new ListDataEvent(currentMeshes, ListDataEvent.INTERVAL_REMOVED, meshIndex, meshIndex));
			}
		}
	}
}
