package com.mesh4j.sync.message.encoding;

import java.nio.charset.Charset;

import com.mesh4j.sync.utils.Base64Helper;
import com.mesh4j.sync.utils.ZipUtils;
import com.mesh4j.sync.validations.MeshException;

public class CompressBase64MessageEncoding extends AbstractMessageEncoding {

	public static final CompressBase64MessageEncoding INSTANCE = new CompressBase64MessageEncoding();

	@Override
	protected String basicDecode(String message) {
		try{
			byte[] bytes = Base64Helper.decode(message);
			byte[] decompressedBytes = ZipUtils.decompress(bytes);
			return new String(decompressedBytes, Charset.forName("UTF-8"));
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	protected String basicEncode(String message) {
		try{
			byte[] bytes = ZipUtils.compress(message.getBytes("UTF-8"));
			String encodeMsg = Base64Helper.encode(bytes);
			return encodeMsg;
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	
	@Override
	public boolean isBynary() {
		return false;
	}


}
