package com.mesh4j.sync.message.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.ZipUtils;
import com.mesh4j.sync.validations.MeshException;

public class ZipBase64Encoding implements IMessageEncoding{

	public static final ZipBase64Encoding INSTANCE = new ZipBase64Encoding();

	@Override
	public String decode(String message) {
		try{
			byte[] zipBytes = Base64Helper.decode(message);
		
			ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes));
			String result = ZipUtils.getTextEntryContent(zipInputStream, "message");
			zipInputStream.close();

			return result;
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	public String encode(String message) {
		try{
			if(message.length() == 0){
				return message;
			}
			
			ByteArrayOutputStream itemsOS = new ByteArrayOutputStream();
			ZipOutputStream zipOS = new ZipOutputStream(itemsOS);
			ZipUtils.write(zipOS, "message", message);
			
			itemsOS.flush();
			zipOS.close();
			
			String itemsAsBase64 = Base64Helper.encode(itemsOS.toByteArray());
			return itemsAsBase64;
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	
}
