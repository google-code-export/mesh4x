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
import org.mesh4j.ektoo.ui.settings.EktooProperties;
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
	
	public  String getPropertyAsDecrepted(String property) throws MeshException {
		String encrypted = oldProperties.getProperty(property);
	    return encryptionUtil.decrypt(encrypted);
   }

	public   void setPropertyAsEncrypted(String property, String plainText)throws MeshException {
    	  String encrypted = encryptionUtil.encrypt(plainText);
      	  newProperties.setProperty(property, encrypted);
	}

	private  void load() {
		// load values in the old properties from properties file
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
		// TODO Auto-generated method stub
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

	public   void setProperty(String property, String plainText) {
		newProperties.setProperty(property,plainText);
	}

	private  void loadDefault(){
		oldProperties.put(EktooProperties.CLOUD_ROOT_URI,EktooProperties.CLOUD_ROOT_URI_DEFAULT);
		oldProperties.put(EktooProperties.CLOUD_MESH_NAME,EktooProperties.CLOUD_MESH_NAME_DEFAULT);
		oldProperties.put(EktooProperties.CLOUD_DATASET_NAME,EktooProperties.CLOUD_DATASET_NAME_DEFAULT);
		
		oldProperties.put(EktooProperties.LANGUAGE,EktooProperties.LANGUAGE_DEFAULT);
		oldProperties.put(EktooProperties.PATH_SOURCE_DIR,EktooProperties.PATH_SOURCE_DIR_DEFAULT);
		oldProperties.put(EktooProperties.PATH_TARGET_DIR,EktooProperties.PATH_TARGET_DIR_DEFAULT);
		
	}

	/**
	 * @param encryptionUtil The encryptionUtil to set.
	 */
	public void setEncryptionUtil(IEncryptionUtil encryptionUtil) {
		this.encryptionUtil = encryptionUtil;
	}

}
