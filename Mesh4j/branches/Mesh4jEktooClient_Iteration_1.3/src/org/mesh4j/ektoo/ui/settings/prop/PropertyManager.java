package org.mesh4j.ektoo.ui.settings.prop;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class PropertyManager  implements IPropertyManager {

	private static final Log logger = LogFactory.getLog(PropertyManager.class);

	// old properties saved in the file system previously
	private  Properties oldProperties = new Properties();
	// new properties are set by user and not yet saved in the file
	private  Properties newProperties = new Properties();
	private  IEncryptionUtil encryptionUtil;
	private  File file;
	

	@Deprecated
	public PropertyManager(String properyFile){
		Guard.argumentNotNullOrEmptyString(properyFile, "properyFile");
		file = new File(properyFile);
		load();
	}
	
	public PropertyManager(String properyFile,IEncryptionUtil encryptionUtil){
		Guard.argumentNotNullOrEmptyString(properyFile, "properyFile");
		Guard.argumentNotNull(encryptionUtil, "encryptionUtil");
		file = new File(properyFile);
		this.encryptionUtil = encryptionUtil;
		load();
	}
	
	
	public  String getPropertyAsDecrypted(String property) throws MeshException {
		String encrypted = oldProperties.getProperty(property);
	    return encryptionUtil.decrypt(encrypted);
   }

	public   void setPropertyAsEncrypted(String property, String plainText)throws MeshException {
    	  String encrypted = encryptionUtil.encrypt(plainText);
      	  newProperties.setProperty(property, encrypted);
	}

	private  void load() {
		try {
			if(!file.exists()){
				loadDefault();
				file.createNewFile();
				oldProperties.store(new FileOutputStream(file),"Application Properties");
				return;
			}			
			oldProperties.load(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			logger.error("properties file not found");
		} catch (IOException e) {
			logger.error("Can not read from  properties file");
		}		
	}

	@SuppressWarnings("unchecked")
	public  void save() {
		// store properties if old and new has diff
		if (newProperties.isEmpty()) {
			// do nothing , old properties will remain as it was
			return;
		}
		// iterate new properties and get key and value and replace the
		// old property using new key value
		Enumeration newKeys = newProperties.keys();
		while (newKeys.hasMoreElements()) {
			String key = (String) newKeys.nextElement();
			String keyValue = newProperties.getProperty(key);
			if(key.equals(AppProperties.PATH_SOURCE_KML)){
				System.out.println("value:" + keyValue);
			}
			
			if (!oldProperties.containsKey(key)) {
				oldProperties.put(key, keyValue);
			} else {
				oldProperties.setProperty(key, keyValue);
			}
		}
		try{ 
			oldProperties.store(new FileOutputStream(file),
					"Application Properties");			
		} catch (FileNotFoundException e) {
			logger.error("properties file not found");
		} catch (IOException e) {
			logger.error("Can not write to file ");
		}

	}

	public  String getProperty(String property) {
		return oldProperties.getProperty(property);
	}

	public  String getProperty(String property,String defaultValue) {
		return oldProperties.getProperty(property,defaultValue);
	}

	
	public   void setProperty(String property, String plainText) {
		newProperties.setProperty(property,plainText);
	}

	//in case of property file missing application will feed property to itself.
	
	private void loadDefault(){
		loadProperty(".default");
		loadProperty("");
	}
	
	private  void loadProperty(String extension){
		
		oldProperties.put(AppProperties.LANGUAGE + extension, 
				AppProperties.LANGUAGE_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_ACCESS + extension, 
				AppProperties.PATH_SOURCE_ACCESS_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_ACCESS + extension, 
				AppProperties.PATH_TARGET_ACCESS_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_EXCEL + extension, 
				AppProperties.PATH_SOURCE_EXCEL_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_EXCEL + extension, 
				AppProperties.PATH_TARGET_EXCEL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_KML + extension, 
				AppProperties.PATH_TARGET_KML_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_KML + extension, 
				AppProperties.PATH_TARGET_KML_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_RSS + extension, 
				AppProperties.PATH_SOURCE_RSS_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_RSS + extension, 
				AppProperties.PATH_TARGET_RSS_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_ATOM + extension, 
				AppProperties.PATH_SOURCE_ATOM_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_ATOM + extension, 
				AppProperties.PATH_TARGET_ATOM_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_FOLDER + extension, 
				AppProperties.PATH_SOURCE_FOLDER_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_FOLDER + extension, 
				AppProperties.PATH_TARGET_FOLDER_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_ZIP + extension, 
				AppProperties.PATH_SOURCE_ZIP_DEFAULT_VALUE);
	
		//cloud default settings
		oldProperties.put(AppProperties.CLOUD_ROOT_URI + extension, 
				AppProperties.CLOUD_ROOT_URI_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.CLOUD_MESH_NAME + extension, 
				AppProperties.CLOUD_MESH_NAME_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.CLOUD_DATASET_NAME + extension, 
				AppProperties.CLOUD_DATASET_NAME_DEFAULT_VALUE);
		
		//mysql default settings
		oldProperties.put(AppProperties.USER_NAME_MYSQL + extension, 
				AppProperties.USER_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.USER_PASSWORD_MYSQL + extension, 
				AppProperties.USER_PASSWORD_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.HOST_NAME_MYSQL + extension, 
				AppProperties.HOST_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PORT_MYSQL + extension, 
				AppProperties.PORT_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.DATABASE_NAME_MYSQL + extension, 
				AppProperties.DATABASE_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.TABLE_NAME_MYSQL + extension, 
				AppProperties.TABLE_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.DATABASE_NAME_MYSQL + extension, 
				AppProperties.DATABASE_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.TABLE_NAME_MYSQL + extension, 
				AppProperties.TABLE_NAME_MYSQL_DEFAULT_VALUE);
	
		//google spreadsheet
		oldProperties.put(AppProperties.USER_NAME_GOOGLE + extension, 
				AppProperties.USER_NAME_GOOGLE_DEFAULT_VALUE);
		
		String passAsEncrypted = this.encryptionUtil.encrypt(AppProperties.USER_PASSWORD_GOOGLE_DEFAULT_VALUE);
		oldProperties.put(AppProperties.USER_PASSWORD_GOOGLE + extension , 
				passAsEncrypted);
		
		
//		oldProperties.put(AppProperties.USER_PASSWORD_GOOGLE_ENCRYPTED, 
//				AppProperties.USER_PASSWORD_GOOGLE_DEFAULT_VALUE);
		
		//end google spreadsheet	
		
		
		oldProperties.put(AppProperties.GOOGLE_GEO_CODER_KEY, 
				AppProperties.GOOGLE_GEO_CODER_KEY_VALUE);
		
		oldProperties.put(AppProperties.LOOK_AND_FEEL_CLASS_NAME, 
				AppProperties.LOOK_AND_FEEL_CLASS_NAME_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.URL_GOOGLE_DOCS, 
				AppProperties.URL_GOOGLE_DOCS_VALUE);
		
		oldProperties.put(AppProperties.URL_MESH4X, 
				AppProperties.URL_MESH4X_VALUE);
		
		oldProperties.put(AppProperties.URL_MESH4X_EKTOO, 
				AppProperties.URL_MESH4X_EKTOO_VALUE);
		
		oldProperties.put(AppProperties.BASE_DIRECTORY, 
				AppProperties.BASE_DIRECTORY_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.SYNC_IDENTITY_PROVIDER, 
				AppProperties.SYNC_IDENTITY_PROVIDER_VALUE);
	}

//	public void setEncryptionUtil(IEncryptionUtil encryptionUtil) {
//		this.encryptionUtil = encryptionUtil;
//	}

}
