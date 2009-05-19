package org.mesh4j.sync.message.encoding;

import java.nio.charset.Charset;

import org.mesh4j.sync.utils.YEnc;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.MeshException;


public class CompressYEncMessageEncoding extends AbstractMessageEncoding {

	public static final CompressYEncMessageEncoding INSTANCE = new CompressYEncMessageEncoding();
	
	@Override
	protected String basicDecode(String message) {
		try{
			YEnc yenc = new YEnc();
			byte[] bytes = yenc.decode(message.getBytes("ISO-8859-1"));
			
			byte[] decompressedBytes = ZipUtils.decompress(bytes);
			return new String(decompressedBytes, Charset.forName("ISO-8859-1"));
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	@Override
	protected String basicEncode(String message) {
		try{
			byte[] bytes = ZipUtils.compress(message.getBytes("ISO-8859-1"));
			String compressMsg = new String(bytes, Charset.forName("ISO-8859-1"));
			System.out.println("Compressed: " + compressMsg.length());
			
			YEnc yenc = new YEnc();
			byte[] encodedBytes = yenc.encode(bytes);
			String encodeMsg = new String(encodedBytes, Charset.forName("ISO-8859-1"));
			System.out.println("Encoded: " + encodeMsg.length());
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
