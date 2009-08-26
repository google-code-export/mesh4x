package org.mesh4j.translator;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;

/**
 * 
 * There must be a file named following RESOURCE_BASE_NAME.
 * 
 */
public class MessageProvider {
	
	private static final Log LOGGER = LogFactory.getLog(MessageProvider.class);
	private static ResourceBundle RESOURCE_BUNDLE = null;
	private static final String RESOURCE_BASE_NAME = "ektoo_resources";
	
	public final  static String LANGUAGE_ENGLISH = "en";
	public final  static String LANGUAGE_SPANISH = "es";
	
	public final static String COUNTRY_US = "US";
	public final  static String COUNTRY_SPAIN = "ES";
	
	private MessageProvider(){
	}
	
	
	/**
	 * there must be default file named <code>RESOURCE_BUNDLE<code>
	 * other local specific file must follow the java internationalization
	 * rule which is bundlename + _ +language + _ + country
	 * for example if the base bundle name is MessageBundle then for
	 * English Locale file name will be MessageBundle_en_US.properties.
	 */
	public static  void init(String language,String country){
		
		Locale currentLocale = new Locale(language,country);
		try{
			RESOURCE_BUNDLE = ResourceBundle.getBundle(RESOURCE_BASE_NAME,currentLocale);
		} catch (MissingResourceException e){
			LOGGER.error(e.getMessage(), e);
			MessageDialog.showErrorMessage(null, "Locale specific resource file is not available " +
					"\n application will exit");
			System.exit(0);
		}
	}
	
	public static String translate(String key) {
		String messageText;
		try {
			messageText = RESOURCE_BUNDLE.getString(key);
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
