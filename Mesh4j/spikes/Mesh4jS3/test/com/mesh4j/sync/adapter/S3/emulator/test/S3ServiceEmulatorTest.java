package com.mesh4j.sync.adapter.S3.emulator.test;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapter.S3.emulator.S3Service;

public class S3ServiceEmulatorTest {
	
	@Test
	public void shouldObtainsSameValueNewWasReplicated(){
		S3Service service = makeService();
		
		String bucketId = "bucket1";
		String oid = "oid1";
		byte[] data = "hola".getBytes();

		service.writeAndFastReplicate(bucketId, oid, data);
		
		byte[] dataNode0 = service.read("0", bucketId, oid);
		byte[] dataNode1 = service.read("1", bucketId, oid);
		
		Assert.assertArrayEquals(dataNode0, dataNode1);
		Assert.assertArrayEquals(data, dataNode1);
	}

	@Test
	public void shouldObtainsSameValueUpdateWasReplicated(){
		S3Service service = makeService();
		
		String bucketId = "bucket1";
		String oid = "oid1";
		byte[] data = "hola".getBytes();

		service.writeAndFastReplicate(bucketId, oid, data);
		
		byte[] dataNode0 = service.read("0", bucketId, oid);
		byte[] dataNode1 = service.read("1", bucketId, oid);
		
		Assert.assertArrayEquals(dataNode0, dataNode1);
		Assert.assertArrayEquals(data, dataNode1);
		
		byte[] dataToUpdate = "holaMundo".getBytes();
		service.writeAndFastReplicate(bucketId, oid, dataToUpdate);

		dataNode0 = service.read("0", bucketId, oid);
		dataNode1 = service.read("1", bucketId, oid);
		
		Assert.assertArrayEquals(dataNode0, dataNode1);
		Assert.assertArrayEquals(dataToUpdate, dataNode1);

	}
	
	
	@Test
	public void shouldObtainsNullValueNewNoWasReplicated(){
		S3Service service = makeService();
		
		String bucketId = "bucket1";
		String oid = "oid1";
		byte[] data = "hola".getBytes();

		service.write("1", bucketId, oid, data);
		
		byte[] dataNode0 = service.read("0", bucketId, oid);
		byte[] dataNode1 = service.read("1", bucketId, oid);
		
		Assert.assertNull(dataNode0);
		Assert.assertArrayEquals(data, dataNode1);
	}
	
	
	@Test
	public void shouldObtainsOldValueUpdateNoWasReplicated(){
		S3Service service = makeService();
		
		String bucketId = "bucket1";
		String oid = "oid1";
		byte[] data = "hola".getBytes();

		service.writeAndFastReplicate(bucketId, oid, data);
		
		byte[] dataNode0 = service.read("0", bucketId, oid);
		byte[] dataNode1 = service.read("1", bucketId, oid);
		
		Assert.assertArrayEquals(dataNode0, dataNode1);
		Assert.assertArrayEquals(data, dataNode1);
		
		
		byte[] dataToUpdate = "holaMundo".getBytes();
		service.write("1", bucketId, oid, dataToUpdate);

		dataNode0 = service.read("0", bucketId, oid);
		dataNode1 = service.read("1", bucketId, oid);
		
		Assert.assertArrayEquals(data, dataNode0);
		Assert.assertArrayEquals(dataToUpdate, dataNode1);
		
	}

	private S3Service makeService() {
		S3Service service = new S3Service(50);
		service.addNode("0");
		service.addNode("1");
		return service;
	}

}
