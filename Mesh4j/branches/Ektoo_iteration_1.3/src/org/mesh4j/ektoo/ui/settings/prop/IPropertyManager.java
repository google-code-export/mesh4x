package org.mesh4j.ektoo.ui.settings.prop;

import org.mesh4j.ektoo.ui.settings.encryption.EncryptionException;
import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;





public interface IPropertyManager {

	//public void load();

	public void save();

	public void setPropertyAsEncrypted(String property, String plainText)throws EncryptionException;

	public String getPropertyAsDecrepted(String property)throws EncryptionException;

	public void setProperty(String property, String plainText);

	public String getProperty(String property);
	
	public void setEncryptionUtil(IEncryptionUtil encryptionUtil); 
	
}
