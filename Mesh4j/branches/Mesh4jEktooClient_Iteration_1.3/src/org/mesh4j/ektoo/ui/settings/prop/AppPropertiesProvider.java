package org.mesh4j.ektoo.ui.settings.prop;

import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil.ALGORITHM;


public class AppPropertiesProvider {

	private static IPropertyManager propertyManager = null;
	private static final String MESH4J_PROPERTIES = "mesh4j_settings.properties";
	
	private AppPropertiesProvider(){
		
	}
	
	public static  IPropertyManager getPropetyManager(){
		if(propertyManager != null){
			return propertyManager;
		}
		propertyManager = new PropertyManager(getPropertyLocation());
		IEncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		propertyManager.setEncryptionUtil(encryptionUtil);
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
}
