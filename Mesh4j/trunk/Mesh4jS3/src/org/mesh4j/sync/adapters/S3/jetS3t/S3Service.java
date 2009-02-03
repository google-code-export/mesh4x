package org.mesh4j.sync.adapters.S3.jetS3t;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jets3t.service.S3ServiceException;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;
import org.mesh4j.sync.adapters.S3.IS3Service;
import org.mesh4j.sync.adapters.S3.ObjectData;
import org.mesh4j.sync.validations.MeshException;

public class S3Service implements IS3Service {

	// MODEL VARIABLES
	private org.jets3t.service.S3Service s3;

	// BUSINES METHODS
	public S3Service(String awsAccessKey, String awsSecretKey){
		super();
		initialize(awsAccessKey, awsSecretKey);
	}
	
	private void initialize(String awsAccessKey, String awsSecretKey) {
		try{
			AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretKey);	
			this.s3 = new RestS3Service(awsCredentials);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	// IS3Service methods
	@Override
	public List<ObjectData> readObjects(String bucketName) {
		try{
			S3Bucket bucket = this.s3.getBucket(bucketName);
			S3Object[] filteredObjects = this.s3.listObjects(bucket);
	
			List<ObjectData> result = new ArrayList<ObjectData>();
			S3Object s3DownloadedObject;
			for (S3Object s3Object : filteredObjects) {
				s3DownloadedObject = this.s3.getObject(bucket, s3Object.getKey());
				String data = ServiceUtils.readInputStreamToString(s3DownloadedObject.getDataInputStream(), "UTF-8");
				result.add(new ObjectData(s3Object.getKey(), data.getBytes()));
			}
			return result;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	@Override
	public List<ObjectData> readObjectsStartsWith(String bucketName, String oidPath) {
		try{
			S3Bucket bucket = this.s3.getBucket(bucketName);
			S3Object[] filteredObjects = this.s3.listObjects(bucket, oidPath, null);

			List<ObjectData> result = new ArrayList<ObjectData>();
			S3Object s3DownloadedObject;
			for (S3Object s3Object : filteredObjects) {
				s3DownloadedObject = this.s3.getObject(bucket, s3Object.getKey());
				String data = ServiceUtils.readInputStreamToString(s3DownloadedObject.getDataInputStream(), "UTF-8");
				result.add(new ObjectData(s3Object.getKey(), data.getBytes()));
			}
			return result;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public void writeObject(String bucketName, String oid, byte[] data) {
		ByteArrayInputStream bais = null;
		try{
			bais = new ByteArrayInputStream(data);
			
			S3Object object = new S3Object(bucketName);
			object.setKey(oid);
			object.setDataInputStream(bais);
			this.s3.putObject(bucketName, object);
		}catch (Exception e) {
			throw new MeshException(e);
		}finally{
			if(bais != null) {
				try{
					bais.close();
				}catch (Exception e) {
					throw new MeshException(e);
				}
			}
		} 
	}

	@Override
	public void createBucket(String bucket) {
		try {
			this.s3.createBucket(bucket);
		} catch (S3ServiceException e) {
			throw new MeshException(e);
		}
	}

	@Override
	public void deleteObjects(String bucket, Set<String> oids) {
		for (String oid : oids) {
			try {
				this.s3.deleteObject(bucket, oid);
			} catch (S3ServiceException e) {
				throw new MeshException(e);
			}
		}
	}
}
