package org.mesh4j.translator;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EktooMessageTranslator {
	
	private static final Log LOGGER = LogFactory.getLog(EktooMessageTranslator.class);
	
	private static String defaultLocate = "en_US";
	private static String systemLocale = Locale.getDefault().toString();
  
  private static ResourceBundle DEFAULT_BUNDLE =  ResourceBundle.getBundle( defaultLocate ); 

	private static ResourceBundle RESOURCE_BUNDLE = (defaultLocate.equals(systemLocale)) 
	                                                  ? DEFAULT_BUNDLE 
	                                                      : ( ResourceBundle.getBundle(systemLocale) == null)  
	                                                        ? DEFAULT_BUNDLE 
	                                                            : ResourceBundle.getBundle(systemLocale);

	                                                  
	public static String translate(String key) 
	{
		String messageText = null;
		try 
		{
			messageText = RESOURCE_BUNDLE.getString(key);
		} 
		catch (Exception e) 
		{
		  LOGGER.error(e.getMessage(), e);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("System Resource Bundle for key <" + key
						+ "> does not exist.");
			}
		}
		
		// if key is not in System Resource Bundle, check if it is in Default Resource Bundle
		if (messageText == null &&  !RESOURCE_BUNDLE.getLocale().toString().equals(DEFAULT_BUNDLE.getLocale().toString()))
    {
      try 
      {
        messageText = DEFAULT_BUNDLE.getString(key);
      } 
      catch (Exception e) 
      {
        LOGGER.error(e.getMessage(), e);
        if (LOGGER.isInfoEnabled()) {
          LOGGER.info("Default Resource Bundle for key <" + key
              + "> does not exist.");
        }
      }      
    }
		
		if (messageText == null)
		  messageText = key;
		
		return messageText;
	}

	public static String translate(String key, Object... args) {
		String resourceValue = translate(key);
		String message = MessageFormat.format(resourceValue, args);
		return message;
	}
}
