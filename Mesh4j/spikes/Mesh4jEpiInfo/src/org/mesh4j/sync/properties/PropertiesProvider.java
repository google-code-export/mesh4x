package org.mesh4j.sync.properties;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;


public class PropertiesProvider {

	// MODEL VARIABLES
	private Properties properties;
	
	// BUSINESS METHODS
	public PropertiesProvider(){
		super();
		initialize("mesh4j.properties");
	}
	
	public PropertiesProvider(String resourceName){
		super();
		initialize(resourceName);
	}
	
	private void initialize(String resourceName) {
		try{
			FileReader reader = new FileReader(resourceName);
			Properties prop = new Properties();
			prop.load(reader);
			this.properties = prop;
			reader.close();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public String getDefaultDataSource() {
		String defaultValue =  this.properties.getProperty("default.mdb.file", "");
		File file = new File(defaultValue);
		if(file.exists() && file.canRead()){
			defaultValue = file.getAbsolutePath();
		}	
		return defaultValue;
	}

	public String getDefaultTable() {
		return this.properties.getProperty("default.mdb.table", "");
	}
	
	public String getDefaultPhoneNumber() {
		return this.properties.getProperty("default.phone.number", "");
	}

	public IIdentityProvider getIdentityProvider() {
		try{
			String identityProviderClassName =  this.properties.getProperty("sync.identity.provider", LoggedInIdentityProvider.class.getName());
			IIdentityProvider security = (IIdentityProvider)makeNewInstance(identityProviderClassName);
			return security;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object makeNewInstance(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class clazz = Class.forName(className);
		return clazz.newInstance();
	}

	public String getBaseDirectory() {
		return this.properties.getProperty("default.base.directory", getCurrentDirectory());
	}

	public String getCurrentDirectory(){
		return System.getProperty("user.dir");
	}
	
	public int getInt(String key) {
		return Integer.valueOf(this.properties.getProperty(key, "0"));
	}

	public String getString(String key) {
		return this.properties.getProperty(key, "");
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf(this.properties.getProperty(key, "true"));
	}

	public Object getInstance(String key, Object defaultInstanceIfAbsent) {
		try{
			String className = this.getString(key);
			if(className == null || className.length() == 0){
				return defaultInstanceIfAbsent;
			}
			return makeNewInstance(className);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
}
