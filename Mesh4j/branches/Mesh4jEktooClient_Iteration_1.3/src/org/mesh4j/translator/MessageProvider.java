package org.mesh4j.translator;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;

/**
 * Provides locale specific text according to java Internationalization rule.
 * Application must have resource file which is located at <code>RESOURCE_BASE_NAME</code> <li>
 * corresponding locale specific data must be suffixed with <code>RESOURCE_BASE_NAME</code><br>
 * For example 
 * if <code>RESOURCE_BASE_NAME</code> = ektoo_resources <br> 
 * then for English it will be ektoo_resources_en_US
 * 
 * NOTE: if the specific resource file  is not found it will load<br>
 * default resource as followed bye <code>RESOURCE_BASE_NAME<code> 
 * but in such case in absence of default resource bundle application <br>
 * will  shutdown by calling
 * System.exit(0);
 * 
 * If any specific property(key) is not found in the resource file then that 
 * property is loaded from the default resource file. 
 * 
 */
public class MessageProvider {
	
	private static final Log LOGGER = LogFactory.getLog(MessageProvider.class);
	private static ResourceBundle resourceBundle = null;
	private static final String RESOURCE_BASE_NAME = "ektoo_resources";
	
	public final  static String LANGUAGE_ENGLISH = "en";
	public final  static String LANGUAGE_SPANISH = "es";
	public final  static String LANGUAGE_BANGLA = "bn";
	
	
	public final static String COUNTRY_US = "US";
	public final  static String COUNTRY_ARGENTINA = "AR";
	public final  static String COUNTRY_BANGLADESH = "BD";
	
	
	private MessageProvider(){
	}
	
	
	/**
	 * Initialize locale.must be followed by java Internationalization rule
	 * @param language , the language name, For example, en for English
	 * @param country , the country name,For example US for United states
	 */
	public static  void init(String language,String country){
		Locale currentLocale = new Locale(language,country);
		init(currentLocale);
	}
	
	public static void init(Locale currentLocale){
		try{
			resourceBundle = ResourceBundle.getBundle(RESOURCE_BASE_NAME,currentLocale);
		} catch (MissingResourceException e){
			e.printStackTrace();
			LOGGER.error(e.getMessage(), e);
			MessageDialog.showErrorMessage(null, "Locale specific resource file is not available " +
					"\n application will exit");
			System.exit(0);
		}
	}
	
	
	
	public static String translate(String key) {
		String messageText;
		try {
			messageText = resourceBundle.getString(key);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);

			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Resource Bundle for key <" + key
						+ "> does not exist.");
			}
			messageText = key;
		}
		return messageText;
	}

	
	public static String translate(String key, Object... args) {
		String resourceValue = translate(key);
		String message = MessageFormat.format(resourceValue, args);
		return message;
	}
	
	
	
}
