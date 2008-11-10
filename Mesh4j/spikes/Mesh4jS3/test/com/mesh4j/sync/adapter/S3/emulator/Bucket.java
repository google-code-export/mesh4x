package com.mesh4j.sync.adapter.S3.emulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mesh4j.sync.adapters.S3.ObjectData;

public class Bucket {

	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLES
	private String bucketId;
	private HashMap<String, ObjectData> objects = new HashMap<String, ObjectData>(); 
	
	// BUSINESS METHODS
	public Bucket(String bucketId){
		super();
		this.bucketId = bucketId;
	}

	public String getBucketId() {
		return bucketId;
	}
	
	public void write(String oid, byte[] data){
		synchronized (SEMAPHORE) {
			this.objects.put(oid, new ObjectData(oid, data));	
		}
		
	}

	public byte[] read(String oid) {
		ObjectData obj = null;
	
		synchronized (SEMAPHORE) {
			obj = this.objects.get(oid);
		}
		
		if(obj == null){
			return null;
		} else {
			return obj.getData();
		}
	}
	
	public List<ObjectData> readObjectsStartsWith(String oidPath){
		synchronized (SEMAPHORE) {
			ArrayList<ObjectData> objs = new ArrayList<ObjectData>();
			for (String oid : this.objects.keySet()) {
				if(oid.startsWith(oidPath)){
					objs.add( this.objects.get(oid));
				}
			}
			return objs;
		}
	}

	public void delete(Set<String> oids) {
		synchronized (SEMAPHORE) {
			for (String oid : oids) {
				this.objects.remove(oid);
			}
		}
	}
}
