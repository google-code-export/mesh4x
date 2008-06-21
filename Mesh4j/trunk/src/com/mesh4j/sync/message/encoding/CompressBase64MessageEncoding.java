package com.mesh4j.sync.message.encoding;

import java.nio.charset.Charset;

import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.ZipUtils;
import com.mesh4j.sync.validations.MeshException;

public class CompressBase64MessageEncoding implements IMessageEncoding {

	public static final CompressBase64MessageEncoding INSTANCE = new CompressBase64MessageEncoding();

	@Override
	public String decode(String message) {
		try{
			if(message.length() == 0){
				return message;
			}
			String data = message.substring(1, message.length());
			if(message.startsWith("n")){
				return data;
			}else if(message.startsWith("c")){
				byte[] bytes = Base64Helper.decode(data);
				byte[] decompressedBytes = ZipUtils.decompress(bytes);
				return new String(decompressedBytes, Charset.forName("UTF-8"));
			}else{
				return "";
			}
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	public String encode(String message) {
		try{
			byte[] bytes = ZipUtils.compress(message.getBytes("UTF-8"));
			String encodeMsg = Base64Helper.encode(bytes);
			if(encodeMsg.length() >= message.length()+1){
				encodeMsg = "n"+message;
			}else{
				encodeMsg = "c"+encodeMsg;
			}
			return encodeMsg;
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

}