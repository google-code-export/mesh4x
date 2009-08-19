package org.mesh4j.ektoo.ui.settings.prop;

import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;
import org.mesh4j.sync.validations.MeshException;


/**
 * getProperty method always return property from persistent store.
 * You cant expect a property by getProperty unless you call the
 * save method  after setting the property by setProperty method.
 * @author raju
 */


public interface IPropertyManager {

	//public void load();

	/**
	 * save all the property from memory to persistent store
	 */
	public void save();

	public void setPropertyAsEncrypted(String property, String plainText)throws MeshException;

	public String getPropertyAsDecrepted(String property)throws MeshException;

	/**
	 * Stores the property in memory , property stored in persistent
	 * store only after calls save method.
	 * @param property 
	 * @param plainText
	 */
	public void setProperty(String property, String plainText);

	/**
	 * @param property 
	 * @return property from persistent store
	 */
	public String getProperty(String property);
	
	public String getProperty(String property,String defaultValue);
	
	public void setEncryptionUtil(IEncryptionUtil encryptionUtil); 
	
}
