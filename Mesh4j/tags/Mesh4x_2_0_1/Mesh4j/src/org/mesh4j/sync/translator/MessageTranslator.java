package org.mesh4j.sync.translator;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageTranslator {

	private static final Log LOGGER = LogFactory.getLog(MessageTranslator.class);
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("mesh4j_resources");
	private static ResourceBundle RESOURCE_BUNDLE_APP = null;
	
	public static void setResourceBundle(String bundleName){
		RESOURCE_BUNDLE_APP = ResourceBundle.getBundle(bundleName);
	}
	
	public static String translate(String key){
		String messageText = translate(key, RESOURCE_BUNDLE_APP);
		if(messageText == null){
			messageText = translate(key, RESOURCE_BUNDLE);
			if(messageText == null){
				messageText = key;
			}
		}
		return messageText;
	}
	
	private static String translate(String key, ResourceBundle resourceBundle){
		String messageText = null;
		try{
			if(resourceBundle != null){
				messageText = resourceBundle.getString(key);
			}
		}catch (Exception e) {
			if(LOGGER.isInfoEnabled()){
				LOGGER.info("Resource Bundle for key <"+ key +"> does not exist.");
			}
		}
		return messageText;
	}
	
	public static String translate(String key, Object ... args) {
		String resourceValue = translate(key);
		String message = MessageFormat.format(resourceValue, args);
		return message; 
	}
}
