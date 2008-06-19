package com.mesh4j.sync.message.encoding;

import com.mesh4j.sync.utils.Base91Helper;
import com.mesh4j.sync.utils.ZipUtils;

public class ZipBase91MessageEncoding implements IMessageEncoding{

	public static final ZipBase91MessageEncoding INSTANCE = new ZipBase91MessageEncoding();

	@Override
	public String encode(String message) {
		if(message.length() == 0){
			return message;
		}
		byte[] bytes = ZipUtils.zip(message, "message");
		String encodeMsg = Base91Helper.encode(bytes);
		if(encodeMsg.length() >= message.length()+1){
			encodeMsg = "n"+message;
		}else{
			encodeMsg = "c"+encodeMsg;
		}
		return encodeMsg;
	}
	
	@Override
	public String decode(String message) {
		if(message.length() == 0){
			return message;
		}
		String data = message.substring(1, message.length());
		if(message.startsWith("n")){
			return data;
		}else if(message.startsWith("c")){
			byte[] zipBytes = Base91Helper.decode(data);		
			return ZipUtils.unzip(zipBytes, "message");
		}else{
			return "";
		}
	}
}
