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
	

	public PropertyManager(String properyFile){
		Guard.argumentNotNullOrEmptyString(properyFile, "properyFile");
		file = new File(properyFile);
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
	private  void loadDefault(){
		oldProperties.put(AppProperties.PATH_SOURCE_ACCESS, 
				AppProperties.PATH_SOURCE_ACCESS_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_ACCESS, 
				AppProperties.PATH_TARGET_ACCESS_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_EXCEL, 
				AppProperties.PATH_SOURCE_EXCEL_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_EXCEL, 
				AppProperties.PATH_TARGET_EXCEL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_KML, 
				AppProperties.PATH_TARGET_KML_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_KML, 
				AppProperties.PATH_TARGET_KML_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_RSS, 
				AppProperties.PATH_SOURCE_RSS_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_RSS, 
				AppProperties.PATH_TARGET_RSS_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_ATOM, 
				AppProperties.PATH_SOURCE_ATOM_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_ATOM, 
				AppProperties.PATH_TARGET_ATOM_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_FOLDER, 
				AppProperties.PATH_SOURCE_FOLDER_DEFAULT_VALUE);
		oldProperties.put(AppProperties.PATH_TARGET_FOLDER, 
				AppProperties.PATH_TARGET_FOLDER_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PATH_SOURCE_ZIP, 
				AppProperties.PATH_SOURCE_ZIP_DEFAULT_VALUE);
	
		//cloud default settings
		oldProperties.put(AppProperties.CLOUD_ROOT_URI, 
				AppProperties.CLOUD_ROOT_URI_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.CLOUD_MESH_NAME, 
				AppProperties.CLOUD_MESH_NAME_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.CLOUD_DATASET_NAME, 
				AppProperties.CLOUD_DATASET_NAME_DEFAULT_VALUE);
		
		//mysql default settings
		oldProperties.put(AppProperties.USER_NAME_MYSQL, 
				AppProperties.USER_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.USER_PASSWORD_MYSQL, 
				AppProperties.USER_PASSWORD_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.HOST_NAME_MYSQL, 
				AppProperties.HOST_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.PORT_MYSQL, 
				AppProperties.PORT_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.DATABASE_NAME_MYSQL, 
				AppProperties.DATABASE_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.TABLE_NAME_MYSQL, 
				AppProperties.TABLE_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.DATABASE_NAME_MYSQL, 
				AppProperties.DATABASE_NAME_MYSQL_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.TABLE_NAME_MYSQL, 
				AppProperties.TABLE_NAME_MYSQL_DEFAULT_VALUE);
	
		//google spreadsheet
		oldProperties.put(AppProperties.USER_NAME_GOOGLE, 
				AppProperties.USER_NAME_GOOGLE_DEFAULT_VALUE);
		
		oldProperties.put(AppProperties.USER_PASSWORD_GOOGLE, 
				AppProperties.USER_PASSWORD_GOOGLE_DEFAULT_VALUE);
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

	public void setEncryptionUtil(IEncryptionUtil encryptionUtil) {
		this.encryptionUtil = encryptionUtil;
	}

}
