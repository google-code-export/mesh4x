package org.mesh4j.sync.message.encoding;

import org.mesh4j.sync.utils.Base64;
import org.mesh4j.sync.validations.MeshException;

import de.enough.polish.util.ZipUtil;


public class CompressBase64MessageEncoding extends AbstractMessageEncoding {

	public static final CompressBase64MessageEncoding INSTANCE = new CompressBase64MessageEncoding();

	protected String basicDecode(String message) {
		try{
			byte[] encodedData = message.getBytes();
			byte[] decodedData = Base64.decode(encodedData);
			byte[] decompressedData = ZipUtil.decompress(decodedData);
			String decodedString = new String(decompressedData);
			return decodedString;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	protected String basicEncode(String message) {
		try{
			byte[] data = message.getBytes();
			byte[] compressedData = ZipUtil.compress(data);
			byte[] encodedData = Base64.encode(compressedData);
			String encodedString = new String(encodedData);			
			return encodedString;
		} catch(Exception e){
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}
	
	public boolean isBynary() {
		return false;
	}


}
