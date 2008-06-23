package com.mesh4j.sync.message.encoding;

import com.mesh4j.sync.utils.Base91Helper;
import com.mesh4j.sync.utils.ZipUtils;

public class ZipBase91MessageEncoding extends AbstractMessageEncoding{

	public static final ZipBase91MessageEncoding INSTANCE = new ZipBase91MessageEncoding();

	private ZipBase91MessageEncoding(){
		super();
	}
	
	@Override
	protected String basicEncode(String message) {
		byte[] bytes = ZipUtils.zip(message, "message");
		String encodeMsg = Base91Helper.encode(bytes);
		return encodeMsg;
	}
	
	@Override
	protected String basicDecode(String message) {
		byte[] zipBytes = Base91Helper.decode(message);		
		return ZipUtils.unzip(zipBytes, "message");
	}
}
