package com.mesh4j.sync.translator;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageTranslator {
	
	// TODO (JMT) REFACTORING: Spring, inject file name

	private static final Log Logger = LogFactory.getLog(MessageTranslator.class);
	private static final ResourceBundle Resources = ResourceBundle.getBundle("mesh4j_resources");
	
	public static String translate(String key){
		String messageText;
		try{
			messageText = Resources.getString(key);
		}catch (Exception e) {
			if(Logger.isInfoEnabled()){
				Logger.info("Resource Bundle for key <"+ key +"> does not exist.");
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
