package org.mesh4j.sync.adapters.folder;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class FilesFilter implements FilenameFilter {

	// MODEL VARIABLES
	private List<String> includeFileNames = new ArrayList<String>();
	private List<String> excludeFileNames = new ArrayList<String>();
	private List<String> includeFileExt = new ArrayList<String>();
	private List<String> excludeFileExt = new ArrayList<String>();
	
	// BUSINESS METHODS
	
	@Override
	public boolean accept(File dir, String name) {
		int pointIndex = name.indexOf(".");
		
		String extension = "";
		if(pointIndex != -1){
			extension = name.substring(pointIndex+1, name.length());
		}
		
		if(this.includeFileNames.contains(name) || this.includeFileExt.contains(extension)){
			return true;
		}
		
		if(this.includeFileNames.isEmpty() && this.includeFileExt.isEmpty()){
			if(this.excludeFileNames.isEmpty() && this.excludeFileExt.isEmpty()){
				return true;
			} else {
				return !(this.excludeFileNames.contains(name) || this.excludeFileExt.contains(extension));
			}
		}else{
			return false;
		}
	}

	public void includeFileName(String fileName){
		this.includeFileNames.add(fileName);
	}

	public void includeFileExt(String fileExt){
		this.includeFileExt.add(fileExt);
	}

	public void excludeFileName(String fileName){
		this.excludeFileNames.add(fileName);
	}

	public void excludeFileExt(String fileExt){
		this.excludeFileExt.add(fileExt);
	}
}
