package org.mesh4j.sync.adapters.S3;

import java.util.List;

import com.mesh4j.sync.adapter.S3.emulator.ObjectData;

public interface IS3Service {

	void write(String bucket, String oid, byte[] data);

	List<ObjectData> readObjectsStartsWith(String bucket, String oidPath);

	void write(String location, String bucket, String oid, byte[] data);

	void delete(String bucket, List<String> oids);

	List<ObjectData> readObjectsStartsWith(String nodeId, String bucket,
			String oidPath);

}
