package org.mesh4j.sync.adapters.S3.amazon;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mesh4j.sync.adapters.S3.IS3Service;
import org.mesh4j.sync.adapters.S3.ObjectData;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.GetResponse;
import com.amazon.s3.ListBucketResponse;
import com.amazon.s3.ListEntry;
import com.amazon.s3.Response;
import com.amazon.s3.S3Object;

public class S3Service implements IS3Service{
	
	// MODEL VARIABLES
	private String accessKey;
	private String secretAccessKey;
	
	// BUSINESS METHODS

	public S3Service(String accessKey, String secretAccessKey){
		super();
		
		Guard.argumentNotNullOrEmptyString(accessKey, "accessKey");
		Guard.argumentNotNullOrEmptyString(secretAccessKey, "secretAccessKey");
		
		this.accessKey = accessKey;
		this.secretAccessKey = secretAccessKey;
	}
	
	private AWSAuthConnection connect(){
		return new AWSAuthConnection(this.accessKey, this.secretAccessKey);
	}

	public void createBucket(String bucket){
		AWSAuthConnection connection = this.connect();
		
		try{
			Response response = connection.createBucket(bucket, AWSAuthConnection.LOCATION_DEFAULT, null);
			if (response.connection.getResponseCode() != 200) {
				Guard.throwsException(S3Error.ERROR_CREATE_BUCKET.name()); 
			}
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	
	
	public byte[] readObject(String bucket, String objectID){
		AWSAuthConnection connection = this.connect();
		
		try{
			return readObject(bucket, objectID, connection);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private byte[] readObject(String bucket, String objectID,
			AWSAuthConnection connection) throws MalformedURLException,
			IOException {
		GetResponse response = connection.get(bucket, objectID, null);
		byte[] value = response.object.data;
		return value;
	}
	
	public void deleteObject(String bucket, String objectID){
		
		AWSAuthConnection connection = this.connect();

		try{
			Response response = connection.delete(bucket, objectID, null);
			if (response.connection.getResponseCode() != 204) {
				Guard.throwsException(S3Error.ERROR_DELETE_OBJECT.name());
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public List<ObjectData> readObjects(String bucket){
		AWSAuthConnection connection = this.connect();
		
		ListBucketResponse response = null;
		try{
			response = connection.listBucket(bucket, null, null, null, null);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		return readObjectsFromListEntries(bucket, connection, response);
	}

	@SuppressWarnings("unchecked")
	private List<ObjectData> readObjectsFromListEntries(String bucket, AWSAuthConnection connection, ListBucketResponse response) {
		HashMap<String, ObjectData> result = new HashMap<String, ObjectData>();
		List<ListEntry> objects = response.entries;
		for (Iterator<ListEntry> it = objects.iterator(); it.hasNext(); ) {
			ListEntry entry = it.next();
			
			try{
				byte[] data = readObject(bucket, entry.key, connection);
				ObjectData currentData = result.get(entry.key);
				if(currentData == null){
					result.put(entry.key, new ObjectData(entry.key, data));
				} else {
					if(!currentData.getData().equals(data)){
						currentData.setData(data);
					}
				}
			} catch(Exception e){
				throw new MeshException(e);
			}
		}
		return new ArrayList<ObjectData>(result.values());
	}
	 

	@Override
	public void deleteObjects(String bucket, Set<String> oids) {
		for (String oid : oids) {
			this.deleteObject(bucket, oid);
		}		
	}

	@Override
	public List<ObjectData> readObjectsStartsWith(String bucket, String oidPath) {
		AWSAuthConnection connection = this.connect();
		
		ListBucketResponse response = null;
		try{
			response = connection.listBucket(bucket, oidPath, null, null, null);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		return readObjectsFromListEntries(bucket, connection, response);
	}

	@Override
	public void writeObject(String bucket, String oid, byte[] data) {
		AWSAuthConnection connection = this.connect();
		S3Object simpleObject = new S3Object(data, null);
	
		try{
			Response response = connection.put(bucket, oid, simpleObject, null);
			if (response.connection.getResponseCode() != 200) {
				Guard.throwsException(S3Error.ERROR_WRITE_OBJECT.name()); 
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
