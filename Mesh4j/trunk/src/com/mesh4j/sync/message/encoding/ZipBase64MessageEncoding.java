package com.mesh4j.sync.message.encoding;

import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.ZipUtils;

public class ZipBase64MessageEncoding extends AbstractMessageEncoding{

	public static final ZipBase64MessageEncoding INSTANCE = new ZipBase64MessageEncoding();

	private ZipBase64MessageEncoding(){
		super();
	}
	
	@Override
	public String basicEncode(String message) {
		byte[] bytes = ZipUtils.zip(message, "message");
		String encodeMsg = Base64Helper.encode(bytes);
		return encodeMsg;
	}
	
	@Override
	public String basicDecode(String message) {
		byte[] zipBytes = Base64Helper.decode(message);		
		return ZipUtils.unzip(zipBytes, "message");
	}	
}
