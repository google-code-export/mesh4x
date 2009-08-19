package org.mesh4j.ektoo;

import java.io.File;
import java.io.IOException;

import org.mesh4j.ektoo.ui.settings.AppProperties;
import org.mesh4j.ektoo.ui.settings.prop.IPropertyManager;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class Util {


	
	public static String getFileName(IPropertyManager propertyManager,String propName){
		String fileName = propertyManager.getProperty(propName);
		try {
			return new File(fileName).getCanonicalPath();
		} catch (IOException e) {
			// nothing to do
			return "";
		}
	}
	
	public static IIdentityProvider getIdentityProvider(IPropertyManager propertyManager) {
		try {
			String identityProviderClassName = propertyManager.getProperty(
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
