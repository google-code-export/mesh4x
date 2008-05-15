package com.mesh4j.sync.properties;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.security.LocalSecurity;
import com.mesh4j.sync.validations.MeshException;

public class PropertiesProvider {

	// MODEL VARIABLES
	private Properties properties;
	
	// BUSINESS METHODS
	public PropertiesProvider(){
		super();
		initialize();
	}

	private void initialize() {
		try{
			FileReader reader = new FileReader("mesh4j.properties");
			Properties prop = new Properties();
			prop.load(reader);
			this.properties = prop;
			reader.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String getDefaultEnpoint1() {
		String defaultEndpoint1 =  this.properties.getProperty("default.kml.file", "");
		File file = new File(defaultEndpoint1);
		if(file.exists()){
			defaultEndpoint1 = file.getAbsolutePath();
		}	
		return defaultEndpoint1;
	}

	public String getDefaultEnpoint2() {
		return this.properties.getProperty("default.feed.url", "");
	}

	public ISecurity getSecurity() {
		try{
			String securityClassName =  this.properties.getProperty("sync.security", LocalSecurity.class.getName());
			ISecurity security = makeNewInstance(securityClassName);
			return security;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private ISecurity makeNewInstance(String securityClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class clazz = Class.forName(securityClassName);
		ISecurity security = (ISecurity)clazz.newInstance();
		return security;
	}
}
