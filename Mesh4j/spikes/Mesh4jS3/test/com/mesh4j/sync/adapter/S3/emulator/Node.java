package com.mesh4j.sync.adapter.S3.emulator;

import java.util.HashMap;
import java.util.List;

public class Node {

	// MODEL VARIABLES
	private String nodeId;
	private HashMap<String, Bucket> buckets = new HashMap<String, Bucket>();
	
	// BUSINESS METHODS
	public Node(String nodeId){
		super();
		this.nodeId = nodeId;
	}
	
	public void write(String bucketId, String oid, byte[] data){
		Bucket bucket = getBucket(bucketId);
		bucket.write(oid, data);
	}

	private Bucket getBucket(String bucketId) {
		Bucket bucket = this.buckets.get(bucketId);
		if(bucket == null){
			bucket = new Bucket(bucketId);
			this.buckets.put(bucketId, bucket);
		}
		return bucket;
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public byte[] read(String bucketId, String oid) {
		Bucket bucket = getBucket(bucketId);
		return bucket.read(oid);
	}

	public List<ObjectData> readObjectsStartsWith(String bucketId, String oidPath) {
		Bucket bucket = getBucket(bucketId);
		return bucket.readObjectsStartsWith(oidPath);
	}

	public void delete(String bucketId, List<String> oids) {
		Bucket bucket = getBucket(bucketId);
		bucket.delete(oids);
	}
}
