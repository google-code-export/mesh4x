package org.mesh4j.sync.adapters.S3;

import java.util.List;
import java.util.Set;


public interface IS3Service {

	void write(String bucket, String oid, byte[] data);

	List<ObjectData> readObjectsStartsWith(String bucket, String oidPath);
	
	void deleteObject(String bucket, String oid);
	
	void deleteObjects(String bucket, Set<String> oids);

}
