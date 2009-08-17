package org.mesh4j.ektoo;

import java.io.File;
import java.io.IOException;

import org.mesh4j.ektoo.ui.settings.prop.IPropertyManager;

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

}
