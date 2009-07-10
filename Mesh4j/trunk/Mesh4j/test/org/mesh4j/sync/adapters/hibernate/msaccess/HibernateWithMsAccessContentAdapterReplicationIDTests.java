package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.hibernate.Hibernate;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateMsAccessToRDFMapping;
import org.mesh4j.sync.adapters.hibernate.mapping.UUIDStringToHexStringSchemaTypeFormat;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

import sun.jdbc.odbc.JdbcOdbcDriver;

public class HibernateWithMsAccessContentAdapterReplicationIDTests {

	@Test
	public void testReplicationID() throws Exception{
		String guid="B98A34A6-EE0A-4FB7-8A7A-AC5BAC0FAF5F";
		
		String fileName = TestHelper.fileName("msAccess_hibernate_repId_"+IdGenerator.INSTANCE.newID()+".mdb");
		String mdbFileName = getMsAccessFileNameToTest(fileName);
		
		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
		database+= mdbFileName + ";DriverID=22;READONLY=false}"; // add on to the end 
 
		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
		Assert.assertNotNull(driver);
		
		Properties prop = new Properties();
		prop.put("user", "");
		prop.put("password", "");

		String uuidStr = null;
		Connection conn = null;
		Statement command = null;
		ResultSet rs = null;
		
		try{
			conn = DriverManager.getConnection(database, prop);
			command = conn.createStatement();
			rs = command.executeQuery("select * from mytable");
			
			rs.next();
			byte[] bytes = (byte[])Hibernate.BINARY.get(rs, "id");
			String hexStr = Hibernate.BINARY.toString(bytes);
			uuidStr = (String)UUIDStringToHexStringSchemaTypeFormat.INSTANCE.parseObject(hexStr);
		}finally{
			if(rs != null){
				try{
					rs.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(command != null){
				try{
					command.close();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if(conn != null){
				conn.close();
			}
		}
			
		Assert.assertEquals(guid, uuidStr.toUpperCase());		
	}
	
	@Test
	public void shouldconvert() throws DecoderException, IOException{
		
		String guid="B98A34A6-EE0A-4FB7-8A7A-AC5BAC0FAF5F";
		
		// TO READ
		//  "26b40a398a6e37cf0afa2cdb2c8f2fdf";
		byte[] bytearray = new byte[]{-90, 52, -118, -71, 10, -18, -73, 79, -118, 122, -84, 91, -84, 15, -81, 95};
		String hexString = Hibernate.BINARY.toString(bytearray);	
		byte[] bytes = (byte[])Hibernate.BINARY.fromStringValue(hexString);
		
		byte[] bytesMostSignificant = new byte[8];
		bytesMostSignificant[0] = bytes[3];
		bytesMostSignificant[1] = bytes[2];
		bytesMostSignificant[2] = bytes[1];
		bytesMostSignificant[3] = bytes[0];
		
		bytesMostSignificant[6] = bytes[7];
		bytesMostSignificant[7] = bytes[6];
		bytesMostSignificant[4] = bytes[5];
		bytesMostSignificant[5] = bytes[4];
		
		Long mostSignificant = ByteBuffer.wrap(bytesMostSignificant).getLong();
				
		byte[] bytesLestSignificant = new byte[8];
		bytesLestSignificant[0] = bytes[8];
		bytesLestSignificant[1] = bytes[9];
		bytesLestSignificant[2] = bytes[10];
		bytesLestSignificant[3] = bytes[11];
		bytesLestSignificant[4] = bytes[12];
		bytesLestSignificant[5] = bytes[13];
		bytesLestSignificant[6] = bytes[14];
		bytesLestSignificant[7] = bytes[15];
		
		Long leastSignificant = ByteBuffer.wrap(bytesLestSignificant).getLong();
		UUID uuid = new UUID(mostSignificant, leastSignificant);	
		Assert.assertEquals(guid, uuid.toString().toUpperCase());
		
		// TO WRITE
		long mostSignificantBits = uuid.getMostSignificantBits();
		
		ByteArrayOutputStream mbis = new ByteArrayOutputStream();
		DataOutputStream mdos = new DataOutputStream(mbis);
		mdos.writeLong(mostSignificantBits);
		mdos.flush();
		byte[] mostSigBytes = mbis.toByteArray();
		
		long leastSignificantBits = uuid.getLeastSignificantBits();
		ByteArrayOutputStream bis = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bis);
		dos.writeByte(mostSigBytes[3]);
		dos.writeByte(mostSigBytes[2]);
		dos.writeByte(mostSigBytes[1]);
		dos.writeByte(mostSigBytes[0]);
		dos.writeByte(mostSigBytes[5]);
		dos.writeByte(mostSigBytes[4]);
		dos.writeByte(mostSigBytes[7]);
		dos.writeByte(mostSigBytes[6]);
		dos.writeLong(leastSignificantBits);
		dos.flush();
		
		byte[] bytes1 = bis.toByteArray();
		Assert.assertArrayEquals(bytearray, bytes1);
	}
	
	// XHTT driver
//	@Test
//	public void shouldGetAllFromXHTT() throws UnsupportedEncodingException{		
//		SplitAdapter adapter = makeAdapter(false);
//		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
//	
//		List<Item> items = adapter.getAll();
//		
//		Assert.assertNotNull(items);
//		Assert.assertFalse(items.isEmpty());
//		Assert.assertEquals(1, items.size());
//		
//		String id = "B98A34A6-EE0A-4FB7-8A7A-AC5BAC0FAF5F".toLowerCase();
//		HashMap<String, Object> properties = new HashMap<String, Object>();
//		properties.put("id", id);
//		properties.put("name", "jmt");
//		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
//		
//		Assert.assertEquals(instance.asXML(), items.get(0).getContent().getPayload().asXML());
//		
//	}
//
//	@Test
//	public void shouldGetFromXHTT(){
//		SplitAdapter adapter = makeAdapter(false);
//		List<Item> items = adapter.getAll();
//		TestHelper.assertItem(items.get(0), adapter);
//	}
//	
//	@Test
//	public void shouldAddFromXHTT(){
//		SplitAdapter adapter = makeAdapter(false);
//		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
//		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
//	
//		List<Item> items = adapter.getAll();
//		int size = items.size();
//		Assert.assertNotNull(items);
//		Assert.assertFalse(items.isEmpty());
//		
//		String id = IdGenerator.INSTANCE.newID();
//		HashMap<String, Object> properties = new HashMap<String, Object>();
//		properties.put("id", id);
//		properties.put("name", "sol");
//		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
//		
//		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
//		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
//		adapter.add(item);	
//		
//		items = adapter.getAll();
//		Assert.assertNotNull(items);
//		Assert.assertEquals(size +1, items.size());
//		
//		TestHelper.assertItem(item, adapter);
//	}
//	
//	@Test
//	public void shouldUpdateFromXHTT(){
//		SplitAdapter adapter = makeAdapter(false);
//		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
//		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
//
//		List<Item> items = adapter.getAll();
//		
//		Assert.assertNotNull(items);
//		Assert.assertFalse(items.isEmpty());
//		int size = items.size();
//		
//		String id = IdGenerator.INSTANCE.newID();
//		HashMap<String, Object> properties = new HashMap<String, Object>();
//		properties.put("id", id);
//		properties.put("name", "sol");
//		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
//		
//		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
//		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
//		adapter.add(item);	
//		
//		items = adapter.getAll();		
//		Assert.assertNotNull(items);
//		Assert.assertFalse(items.isEmpty());
//		Assert.assertEquals(size+1, items.size());
//		size = items.size();
//		
//		properties = new HashMap<String, Object>();
//		properties.put("id", id);
//		properties.put("name", "name"+IdGenerator.INSTANCE.newID());
//		instance = rdfSchema.createNewInstanceFromProperties(id, properties);
//		
//		identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
//		Item itemToUpdate = new Item(identifiableContent, item.getSync().clone().update("jmt", new Date(), false));
//		adapter.update(itemToUpdate);	
//		
//		items = adapter.getAll();
//		Assert.assertNotNull(items);
//		Assert.assertEquals(size, items.size());
//		
//		TestHelper.assertItem(itemToUpdate, adapter);
//	}
//	
//	@Test
//	public void shouldDeleteFromXHTT(){
//		SplitAdapter adapter = makeAdapter(false);
//		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
//		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
//
//		List<Item> items = adapter.getAll();
//		
//		Assert.assertNotNull(items);
//		Assert.assertFalse(items.isEmpty());
//		int size = items.size();
//		
//		String id = IdGenerator.INSTANCE.newID();
//		HashMap<String, Object> properties = new HashMap<String, Object>();
//		properties.put("id", id);
//		properties.put("name", "sol");
//		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
//		
//		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
//		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
//		adapter.add(item);	
//		
//		Item itemToDelete = item.clone();
//		itemToDelete.getSync().delete("jmt", new Date());
//		adapter.update(itemToDelete);	
//		
//		Item resultItem = adapter.get(item.getSyncId());
//		Assert.assertTrue(resultItem.isDeleted());
//		
//		id = IdGenerator.INSTANCE.newID();
//		properties = new HashMap<String, Object>();
//		properties.put("id", id);
//		properties.put("name", "sol");
//		instance = rdfSchema.createNewInstanceFromProperties(id, properties);
//		
//		identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
//		item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
//		adapter.add(item);	
//		
//		adapter.delete(item.getSyncId());
//
//		resultItem = adapter.get(item.getSyncId());
//		Assert.assertTrue(resultItem.isDeleted());
//				
//		items = adapter.getAll();
//			
//		Assert.assertNotNull(items);
//		Assert.assertFalse(items.isEmpty());
//		Assert.assertEquals(size + 2, items.size());
//		
//	}
//	
//	@Test
//	public void shouldSyncFromXHTT(){
//		String fileName = TestHelper.fileName("msAccess_hibernate_repId_"+IdGenerator.INSTANCE.newID());
//		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);
//
//		SplitAdapter adapter = makeAdapter(false);
//
//		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
//		
//		TestHelper.assertSync(syncEngine);
//		
//		TestHelper.assertSync(syncEngine);
//	}
	
	// SUN Driver
	
	@Test
	public void shouldGetAll() throws UnsupportedEncodingException{		
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
	
		List<Item> items = adapter.getAll();
		
		adapter.endSync();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(1, items.size());
		
		String id = "B98A34A6-EE0A-4FB7-8A7A-AC5BAC0FAF5F".toLowerCase();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", id);
		properties.put("name", "jmt");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		Assert.assertEquals(instance.asXML(), items.get(0).getContent().getPayload().asXML());
		
	}

	@Test
	public void shouldGet(){
		SplitAdapter adapter = makeAdapter();
		List<Item> items = adapter.getAll();
		TestHelper.assertItem(items.get(0), adapter);
		adapter.endSync();
	}
	
	@Test
	public void shouldAdd(){
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();
	
		List<Item> items = adapter.getAll();
		int size = items.size();
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		
		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", id);
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertEquals(size +1, items.size());
		
		TestHelper.assertItem(item, adapter);
		adapter.endSync();
	}
	
	@Test
	public void shouldUpdate(){
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();

		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		int size = items.size();
		
		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", id);
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		items = adapter.getAll();		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(size+1, items.size());
		size = items.size();
		
		properties = new HashMap<String, Object>();
		properties.put("id", id);
		properties.put("name", "name"+IdGenerator.INSTANCE.newID());
		instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item itemToUpdate = new Item(identifiableContent, item.getSync().clone().update("jmt", new Date(), false));
		adapter.update(itemToUpdate);	
		
		items = adapter.getAll();
				
		Assert.assertNotNull(items);
		Assert.assertEquals(size, items.size());
		
		TestHelper.assertItem(itemToUpdate, adapter);
		adapter.endSync();
	}
	
	@Test
	public void shouldDelete(){
		SplitAdapter adapter = makeAdapter();
		RDFSchema rdfSchema = (RDFSchema)((HibernateContentAdapter)adapter.getContentAdapter()).getSchema();
		HibernateMsAccessToRDFMapping mapping = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)adapter.getContentAdapter()).getMapping();

		List<Item> items = adapter.getAll();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		int size = items.size();
		
		String id = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("id", id);
		properties.put("name", "sol");
		RDFInstance instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		Item itemToDelete = item.clone();
		itemToDelete.getSync().delete("jmt", new Date());
		adapter.update(itemToDelete);	
		
		Item resultItem = adapter.get(item.getSyncId());
		Assert.assertTrue(resultItem.isDeleted());
		
		id = IdGenerator.INSTANCE.newID();
		properties = new HashMap<String, Object>();
		properties.put("id", id);
		properties.put("name", "sol");
		instance = rdfSchema.createNewInstanceFromProperties(id, properties);
		
		identifiableContent = new IdentifiableContent(instance.asElementXML(), mapping, id);
		item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		adapter.add(item);	
		
		adapter.delete(item.getSyncId());

		resultItem = adapter.get(item.getSyncId());
		Assert.assertTrue(resultItem.isDeleted());
				
		items = adapter.getAll();
		adapter.endSync();
		
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(size + 2, items.size());
		
	}
	
	@Test
	public void shouldSync(){
		String fileName = TestHelper.fileName("msAccess_hibernate_repId_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);

		SplitAdapter adapter = makeAdapter();

		SyncEngine syncEngine = new SyncEngine(feedAdapter, adapter);
		
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
	}
	
	// PRIVATE 
	private SplitAdapter makeAdapter() {
		String fileName = TestHelper.fileName("msAccess_"+IdGenerator.INSTANCE.newID()+".mdb");
		String mdbFileName = getMsAccessFileNameToTest(fileName);
		return MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(
				mdbFileName, 
				"myTable", 
				"http://localhost:8080/mesh4x/feeds", 
				TestHelper.baseDirectoryForTest(), 
				NullIdentityProvider.INSTANCE);
	}
// TODO (JMT) XHTT driver HibernateSyncAdapter tests	
//	private SplitAdapter makeAdapter(boolean useDefaultDriver) {
//		String fileName = TestHelper.fileName("msAccess_"+IdGenerator.INSTANCE.newID()+".mdb");
//		String mdbFileName = getMsAccessFileNameToTest(fileName);
//		
//		if(useDefaultDriver){
//			return MsAccessHibernateSyncAdapterFactory.createHibernateAdapter(
//					mdbFileName, 
//					"myTable", 
//					"http://localhost:8080/mesh4x/feeds", 
//					TestHelper.baseDirectoryForTest(), 
//					NullIdentityProvider.INSTANCE);
//		} else {
//			return HibernateSyncAdapterFactory.createHibernateAdapter(
//					"jdbc:access:///" + mdbFileName, 
//					"", 
//					"", 
//					com.hxtt.sql.access.AccessDriver.class, 
//					com.hxtt.support.hibernate.HxttAccessDialect.class, 
//					"myTable", 
//					"http://localhost:8080/mesh4x/feeds", 
//					TestHelper.baseDirectoryForTest(), 
//					NullIdentityProvider.INSTANCE, 
//					null);
//		}
//	}
	
	private String getMsAccessFileNameToTest(String fileName) {
		try{
			String localFileName = this.getClass().getResource("content.mdb").getFile();
			FileUtils.copyFile(localFileName, fileName);
			return new File(fileName).getCanonicalPath();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
