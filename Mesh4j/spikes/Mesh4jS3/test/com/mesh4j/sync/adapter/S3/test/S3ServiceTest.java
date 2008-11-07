package com.mesh4j.sync.adapter.S3.test;

import java.util.List;

import org.junit.Test;
import org.mesh4j.sync.adapters.S3.S3Service;

import com.amazon.s3.ListEntry;

public class S3ServiceTest {

//	@Test
//	public void shouldCreateBucket(){
//		S3Service s3 = new S3Service();
//		s3.createBucket("instedd-tests");
//	}
	
	@Test
	public void shouldReadBucket(){
		S3Service s3 = new S3Service();
		List<ListEntry> objects = s3.getObjects("instedd-tests");
	}
	
	@Test
	public void shouldReadBucketObjectStartWith(){
		S3Service s3 = new S3Service();
		List<ListEntry> objects = s3.getObjectsStartWith("instedd-tests", "myFeed");
	}
}
