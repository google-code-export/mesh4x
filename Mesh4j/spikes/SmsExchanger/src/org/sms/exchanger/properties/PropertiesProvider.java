package org.sms.exchanger.properties;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesProvider {
	
	private final static Log LOGGER = LogFactory.getLog(PropertiesProvider.class);

	// MODEL VARIABLES
	private Properties properties;
	
	// BUSINESS METHODS
	public PropertiesProvider() throws IOException{
		this("sms.properties");
	}
	
	public PropertiesProvider(String resourceName) throws IOException{
		super();
		initialize(resourceName);
	}
	
	private void initialize(String resourceName) throws IOException {
		Properties prop = new Properties();
		
		File file = new File(resourceName);
		if(file.exists()){
			FileReader reader = new FileReader(resourceName);
			prop.load(reader);
			reader.close();
		}		
		this.properties = prop;
	}

	public int getInt(String key, String defaultValue) {
		String value = System.getProperty(key);
		if(value == null){
			value = this.properties.getProperty(key, defaultValue);
		}
		return Integer.valueOf(value);
	}

	public String getString(String key) {
		return getString(key, "");
	}
	
	public String getString(String key, String defaultValue) {
		String value = System.getProperty(key);
		if(value == null){
			value = this.properties.getProperty(key, defaultValue);
		}
		return value;
	}

	public boolean getBoolean(String key) {
		String value = System.getProperty(key);
		if(value == null){
			value = this.properties.getProperty(key, "true");
		}
		return Boolean.valueOf(value);
	}

	@SuppressWarnings("unchecked")
	public Object getInstance(String key, Object defaultInstanceIfAbsent) {
		try{
			String className = this.getString(key);
			if(className == null || className.length() == 0){
				return defaultInstanceIfAbsent;
			}
			Class clazz = Class.forName(className);
			return clazz.newInstance();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			return defaultInstanceIfAbsent;
		}
	}
	
	public String getCurrentDirectory(){
		return System.getProperty("user.dir");
	}

	public void setProperty(String key, String value) {
		this.properties.setProperty(key, value);		
	}
}
