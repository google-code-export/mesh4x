package org.mesh4j.ektoo;
import static org.mesh4j.ektoo.ui.settings.prop.AppPropertiesProvider.getPropetyManager;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.settings.prop.AppProperties;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class Util {
	private final  static Log LOOGER = LogFactory.getLog(Util.class);   

	
	public static boolean isInteger(String integerAsString){
		Pattern pattern = Pattern.compile("^\\d*$");
		Matcher matcher = pattern.matcher(integerAsString);
		if(matcher.matches()){
			return true;
		}
		return false;
	}
	
	//TODO need to improve
	public static int getAsInteger(String integerAsString){
		try{
			return  Integer.parseInt(integerAsString);	
		} catch (Exception ec){
			LOOGER.error(ec);
		}
		return new Integer(null);
	}
	
	public static String getProperty(String propName){
		return getPropetyManager().getProperty(propName);
	}
	
	public static String getPropertyAsDecrypted(String propName){
		return getPropetyManager().getPropertyAsDecrypted(propName);
	}
	
	/**
	 * in case of regular file name is missing default file will be
	 * loaded. 
	 * @param propName
	 * @return
	 */
	public static String getFileName(String propName){
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
	
	public static IIdentityProvider getIdentityProvider() {
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
