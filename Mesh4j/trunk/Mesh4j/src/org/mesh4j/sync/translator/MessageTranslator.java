package org.mesh4j.sync.translator;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageTranslator {

	private static final Log LOGGER = LogFactory.getLog(MessageTranslator.class);
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("mesh4j_resources");
	
	public static String translate(String key){
		String messageText;
		try{
			messageText = RESOURCE_BUNDLE.getString(key);
		}catch (Exception e) {
			if(LOGGER.isInfoEnabled()){
				LOGGER.info("Resource Bundle for key <"+ key +"> does not exist.");
			}
			messageText = key;
		}
		return messageText;
	}

	public static String translate(String key, Object ... args) {
		String resourceValue = translate(key);
		String message = MessageFormat.format(resourceValue, args);
		return message; 
	}
}
