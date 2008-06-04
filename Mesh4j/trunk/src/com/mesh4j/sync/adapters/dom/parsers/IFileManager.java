package com.mesh4j.sync.adapters.dom.parsers;

import java.util.Map;

public interface IFileManager {

	String getFileContent(String fileName);

	void setFileContent(String fileName, String fileContent);

	Map<String, String> getFileContents();

	void removeFileContent(String fileName); 

}
