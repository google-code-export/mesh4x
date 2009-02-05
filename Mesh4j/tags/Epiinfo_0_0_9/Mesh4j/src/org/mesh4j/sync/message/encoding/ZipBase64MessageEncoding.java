package org.mesh4j.sync.message.encoding;

import org.mesh4j.sync.utils.Base64Helper;
import org.mesh4j.sync.utils.ZipUtils;

public class ZipBase64MessageEncoding extends AbstractMessageEncoding{

	public static final ZipBase64MessageEncoding INSTANCE = new ZipBase64MessageEncoding();
	
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
	
	@Override
	public boolean isBynary() {
		return false;
	}

}
