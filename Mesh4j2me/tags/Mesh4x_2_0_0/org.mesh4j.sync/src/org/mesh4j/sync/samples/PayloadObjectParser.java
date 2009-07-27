package org.mesh4j.sync.samples;

import org.mesh4j.sync.adapters.rms.storage.IObjectParser;

public class PayloadObjectParser implements IObjectParser {

	public Object bytesToObject(byte[] data) {
		return new String(data);
	}

	public byte[] objectToBytes(Object object) {
		return ((String)object).getBytes();
	}

}
