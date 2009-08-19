package org.mesh4j.ektoo.test;

import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil.ALGORITHM;

public class TestEncrypter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		System.out.println(encryptionUtil.encrypt("root"));
		System.out.println(encryptionUtil.encrypt("3306"));
		System.out.println(encryptionUtil.encrypt("localhost"));
		System.out.println(encryptionUtil.encrypt("mesh4xdb"));

	}

}
