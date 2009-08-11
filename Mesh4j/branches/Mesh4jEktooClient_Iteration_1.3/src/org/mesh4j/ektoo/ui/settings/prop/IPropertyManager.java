package org.mesh4j.ektoo.ui.settings.prop;

import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;
import org.mesh4j.sync.validations.MeshException;





public interface IPropertyManager {

	//public void load();

	public void save();

	public void setPropertyAsEncrypted(String property, String plainText)throws MeshException;

	public String getPropertyAsDecrepted(String property)throws MeshException;

	public void setProperty(String property, String plainText);

	public String getProperty(String property);
	
	public void setEncryptionUtil(IEncryptionUtil encryptionUtil); 
	
}
