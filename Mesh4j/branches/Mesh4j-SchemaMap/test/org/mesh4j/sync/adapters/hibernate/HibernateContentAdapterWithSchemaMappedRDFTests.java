package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.payload.schema.rdf.SchemaMappedRDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.SqlDBUtils;
import org.mesh4j.sync.utils.XMLHelper;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;
import com.mysql.jdbc.Driver;

/**
 * @author sharif uddin
 *
 */
public class HibernateContentAdapterWithSchemaMappedRDFTests {

	private static Map<String, Resource> SYNC_SCHEMA = new HashMap<String, Resource>();
	private static Map<String, String> SCHEMA_CONVERT_MAP = new HashMap<String, String>();
	{
		SYNC_SCHEMA.put("id_x", XSD.xstring);
		SYNC_SCHEMA.put("name_x", XSD.xstring);
		SYNC_SCHEMA.put("pass_x", XSD.xstring);
		SYNC_SCHEMA.put("phone_x", XSD.xlong);
		SYNC_SCHEMA.put("balance_x", XSD.xdouble);
		SYNC_SCHEMA.put("user_2", XSD.ENTITY);
		
		SCHEMA_CONVERT_MAP.put("user2","user_2");
		SCHEMA_CONVERT_MAP.put("id","id_x");
		SCHEMA_CONVERT_MAP.put("name","name_x");
		SCHEMA_CONVERT_MAP.put("pass","pass_x");
		SCHEMA_CONVERT_MAP.put("phone","phone_x");
		SCHEMA_CONVERT_MAP.put("balance","balance_x");
	}
	
	@BeforeClass
	public static void setUpDB(){
		//create database/tables for source
		String sqlFileName = FileUtils.getResourceFileURL("mesh4j_mysql_SchemaMappedRDFTest.sql").getFile();
		SqlDBUtils.executeSqlScript(Driver.class, "jdbc:mysql://localhost", "mesh4xdb", "root", "", sqlFileName);	
	}
	
	@Test
	public void shouldAdd() throws DocumentException{
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		Assert.assertNotNull(adapter.get(id));
		Assert.assertEquals(content.getPayload().asXML(),adapter.get(id).getPayload().asXML());
	}
	
