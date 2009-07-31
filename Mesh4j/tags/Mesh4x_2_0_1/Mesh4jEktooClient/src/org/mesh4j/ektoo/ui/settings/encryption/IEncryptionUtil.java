package org.mesh4j.ektoo.ui.settings.encryption;



public interface IEncryptionUtil {

	public  String decrypt(String string)throws EncryptionException ;
	public  String encrypt(String string)throws EncryptionException ;

}