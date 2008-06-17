package com.mesh4j.sync.message.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.ZipUtils;
import com.mesh4j.sync.validations.MeshException;

public class ZipBase64MessageEncoding implements IMessageEncoding{

	public static final ZipBase64MessageEncoding INSTANCE = new ZipBase64MessageEncoding();

	@Override
	public String encode(String message) {
		if(message.length() == 0){
			return message;
		}
		byte[] bytes = zip(message);
		String encodeMsg = Base64Helper.encode(bytes);
		if(encodeMsg.length() >= message.length()+1){
			encodeMsg = "o"+message;
		}else{
			encodeMsg = "e"+encodeMsg;
		}
		return encodeMsg;
	}
	
	@Override
	public String decode(String message) {
		if(message.length() == 0){
			return message;
		}
		String data = message.substring(1, message.length());
		if(message.startsWith("o")){
			return data;
		}else if(message.startsWith("e")){
			byte[] zipBytes = Base64Helper.decode(data);		
			return unzip(zipBytes);
		}else{
			return "";
		}
	}

	private String unzip(byte[] zipBytes) {
		try{
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes));
			String result = ZipUtils.getTextEntryContent(zipInputStream, "a");
			zipInputStream.close();
			return result;
		} catch(IOException io){
			throw new MeshException(io);
		}			
	}

	private byte[] zip(String message) {
		try{
			ByteArrayOutputStream itemsOS = new ByteArrayOutputStream();
			ZipOutputStream zipOS = new ZipOutputStream(itemsOS);
			ZipUtils.write(zipOS, "a", message);
			
			itemsOS.flush();
			zipOS.close();
			return itemsOS.toByteArray();
		} catch(IOException io){
			throw new MeshException(io);
		}
	}

	
}
