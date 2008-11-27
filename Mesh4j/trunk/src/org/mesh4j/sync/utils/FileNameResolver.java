package org.mesh4j.sync.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.ISourceIdResolver;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class FileNameResolver implements ISourceIdResolver {

	private final static Log LOGGER = LogFactory.getLog(FileNameResolver.class);
	
	// MODEL VARIABLES
	private String fileName;
	private Properties fileMappings = new Properties();

	// BUSINESS METHODS
	
	public FileNameResolver(String fileName) {
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		this.fileName = fileName;
		this.load();
	}

	public void load() {
		File file = new File(this.fileName);
		if(!file.exists()){
			try{
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (Exception e) {
				throw new MeshException(e);
			}
		} else {
			boolean isOk = true;
			FileReader reader = null;
			try{
				reader = new FileReader(this.fileName);
				this.fileMappings.load(reader);
			} catch (Exception e) {
				isOk = false;
				throw new MeshException(e);
			} finally{
				if(reader != null){
					try{
						reader.close();
					} catch (Exception e) {
						if(isOk){
							throw new MeshException(e);		
						} else {
							LOGGER.error(e.getMessage(), e);
						}
					}
				}
			}
		}
	}
	
	public void store() {
		boolean isOk = true;
		FileWriter writer = null;
		try{
			writer = new FileWriter(this.fileName);
			this.fileMappings.store(writer, "");
			writer.flush();
		} catch(Exception e){
			isOk = false;
			throw new MeshException(e);
		} finally{
			if(writer != null){
				try{
					writer.close();
				} catch (Exception e) {
					if(isOk){
						throw new MeshException(e);		
					} else {
						LOGGER.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	@Override
	public String getSource(String fileId) {
		return this.fileMappings.getProperty(fileId);
	}

	@Override
	public void putSource(String fileId, String fileName) {
		this.fileMappings.put(fileId, fileName);
	}

}
