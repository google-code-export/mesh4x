package com.mesh4j.sync.properties;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.security.LoggedInIdentityProvider;
import com.mesh4j.sync.validations.MeshException;

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

	public IIdentityProvider getIdentityProvider() {
		try{
			String identityProviderClassName =  this.properties.getProperty("sync.identity.provider", LoggedInIdentityProvider.class.getName());
			IIdentityProvider security = makeNewInstance(identityProviderClassName);
			return security;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private IIdentityProvider makeNewInstance(String identityProviderClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class clazz = Class.forName(identityProviderClassName);
		IIdentityProvider identityProvider = (IIdentityProvider)clazz.newInstance();
		return identityProvider;
	}

	public String getBaseDirectory() {
		return this.properties.getProperty("default.base.directory", "c:\\");
	}

	public int getInt(String key) {
		return Integer.valueOf(this.properties.getProperty(key, "0"));
	}

	public String getString(String key) {
		return this.properties.getProperty(key, "");
	}
}
