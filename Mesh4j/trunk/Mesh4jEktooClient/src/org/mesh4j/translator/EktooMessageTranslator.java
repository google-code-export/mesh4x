package org.mesh4j.translator;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EktooMessageTranslator 
{
	private static final Log LOGGER = LogFactory.getLog(EktooMessageTranslator.class);
	private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( Locale.getDefault().toString() );

	public static String translate(String key)
	{
	  String messageText;
		try
		{
			messageText = RESOURCE_BUNDLE.getString(key);
		}
		catch (Exception e) 
		{
		  e.printStackTrace();
		  if(LOGGER.isInfoEnabled())
			{
				LOGGER.info("Resource Bundle for key <"+ key +"> does not exist.");
			}
			messageText = key;
		}
		return messageText;
	}

	public static String translate(String key, Object ... args) 
	{
		String resourceValue = translate(key);
		String message = MessageFormat.format(resourceValue, args);
		return message; 
	}
}
