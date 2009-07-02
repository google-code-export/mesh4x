package org.mesh4j.sync.adapters.rms.storage;


public interface IObjectParser {

	Object bytesToObject(byte[] data);
	byte[] objectToBytes(Object object);

}
