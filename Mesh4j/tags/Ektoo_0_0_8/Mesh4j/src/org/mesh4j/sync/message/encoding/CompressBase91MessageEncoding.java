package org.mesh4j.sync.message.encoding;

import java.nio.charset.Charset;

import org.mesh4j.sync.utils.Base91Helper;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.MeshException;


public class CompressBase91MessageEncoding extends AbstractMessageEncoding {

	public static final CompressBase91MessageEncoding INSTANCE = new CompressBase91MessageEncoding();

	@Override
	protected String basicDecode(String message) {
		try{
			byte[] bytes = Base91Helper.decode(message);
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
			String encodeMsg = Base91Helper.encode(bytes);
			return encodeMsg;
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	public boolean isBynary() {
		return true;
	}

}
