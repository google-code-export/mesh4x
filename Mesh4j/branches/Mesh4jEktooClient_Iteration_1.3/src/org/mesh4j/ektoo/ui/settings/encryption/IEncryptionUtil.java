package org.mesh4j.ektoo.ui.settings.encryption;

import org.mesh4j.sync.validations.MeshException;



public interface IEncryptionUtil {

	public  String decrypt(String string)throws MeshException ;
	public  String encrypt(String string)throws MeshException ;

}