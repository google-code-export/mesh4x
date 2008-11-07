package org.mesh4j.sync.adapters.S3;

import java.util.Iterator;
import java.util.List;

import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.amazon.s3.AWSAuthConnection;
import com.amazon.s3.GetResponse;
import com.amazon.s3.ListBucketResponse;
import com.amazon.s3.ListEntry;
import com.amazon.s3.Response;
import com.amazon.s3.S3Object;

public class S3Service {

	private AWSAuthConnection connect(){
		return new AWSAuthConnection("[aws-access-key-id]", "[aws-secret-access-key-id]");
	}

	public void createBucket(String bucket){
		AWSAuthConnection connection = this.connect();
		
		try{
			Response response = connection.createBucket(bucket, AWSAuthConnection.LOCATION_DEFAULT, null);
			if (response.connection.getResponseCode() != 200) {
				Guard.throwsException("XXXXXXXXXX ERROR");	// TODO (JMT) define the creation error 
			}
		} catch(Exception e){
			throw new MeshException(e);
		}
	}
	

	public void writeObject(String bucket, String objectID, byte[] data){
		AWSAuthConnection connection = this.connect();
		S3Object simpleObject = new S3Object(data, null);
	
		try{
			Response response = connection.put(bucket, objectID, simpleObject, null);
			if (response.connection.getResponseCode() != 200) {
				Guard.throwsException("XXXXXXXXXX ERROR");	// TODO (JMT) define the add/update error 
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}

	}

	
	public byte[] getObject(String bucket, String objectID){
		AWSAuthConnection connection = this.connect();
		
		try{
			GetResponse response = connection.get(bucket, objectID, null);
			byte[] value = response.object.data;
			return value;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public void deleteObject(String bucket, String objectID){
		
		AWSAuthConnection connection = this.connect();

		try{
			Response response = connection.delete(bucket, objectID, null);
			if (response.connection.getResponseCode() != 204) {
				Guard.throwsException("XXXXXXXXXX ERROR");	// TODO (JMT) define the creation error
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	public List<ListEntry> getObjects(String bucketName){
		AWSAuthConnection connection = this.connect();
		
		ListBucketResponse response = null;
		try{
			response = connection.listBucket(bucketName, null, null, null, null);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		List<ListEntry> objects = response.entries;
		for (Iterator<ListEntry> it = objects.iterator(); it.hasNext(); ) {
			ListEntry entry = it.next();
			System.out.println("key = " + entry.key + " size = " + entry.size);
		}
		return objects;
	}
	 
	public List<ListEntry> getObjectsStartWith(String bucketName, String oidPath){
		AWSAuthConnection connection = this.connect();
		
		ListBucketResponse response = null;
		try{
			response = connection.listBucket(bucketName, oidPath, null, null, null);
		} catch (Exception e) {
			throw new MeshException(e);
		}
		
		List<ListEntry> objects = response.entries;
		for (Iterator<ListEntry> it = objects.iterator(); it.hasNext(); ) {
			ListEntry entry = it.next();
			System.out.println("key = " + entry.key + " size = " + entry.size);
		}
		return objects;
	}

}