	@Test
	public void shouldAddWithLessField() throws DocumentException{
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//ignore the field balance
		SYNC_SCHEMA_CLONED.remove("balance_x");
		SCHEMA_CONVERT_MAP_CLONED.remove("balance");
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA_CLONED,SCHEMA_CONVERT_MAP_CLONED), "user2");
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchemaLessField, id, "sharif", "123", 1234l, null, adapter);
		adapter.save(content);
		
		Assert.assertNotNull(adapter.get(id));
		Assert.assertEquals(content.getPayload().asXML(),adapter.get(id).getPayload().asXML());
	}
	
	@Test
	public void shouldAddWithDifferentTypeField() throws DocumentException{
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//change the type of field phone from long to string
		SYNC_SCHEMA_CLONED.remove("phone_x");
		SYNC_SCHEMA_CLONED.put("phone_x", XSD.xstring);
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA_CLONED,SCHEMA_CONVERT_MAP_CLONED), "user2");
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchemaDifferentTypeField, id, "sharif", "123", "1234", 123456.0, adapter);
		adapter.save(content);
		
		Assert.assertNotNull(adapter.get(id));
		Assert.assertEquals(content.getPayload().asXML(),adapter.get(id).getPayload().asXML());
	}
	
	@Test
	public void shouldGetItem() throws DocumentException{
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");

		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);

		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);

		Assert.assertEquals(content.getPayload().asXML(),contentLoaded.getPayload().asXML());
		Assert.assertTrue(content.equals(contentLoaded));		
	}
	
	@Test
	public void shouldGetItemWithLessField() throws DocumentException{
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//ignore the field balance
		SYNC_SCHEMA_CLONED.remove("balance_x");
		SCHEMA_CONVERT_MAP_CLONED.remove("balance");
		
		adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA_CLONED,SCHEMA_CONVERT_MAP_CLONED), "user2");
		
		IContent contentLessField = makeContent(testContentSchemaLessField, id, "sharif", "123", 1234l, null, adapter);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		
		Assert.assertEquals(contentLessField.getPayload().asXML(),contentLoaded.getPayload().asXML());
		Assert.assertTrue(contentLessField.equals(contentLoaded));		
	}
	
	@Test
	public void shouldGetItemWithDifferentTypeField() throws DocumentException{
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		adapter.save(content);
		
		Map<String, Resource> SYNC_SCHEMA_CLONED = (Map<String, Resource>) ((HashMap)SYNC_SCHEMA).clone();
		Map<String, String> SCHEMA_CONVERT_MAP_CLONED = (Map<String, String>) ((HashMap)SCHEMA_CONVERT_MAP).clone();
		
		//change the type of field phone from long to string
		SYNC_SCHEMA_CLONED.remove("phone_x");
		SYNC_SCHEMA_CLONED.put("phone_x", XSD.xstring);
		
		adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA_CLONED,SCHEMA_CONVERT_MAP_CLONED), "user2");
		
		IContent contentLessField = makeContent(testContentSchemaDifferentTypeField, id, "sharif", "123", "1234", 123456.0, adapter);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);
		
		Assert.assertEquals(contentLessField.getPayload().asXML(),contentLoaded.getPayload().asXML());
		Assert.assertTrue(contentLessField.equals(contentLoaded));		
	}
	
	@Test
	public void shouldDeleteItem() throws DocumentException{
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		
		String id = TestHelper.newID();
		IContent content = makeContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, adapter);
		
		adapter.save(content);
		
		IContent contentLoaded = adapter.get(id);
		Assert.assertNotNull(contentLoaded);		
		
		adapter.delete(contentLoaded);
		
		contentLoaded = adapter.get(id);
		Assert.assertNull(contentLoaded);			
	}
	
	@Test
	public void shouldUpdateItem() throws DocumentException{
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		
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
	}
		
	@Test
	public void shouldGetAll() throws DocumentException{
	
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		
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
	}

	@Test
	public void shouldDeleteAllItem() throws DocumentException{
		
		HibernateContentAdapter adapter = new HibernateContentAdapter(getBuilder(SYNC_SCHEMA, SCHEMA_CONVERT_MAP), "user2");
		
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

	}
	
	private boolean containsContent(List<IContent> results, IContent content) {
		for (IContent aContent : results) {
			if(aContent.equals(content)){
				return true;
			}
		}
		return false;
	}

	private RDFSchema schema;
	
	protected IHibernateSessionFactoryBuilder getBuilder(Map<String, Resource> syncSchema, Map<String, String> schemaConversionMap) {
		schema = new /*RDFSchema("user2", "http://mesh4x/user2#", "user2");*/
		SchemaMappedRDFSchema("user2", "http://mesh4x/user2#", "user2", syncSchema, schemaConversionMap);
		schema.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("pass", "password", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addLongProperty("phone", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		schema.addDoubleProperty("balance", "balance", IRDFSchema.DEFAULT_LANGUAGE);
		schema.setIdentifiablePropertyName("id");
		
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(HibernateAdapterTests.class.getResource("User2.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User2_sync.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(this.getClass().getResource("xx_hibernate.properties").getFile()));
		builder.addRDFSchema("user2", schema);
		return builder;
	}
	
	private RDFSchema testContentSchema;
	{
		testContentSchema = new RDFSchema("user_2", "http://mesh4x/user_2#", "user_2");
		testContentSchema.addStringProperty("id_x", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addStringProperty("pass_x", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addStringProperty("name_x", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addLongProperty("phone_x", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addDoubleProperty("balance_x", "balance", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.setIdentifiablePropertyName("id_x");
	}
	
	private RDFSchema testContentSchemaLessField;
	{
		testContentSchemaLessField = new RDFSchema("user_2", "http://mesh4x/user_2#", "user_2");
		testContentSchemaLessField.addStringProperty("id_x", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addStringProperty("pass_x", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addStringProperty("name_x", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addLongProperty("phone_x", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.setIdentifiablePropertyName("id_x");
	}
	
	private RDFSchema testContentSchemaDifferentTypeField;
	{
		testContentSchemaDifferentTypeField = new RDFSchema("user_2", "http://mesh4x/user_2#", "user_2");
		testContentSchemaDifferentTypeField.addStringProperty("id_x", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addStringProperty("pass_x", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addStringProperty("name_x", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addStringProperty("phone_x", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.addDoubleProperty("balance_x", "balance", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaDifferentTypeField.setIdentifiablePropertyName("id_x");
	}
	
	protected IContent makeContent(RDFSchema contentSchema, String id, String name, String pass, Long phone, Double balance, HibernateContentAdapter adapter) throws DocumentException {
		RDFInstance rdfInstance = contentSchema.createNewInstance("uri:urn:"+id);
		if(name != null)
			rdfInstance.setProperty("name_x", name);
		if(pass != null)
			rdfInstance.setProperty("pass_x", pass);
		if(phone != null)
			rdfInstance.setProperty("phone_x", phone);
		if(balance != null)
			rdfInstance.setProperty("balance_x", balance);
		rdfInstance.setProperty("id_x", id);
		
		String rdfXml = rdfInstance.asRDFXML();
		Element payload = XMLHelper.parseElement(rdfXml);
		IContent user = new IdentifiableContent(payload, adapter.getMapping(), id);
		return user;
	}
	
	protected IContent makeContent(RDFSchema contentSchema, String id, String name, String pass, String phone, Double balance, HibernateContentAdapter adapter) throws DocumentException {
		RDFInstance rdfInstance = contentSchema.createNewInstance("uri:urn:"+id);
		if(name != null)
			rdfInstance.setProperty("name_x", name);
		if(pass != null)
			rdfInstance.setProperty("pass_x", pass);
		if(phone != null)
			rdfInstance.setProperty("phone_x", phone);
		if(balance != null)
			rdfInstance.setProperty("balance_x", balance);
		rdfInstance.setProperty("id_x", id);
		
		String rdfXml = rdfInstance.asRDFXML();
		Element payload = XMLHelper.parseElement(rdfXml);
		IContent user = new IdentifiableContent(payload, adapter.getMapping(), id);
		return user;
	}
}
