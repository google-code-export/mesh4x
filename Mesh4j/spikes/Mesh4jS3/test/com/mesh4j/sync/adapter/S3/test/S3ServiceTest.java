package com.mesh4j.sync.adapter.S3.test;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Test;
import org.mesh4j.sync.adapters.S3.ObjectData;
import org.mesh4j.sync.adapters.S3.S3Service;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.NullIdentityProvider;


public class S3ServiceTest {

	private static final String FEED_NAME = "myFeed";
	private static final String BUCKET_NAME = "instedd-tests";

	//	@Test
	public void shouldCreateBucket(){
		S3Service s3 = new S3Service();
		s3.createBucket(BUCKET_NAME);
	}
	
	//@Test
	public void shouldAddData() throws Exception{
		Item item = makeNewItem(IdGenerator.INSTANCE.newID(), "<payload><foo>bar</foo></payload>", "jmt");
		
		S3Service s3 = new S3Service();
		this.write(item, s3, BUCKET_NAME, FEED_NAME);
	}
	
	//@Test
	public void shouldReadBucket(){
		S3Service s3 = new S3Service();
		List<ObjectData> objects = s3.readObjects(BUCKET_NAME);
		Assert.assertTrue(objects.size() > 0);
	}
	
	//@Test
	public void shouldReadBucketObjectStartWith(){
		S3Service s3 = new S3Service();
		List<ObjectData> objects = s3.readObjectsStartsWith(BUCKET_NAME, FEED_NAME);
		Assert.assertTrue(objects.size() > 0);
	}
	
	//@Test
	public void shouldReadBucketItem() throws Exception{
		S3Service s3 = new S3Service();
		
		Item item = makeNewItem(IdGenerator.INSTANCE.newID(), "<payload><foo>bar</foo></payload>", "jmt");
		this.write(item, s3, BUCKET_NAME, FEED_NAME);
		
		item.getSync().update("admin", new Date());
		this.write(item, s3, BUCKET_NAME, FEED_NAME);
		
		List<ObjectData> objects = s3.readObjectsStartsWith(BUCKET_NAME, FEED_NAME+"."+item.getSyncId());
		Assert.assertEquals(2, objects.size());
	}
	
	@Test
	public void shouldDeleteBucketItems() throws Exception{
		S3Service s3 = new S3Service();
		
		List<ObjectData> objects = s3.readObjectsStartsWith(BUCKET_NAME, FEED_NAME);
		Assert.assertTrue(objects.size() > 0);
		
		Set<String> oids = new TreeSet<String>();
		for (ObjectData objectData : objects) {
			oids.add(objectData.getId());
		}
		
		s3.deleteObjects(BUCKET_NAME, oids);
		objects = s3.readObjectsStartsWith(BUCKET_NAME, FEED_NAME);
		Assert.assertTrue(objects.size() == 0);
	}
	
	
	private void write(Item item, S3Service s3, String bucket, String objectPath) throws Exception{
		
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
		String xmlItem = writer.writeAsXml(item);
		String oid = objectPath+"."+item.getSyncId()+"."+item.getLastUpdate().getSequence()+"."+item.getLastUpdate().getBy();
				
		s3.write(bucket, oid, xmlItem.getBytes());

	}
	
	private Item makeNewItem(String syncId, String payload, String user)throws Exception{
		Document doc = DocumentHelper.parseText(payload);
	
		IContent content = new XMLContent(syncId, "title", "desc", doc.getRootElement());
		Sync sync = new Sync(syncId, user, new Date(), false);
		Item item = new Item(content, sync);
		return item;
	}
}
