package org.mesh4j.meshes.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.mesh4j.meshes.filefilters.EpiInfoFileFilter;

public class FileChooserUtil {

	public static File chooseEpiInfoFile(Component parent, File selected) {
		return chooseFile(parent, new EpiInfoFileFilter(), selected);
	}

	public static File chooseFile(Component parent, FileFilter filter, File selected) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(filter);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		fileChooser.setSelectedFile(selected);
		int returnVal = fileChooser.showOpenDialog(parent);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}
}
