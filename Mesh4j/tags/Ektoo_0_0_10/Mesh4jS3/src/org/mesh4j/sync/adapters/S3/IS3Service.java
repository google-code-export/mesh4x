package org.mesh4j.sync.adapters.S3;

import java.util.List;
import java.util.Set;


public interface IS3Service {

	// Bucket methods
	public void createBucket(String bucket);
	
	// Object methods
	void writeObject(String bucket, String oid, byte[] data);

	List<ObjectData> readObjects(String bucket);
	
	List<ObjectData> readObjectsStartsWith(String bucket, String oidPath);
	
	void deleteObjects(String bucket, Set<String> oids);

}
