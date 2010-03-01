package org.mesh4j.sync.adapters.msaccess;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;

public class MsAccessAdapterWithWithSchemaMappedRDFTests {

	private static Map<String, Resource> SYNC_SCHEMA = new HashMap<String, Resource>();
	private static Map<String, String> SCHEMA_CONVERT_MAP = new HashMap<String, String>();
	{
		SYNC_SCHEMA.put("id_y", XSD.xstring);
		SYNC_SCHEMA.put("name_y", XSD.xstring);
		SYNC_SCHEMA.put("pass_y", XSD.xstring);
		SYNC_SCHEMA.put("phone_y", XSD.xlong);
		SYNC_SCHEMA.put("balance_y", XSD.xdouble);
		SYNC_SCHEMA.put("user_y", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.put("user_x","user_y");
		SCHEMA_CONVERT_MAP.put("id_x","id_y");
		SCHEMA_CONVERT_MAP.put("name_x","name_y");
		SCHEMA_CONVERT_MAP.put("pass_x","pass_y");
		SCHEMA_CONVERT_MAP.put("phone_x","phone_y");
		SCHEMA_CONVERT_MAP.put("balance_x","balance_y");
	}

	@Test
	public void shouldAdd() throws DocumentException{
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		Assert.assertEquals(content.getPayload().asXML(), contentLoaded.getPayload().asXML());

		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldAddWithLessField() throws DocumentException{
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//ignore the field balance
		SYNC_SCHEMA_CLONED.remove("balance_y");
		SCHEMA_CONVERT_MAP_CLONED.remove("balance_x");
		
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA_CLONED, SCHEMA_CONVERT_MAP_CLONED)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchemaLessField, id, "sharif", "123", 1234l, null, adapter);
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		Assert.assertEquals(content.getPayload().asXML(), contentLoaded.getPayload().asXML());
		
		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldAddWithDifferentTypeField() throws DocumentException{
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//change the type of field phone from long to string
		SYNC_SCHEMA_CLONED.remove("phone_y");
		SYNC_SCHEMA_CLONED.put("phone_y", XSD.xstring);
		
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA_CLONED, SCHEMA_CONVERT_MAP_CLONED)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchemaDifferentTypeField, id, "sharif", "123", "1234", 123456.0, adapter);
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		Assert.assertEquals(content.getPayload().asXML(), contentLoaded.getPayload().asXML());
		
