package org.mesh4j.sync.adapters.dom.parsers;

import java.util.HashMap;
import java.util.Map;

public class FileManager implements IFileManager {

	// MODEL VARIABLES
	private HashMap<String, String> files = new HashMap<String, String>();
	
	// BUSINESS METHODS
	
	@Override
	public String getFileContent(String fileName) {
		return files.get(fileName);
	}

	@Override
	public Map<String, String> getFileContents() {
		return new HashMap<String, String>(files);
	}

	@Override
	public void removeFileContent(String fileName) {
		files.remove(fileName);
	}

	@Override
	public void setFileContent(String fileName, String fileContent) {
		files.put(fileName, fileContent);
	}
}
