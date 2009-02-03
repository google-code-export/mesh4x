package org.mesh4j.sync.utils;

import org.mesh4j.sync.validations.MeshException;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class Base64Helper {

	public static String encode(String text){
		return Base64.encode(text.getBytes());
	}
	
	public static String encode(byte[] bytes){
		return Base64.encode(bytes);
	}

	public static byte[] decode(String encoded){
		try {
			return Base64.decode(encoded);
		} catch (Base64DecodingException e) {
			throw new MeshException(e);
		}
	}
	
	public static String decodeAsString(String encoded){
		return new String(decode(encoded));
	}
	
}
