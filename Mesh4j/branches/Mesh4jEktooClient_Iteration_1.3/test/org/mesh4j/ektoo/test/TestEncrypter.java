package org.mesh4j.ektoo.test;

import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil.ALGORITHM;

public class TestEncrypter {

	
	public static void main(String[] args) {
		EncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		System.out.println(encryptionUtil.encrypt("java123456"));
	}

}
