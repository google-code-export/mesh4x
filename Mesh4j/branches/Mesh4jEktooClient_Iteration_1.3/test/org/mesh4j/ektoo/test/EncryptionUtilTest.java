package org.mesh4j.ektoo.test;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.IEncryptionUtil;
import org.mesh4j.ektoo.ui.settings.encryption.EncryptionUtil.ALGORITHM;
import org.mesh4j.sync.validations.MeshException;

public class EncryptionUtilTest {

	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfAlgorithmIsNull(){
		new EncryptionUtil("",null); 
	}
	
	@Test(expected = MeshException.class)
	public void ShouldGenerateExceptionIfKeyLengthLessThan24(){
		new EncryptionUtil("test",EncryptionUtil.ALGORITHM.DES); 
	}
	
	@Test
	public void ShouldEncryptDecryptStringWithDefaultKey(){
		
		String plainText = "root";
		IEncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		
		String encryptedString = encryptionUtil.encrypt(plainText);
		Assert.assertNotNull(encryptedString);
		
		String decryptedString = encryptionUtil.decrypt(encryptedString);
		Assert.assertNotNull(decryptedString);
		
		Assert.assertNotSame(encryptedString, decryptedString);
		Assert.assertNotSame(plainText, encryptedString);
		
		Assert.assertEquals(plainText, decryptedString);
	}
	
	@Test
	public void ShouldEncryptDecryptStringWithProvidedKey(){
		
		String key = ",'=/=+,*&abcd48dra((1845554523))[99]+ 45=?";
		String plainText = "root";
		IEncryptionUtil encryptionUtil = new EncryptionUtil(key,ALGORITHM.DES);
		
		String encryptedString = encryptionUtil.encrypt(plainText);
		Assert.assertNotNull(encryptedString);
		
		String decryptedString = encryptionUtil.decrypt(encryptedString);
		Assert.assertNotNull(decryptedString);
		
		Assert.assertNotSame(encryptedString, decryptedString);
		Assert.assertNotSame(plainText, encryptedString);
		
		Assert.assertEquals(plainText, decryptedString);
	}
	
	@Test
	public void ShouldBeSameTwoEqualStringAfterEncryption(){
		String key = ",'=/=+,*&abcd48dra((1845554523))[99]+ 45=?";
		IEncryptionUtil encryptionUtil = new EncryptionUtil(key,ALGORITHM.DES);
		Assert.assertEquals(encryptionUtil.encrypt("root"), encryptionUtil.encrypt("root"));
	}
	
	@Test
	public void ShouldNotSameTwoEqualStringIfKeyisDifferent(){
		String key1 = ",'=/=+,*&abcd48dra((1845554523))[99]+ 45=?";
		String key2 = ",'=/=+,*&abcd48dra((1845554523))?";
		String enString1 = new EncryptionUtil(key1,ALGORITHM.DES).encrypt("root");
		String enString2 = new EncryptionUtil(key2,ALGORITHM.DES).encrypt("root");
		Assert.assertNotSame(enString1,enString2);
	}
	
	@Test
	public void ShouldNotSameTwoEqualStringIfAlgorithmisDifferent(){
		String key = ",'=/=+,*&abcd48dra((1845554523))[99]+ 45=?";
		String enString1 = new EncryptionUtil(key,ALGORITHM.DES).encrypt("root");
		String enString2 = new EncryptionUtil(key,ALGORITHM.DESede).encrypt("root");
		Assert.assertNotSame(enString1,enString2);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfStringToBeEncryptedIsNullOrEmpty(){
		String plainText = "";
		IEncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		encryptionUtil.encrypt(plainText);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void ShouldGenerateExceptionIfStringToBeDecryptedIsNull(){
		IEncryptionUtil encryptionUtil = new EncryptionUtil("",ALGORITHM.DES);
		System.out.println(encryptionUtil.decrypt(null));
	}
}
