package org.mesh4j.ektoo.ui.settings.prop;

import java.io.File;
import java.io.IOException;

import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil.ALGORITHM;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

/**
 * Singletone class make sure only one instance of IPropertyManager 
 */
public class AppPropertiesProvider {
	
	/**
	 * the property manager which is the application gateway to retrieve and 
	 * load property from property file.
	 */
	private static IPropertyManager propertyManager = null;
	private static final String MESH4J_PROPERTIES = "mesh4j_settings.properties";
	
	private AppPropertiesProvider(){}

	
	/**
	 * Creates the instance of property manager with the necessary settings
	 * @return IPropertyManager as the instance of PropertyManager
	 */
	public static  IPropertyManager getPropetyManager(){
		if(propertyManager != null){
			return propertyManager;
		}
		IEncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		propertyManager = new PropertyManager(getPropertyLocation(),encryptionUtil);
		return propertyManager;
	}
	
	private static String getPropertyLocation(){
		String settingsPropAbsoluteLocation = "";
		settingsPropAbsoluteLocation = getCurrentDirectory() + "\\properties\\"
		+ MESH4J_PROPERTIES;
		return settingsPropAbsoluteLocation;
	}
	
	private static String getCurrentDirectory() {
		return System.getProperty("user.dir");
	}
	
	
	/**
	 * in case of regular file name is missing default file will be
	 * loaded. 
	 * @param propName which actually represents the prop key in property file
	 * @return String as the absolute path of file name  specified bye propName.
	 */
	public static String getFilePath(String propName){
		String fileName = "";
		
		fileName = getPropetyManager().getProperty(propName);
		
		if(fileName == null || fileName.trim().equals("") ){
			fileName =  getDefaultFileName(propName);
		}
		
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			return "";
		}
	}
	
	private static String getDefaultFileName(String propName){
		String defaultFileName = propName + ".default";
		String fileName = getPropetyManager().getProperty(defaultFileName);
		return fileName;
	}
	
	public static String getProperty(String propName){
		return getPropetyManager().getProperty(propName);
	}
	
	public static String getPropertyAsDecrypted(String propName){
		return getPropetyManager().getPropertyAsDecrypted(propName);
	}
	
	public static IIdentityProvider getSyncIdentityProvider() {
		try {
			String identityProviderClassName = getPropetyManager().getProperty(
					AppProperties.SYNC_IDENTITY_PROVIDER, LoggedInIdentityProvider.class
							.getName());
			IIdentityProvider security = (IIdentityProvider) makeNewInstance(identityProviderClassName);
			return security;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private static Object makeNewInstance(String className)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Class clazz = Class.forName(className);
		return clazz.newInstance();
	}
	
}
