package org.mesh4j.ektoo.ui.settings.encryption;


import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * EncryptionUtil do the following
 * 1.encrypt
 * 2.decrypt
 * 
 * Example code:
 *
 * EncryptionUtil encryptionUtil = new EncryptionUtil("com.mesh4x.ektoo.properties",EncryptionUtil.ALGORITHM.DES);
 *		String encryptedString = encryptionUtil.encrypt("root");
 *		String decryptedString = encryptionUtil.decrypt(encryptedString);
 *
 * @author raju
 */
public class EncryptionUtil implements IEncryptionUtil{

	private static Log logger = LogFactory.getLog(EncryptionUtil.class);
    /** string format is for represent the encrypted character after encode */
	private static String stringFormat = "UTF8";
    /**
     * A  specification of  a cryptographic key.<p>
     * Its only purpose is to group  all key specifications.<p> 
     * this interface has no methods it just for specification.<p>
     * All key specifications has implemented this interface.<p> 
     * here we have used <code>DESKeySpec</code> 
     */
	private KeySpec	keySpec;
    /**
     * <code>SecretKeyFactory</code> is a factory for generating<p>
     * <code>SecretKey</code> with <code>key</code> according algorithm
     */
	private SecretKeyFactory	keyFactory;
    /**cryptographic cipher for encryption and decryption*/
	private Cipher	cipher;
   	public String key;
	public enum ALGORITHM{
		DES,
		MD5,
		DESede
	}
	/**
     * construction part of <code>EncryptionUtil</code>
     * moreover it initializes some necessary settings to prepare
     * the <code>EncryptionUtil</code> to be ready for encryption and
     * decryption.
     * 
     * @param key as String
	 * @throws EncryptionException 
	 */
	public EncryptionUtil(String key,ALGORITHM algorithm) throws EncryptionException{
     	try {
			this.key = key;
			logger.info("Encryption util initialized with key ..."+ key);
			//get the string as bytes
			byte[] keyByte = key.getBytes(stringFormat);
            //get the instance of specific algorithm key specification.
			keySpec = new DESKeySpec(keyByte);
            /*
             * get the instance of SecretKeyFactory this will help us to geneate
             * secret key in encrypted format
             */
			keyFactory = SecretKeyFactory.getInstance(algorithm.toString());
            //get instance of Cipher
			cipher = Cipher.getInstance(algorithm.toString());
		}
		catch (UnsupportedEncodingException e) {
	        throw new EncryptionException(e.getMessage(),e);
    	}
		catch (InvalidKeyException e){
			throw new EncryptionException(e.getMessage(),e);
		} 
		catch (NoSuchAlgorithmException e){ 
			throw new EncryptionException(e.getMessage(),e);
		}
        catch (NoSuchPaddingException e){
	    	throw new EncryptionException(e.getMessage(),e);
		}
	}
	/**
	 * 
     * @param toBeEncryptedString
	 * @return encrypted value as String or null if any exception occured
	 * @throws EncryptionException 
	 */
	public String encrypt(String toBeEncryptedString) throws EncryptionException{
		try {
	        //get the secret key according to sepcification of DESKeySpec
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
            //initialize the Chiper object
			cipher.init( Cipher.ENCRYPT_MODE, secretKey );
            //get the value of toBeEncryptedString as bytes of UTF8 format
			byte[] cleartext = toBeEncryptedString.getBytes( stringFormat );
            //now convert the UTF8 format bytes string into chiper bytes array
			byte[] ciphertext = cipher.doFinal( cleartext );
			//this is our encoder which is a BASE64Encoder
			EktooEncoder encoder = new EktooEncoder();
            //now encode the string 
			String encrtpStr = encoder.encode( ciphertext );
            /*
             * to more securate we need to convert to encrypted string into
             * base 16
             */
			return converToBase16(encrtpStr);
		} 
		catch (InvalidKeySpecException e){ 
			throw new EncryptionException(e.getMessage(),e);
		}
		catch (InvalidKeyException e){
			throw new EncryptionException(e.getMessage(),e);
		}
        catch (UnsupportedEncodingException e) {
        	throw new EncryptionException(e.getMessage(),e);
		}
        catch (IllegalBlockSizeException e){ 
        	throw new EncryptionException(e.getMessage(),e);
		} 
        catch (BadPaddingException e) {
        	throw new EncryptionException(e.getMessage(),e);
		}
		//return null;
	}
	
	/**
	 * convert bytes array to String value
     * 
 	 * @param bytes as bytes array
	 * @return stringBuffer as String
	 */
	private static String bytes2String( byte[] bytes ){
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++){
			stringBuffer.append( (char) bytes[i] );
		}
		return stringBuffer.toString();
	}
	/**
	 * Decrypt any String value
     *
     * @param encryptedString
	 * @return decrypted value as String or null if any exception occured
	 * @throws EncryptionException 
	 */
	public String decrypt( String encryptedString ) throws EncryptionException 
	{
		if ( encryptedString == null || encryptedString.trim().length() <= 0 ){
       		throw new EncryptionException( "encrypted string was null or empty" );
	    }
		try{
	        //get secret key
			SecretKey key = keyFactory.generateSecret( keySpec );
			
            //init chiper into decrypt mode
			cipher.init( Cipher.DECRYPT_MODE, key );
			EktooDecoder decoder = new EktooDecoder();
			
            //convert the string into base 10
			String string = converToBase10(encryptedString);
            //decode the string and get the bytes
			byte[] cleartext = decoder.decodeBuffer(string );
            //using cipher create the cryptographic array of bytes
			byte[] ciphertext = cipher.doFinal( cleartext );
			//finally convert bytes into string
			
			return bytes2String( ciphertext );
		}
		catch (Exception e){
			throw new EncryptionException(e.getMessage(),e);
		}
		//return null;
	}

	/**
     * this method convert any string into base16 format
     * 
     * @param toBeConvertedString
     * @return encrypted value as base16 format
     */
	private  String converToBase16(String toBeConvertedString){
        //get the bytes of to be converted string
        byte[] bytes = toBeConvertedString.getBytes();
        StringBuffer buffer = new StringBuffer();
        //iterate the bytes and convert to each byte into hex string 
        for(byte b :bytes){
            int bint = b;
            buffer.append(Integer.toHexString(bint));
        }
        return buffer.toString();
    }




    /**
     * convert any string into decimal string(base 10 string)
     * 
     * @param toBeConvertedString as String
     * @return buffer as decimal string
     */
  private String converToBase10(String toBeConvertedString){
        StringBuffer buffer = new StringBuffer();
        for(int index = 0; index < toBeConvertedString.length() ; index++){
            //in the decimal format we will take two digit in each time and make them hex
           String twoDigitHexValue = toBeConvertedString.substring(index, index+2) ;
           int iValue =  Integer.parseInt(twoDigitHexValue,16) ;
           char cValue = (char)iValue;
           buffer.append(cValue);
           index++ ;
       }       
        return buffer.toString();
    }

}
