package org.mesh4j.meshes.filefilters;

import java.io.File;

public class EpiInfoFileFilter extends javax.swing.filechooser.FileFilter {
	
	@Override
	public String getDescription() {
		return "EpiInfo data file (mdb file)";
	}
	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() || file.getName().toLowerCase().endsWith(".mdb");
	}

}