		FileUtils.delete(sourceFileName);
	}
	
	
	@Test
	public void shouldGetItem() throws DocumentException{
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);

		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);

		Assert.assertEquals(content.getPayload().asXML(),contentLoaded.getPayload().asXML());
		Assert.assertTrue(content.equals(contentLoaded));	
		
		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldGetItemWithLessField() throws DocumentException{
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//ignore the field balance
		SYNC_SCHEMA_CLONED.remove("balance_y");
		SCHEMA_CONVERT_MAP_CLONED.remove("balance_x");
		
		adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA_CLONED, SCHEMA_CONVERT_MAP_CLONED)).getContentAdapter();
		
		IContent contentLessField = makeContent(testContentSchemaLessField, id, "sharif", "123", 1234l, null, adapter);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		
		Assert.assertEquals(contentLessField.getPayload().asXML(),contentLoaded.getPayload().asXML());
		Assert.assertTrue(contentLessField.equals(contentLoaded));		
		
		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldGetItemWithDifferentTypeField() throws DocumentException{
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//change the type of field phone from long to string
		SYNC_SCHEMA_CLONED.remove("phone_y");
		SYNC_SCHEMA_CLONED.put("phone_y", XSD.xstring);
		
		adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA_CLONED, SCHEMA_CONVERT_MAP_CLONED)).getContentAdapter();
		
		IContent contentLessField = makeContent(testContentSchemaDifferentTypeField, id, "sharif", "123", "1234", 123456.0, adapter);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		
		Assert.assertEquals(contentLessField.getPayload().asXML(),contentLoaded.getPayload().asXML());
		Assert.assertTrue(contentLessField.equals(contentLoaded));		
		
		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldDeleteItem() throws DocumentException{
		
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);		
		
		adapter.delete(contentLoaded);
		
		contentLoaded = adapter.get(id);
		Assert.assertNull(contentLoaded);
		
		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldUpdateItem() throws DocumentException{
		
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);		
		
		IContent contentUpdated = makeContent(testContentSchema, id, "jose", "456", 5678l, 123456.0, adapter);
		adapter.save(contentUpdated);
		
		contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		Assert.assertFalse(content.equals(contentLoaded));		
		Assert.assertTrue(contentUpdated.equals(contentLoaded));
		
		FileUtils.delete(sourceFileName);
	}
		
	@Test
	public void shouldGetAll() throws DocumentException{
	
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		Date sinceDate = TestHelper.nowSubtractDays(1);
		
		String id0 = TestHelper.newID();
		IContent content0 = makeContent(testContentSchema, id0, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content0);
		
		String id1 = TestHelper.newID();
		IContent content1 = makeContent(testContentSchema, id1, "marcelo", "456", 5678l, 123456.0, adapter);
		adapter.save(content1);
		
		List<IContent> results = adapter.getAll(sinceDate);
		Assert.assertNotNull(results);
		
		Assert.assertTrue(containsContent(results, content0));
		Assert.assertTrue(containsContent(results, content1));	
		
		FileUtils.delete(sourceFileName);
	}

	@Test
	public void shouldDeleteAllItem() throws DocumentException{
		
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");
		
		HibernateContentAdapter adapter = (HibernateContentAdapter)((SplitAdapter) factory.createSyncAdapterFromFile(
				"user_x", sourceFileName, "user_x", NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, SCHEMA_CONVERT_MAP)).getContentAdapter();
		
		String id0 = TestHelper.newID();
		IContent content0 = makeContent(testContentSchema, id0, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content0);
		
		String id1 = TestHelper.newID();
		IContent content1 = makeContent(testContentSchema, id1, "marcelo", "456", 5678l, 123456.0, adapter);
		adapter.save(content1);
		
		List<IContent> contentList = adapter.getAll();
		Assert.assertNotNull(contentList);		
		
		adapter.deleteAll();
		
		IContent contentLoaded = adapter.get(id0);
		Assert.assertNull(contentLoaded);
		
		contentLoaded = adapter.get(id1);
		Assert.assertNull(contentLoaded);
		
		contentList = adapter.getAll();
		Assert.assertTrue(contentList.size()==0);
		
		FileUtils.delete(sourceFileName);

	}
	
	private boolean containsContent(List<IContent> results, IContent content) {
		for (IContent aContent : results) {
			if(aContent.equals(content)){
				return true;
			}
		}
		return false;
	}
	
	private String getMsAccessFileNameToTest(String localName) {
		try{
			//String localFileName = this.getClass().getResource(localName).getFile();
			String localFileName = FileUtils.getResourceFileURL(localName).getFile();
			String fileName = TestHelper.fileName(localName.substring(0, localName.length() -4)+IdGenerator.INSTANCE.newID()+".mdb");
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private RDFSchema testContentSchema;
	{
		testContentSchema = new RDFSchema("user_y", "http://mesh4x/user_y#", "user_y");
		testContentSchema.addStringProperty("id_y", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addStringProperty("pass_y", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addStringProperty("name_y", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addLongProperty("phone_y", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addDoubleProperty("balance_y", "balance", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.setIdentifiablePropertyName("id_y");
	}
	
	private RDFSchema testContentSchemaLessField;
	{
		testContentSchemaLessField = new RDFSchema("user_y", "http://mesh4x/user_y#", "user_y");
		testContentSchemaLessField.addStringProperty("id_y", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addStringProperty("pass_y", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addStringProperty("name_y", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addLongProperty("phone_y", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.setIdentifiablePropertyName("id_y");
	}
	
	private RDFSchema testContentSchemaDifferentTypeField;
	{
		testContentSchemaDifferentTypeField = new RDFSchema("user_y", "http://mesh4x/user_y#", "user_y");
		testContentSchemaDifferentTypeField.addStringProperty("id_y", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addStringProperty("pass_y", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addStringProperty("name_y", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addStringProperty("phone_y", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addDoubleProperty("balance_y", "balance", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.setIdentifiablePropertyName("id_y");
	}
	
	protected IContent makeContent(RDFSchema contentSchema, String id, String name, String pass, Long phone, Double balance, HibernateContentAdapter adapter) throws DocumentException {
		RDFInstance rdfInstance = contentSchema.createNewInstance("uri:urn:"+id);
		if(name != null)
			rdfInstance.setProperty("name_y", name);
		if(pass != null)
			rdfInstance.setProperty("pass_y", pass);
		if(phone != null)
			rdfInstance.setProperty("phone_y", phone);
		if(balance != null)
			rdfInstance.setProperty("balance_y", balance);
		rdfInstance.setProperty("id_y", id);
		
		String rdfXml = rdfInstance.asRDFXML();
		Element payload = XMLHelper.parseElement(rdfXml);
		IContent user = new IdentifiableContent(payload, adapter.getMapping(), id);
		return user;
	}
	
	protected IContent makeContent(RDFSchema contentSchema, String id, String name, String pass, String phone, Double balance, HibernateContentAdapter adapter) throws DocumentException {
		RDFInstance rdfInstance = contentSchema.createNewInstance("uri:urn:"+id);
		if(name != null)
			rdfInstance.setProperty("name_y", name);
		if(pass != null)
			rdfInstance.setProperty("pass_y", pass);
		if(phone != null)
			rdfInstance.setProperty("phone_y", phone);
		if(balance != null)
			rdfInstance.setProperty("balance_y", balance);
		rdfInstance.setProperty("id_y", id);
		
		String rdfXml = rdfInstance.asRDFXML();
		Element payload = XMLHelper.parseElement(rdfXml);
		IContent user = new IdentifiableContent(payload, adapter.getMapping(), id);
		return user;
	}
}
