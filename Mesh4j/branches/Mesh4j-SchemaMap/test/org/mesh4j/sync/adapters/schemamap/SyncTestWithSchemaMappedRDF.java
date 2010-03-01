package org.mesh4j.sync.adapters.schemamap;


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
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.hibernate.HibernateAdapterTests;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.IHibernateSessionFactoryBuilder;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.payload.schema.rdf.SchemaMappedRDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.SqlDBUtils;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.XSD;
import com.mysql.jdbc.Driver;

/**
 * @author sharif uddin
 *
 */
public class SyncTestWithSchemaMappedRDF {

	private static Map<String, Resource> SYNC_SCHEMA = new HashMap<String, Resource>();
	private static Map<String, String> SCHEMA_CONVERT_MAP_MYSQL = new HashMap<String, String>();
	private static Map<String, String> SCHEMA_CONVERT_MAP_MSACCESS = new HashMap<String, String>();
	private static Map<String, String> SCHEMA_CONVERT_MAP_MSEXCEL = new HashMap<String, String>();
	{
		SYNC_SCHEMA.put("user_x", XSD.ENTITY);
		SYNC_SCHEMA.put("id_x", XSD.xstring);
		SYNC_SCHEMA.put("name_x", XSD.xstring);
		SYNC_SCHEMA.put("pass_x", XSD.xstring);
		SYNC_SCHEMA.put("phone_x", XSD.xlong);
		SYNC_SCHEMA.put("balance_x", XSD.xdouble);
		
		SCHEMA_CONVERT_MAP_MYSQL.put("user2","user_x");
		SCHEMA_CONVERT_MAP_MYSQL.put("id","id_x");
		SCHEMA_CONVERT_MAP_MYSQL.put("name","name_x");
		SCHEMA_CONVERT_MAP_MYSQL.put("pass","pass_x");
		SCHEMA_CONVERT_MAP_MYSQL.put("phone","phone_x");
		SCHEMA_CONVERT_MAP_MYSQL.put("balance","balance_x");
		
		SCHEMA_CONVERT_MAP_MSACCESS.put("user_x","user_x");
		SCHEMA_CONVERT_MAP_MSACCESS.put("id_x","id_x");
		SCHEMA_CONVERT_MAP_MSACCESS.put("name_x","name_x");
		SCHEMA_CONVERT_MAP_MSACCESS.put("pass_x","pass_x");
		SCHEMA_CONVERT_MAP_MSACCESS.put("phone_x","phone_x");
		SCHEMA_CONVERT_MAP_MSACCESS.put("balance_x","balance_x");
	}
	
	@BeforeClass
	public static void setUpDB(){
		//create database/tables for source
		String sqlFileName = FileUtils.getResourceFileURL("mesh4j_mysql_SchemaMappedRDFTest.sql").getFile();
		SqlDBUtils.executeSqlScript(Driver.class, "jdbc:mysql://localhost", "mesh4xdb", "root", "", sqlFileName);	
	}
	
	@Test
	public void shouldSync() throws DocumentException{
		
		SplitAdapter adapterMysql = HibernateSyncAdapterFactory.createHibernateAdapter(
				"jdbc:mysql:///mesh4xdb", 
				"root", 
				"", 
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class,
				"user2", 
				"http://mesh4x", 
				TestHelper.baseDirectoryRootForTest(),
				NullIdentityProvider.INSTANCE,
				null, 
				SYNC_SCHEMA,
				SCHEMA_CONVERT_MAP_MYSQL);
		
		
		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");

		SplitAdapter adapterMsaccess = factory.createSyncAdapterFromFile(
				"user_x", 
				sourceFileName, 
				"user_x", 
				NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, 
				SCHEMA_CONVERT_MAP_MSACCESS);
		
		SyncEngine syncEngine = new SyncEngine(adapterMysql , adapterMsaccess);
		TestHelper.assertSync(syncEngine);
		
		adapterMysql.beginSync();
		adapterMsaccess.beginSync();
		
		String id = TestHelper.newID();
		IContent content = makeMysqlContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, (HibernateContentAdapter) adapterMysql.getContentAdapter());
		adapterMysql.getContentAdapter().save(content);
		
		syncEngine = new SyncEngine(adapterMysql , adapterMsaccess);
		TestHelper.assertSync(syncEngine);
		
		FileUtils.delete(sourceFileName);
	}
	
	@Test
	public void shouldSyncWithMsExcel() throws DocumentException{
		
		SYNC_SCHEMA.clear();
		SYNC_SCHEMA.put("customer", XSD.ENTITY);
		SYNC_SCHEMA.put("id", XSD.xstring);
		SYNC_SCHEMA.put("firstName", XSD.xstring);
		SYNC_SCHEMA.put("middleName", XSD.xstring);
		SYNC_SCHEMA.put("lastName", XSD.xstring);
		SYNC_SCHEMA.put("fathersFirstName", XSD.xstring);
		SYNC_SCHEMA.put("mothersFirstName", XSD.xstring);
		SYNC_SCHEMA.put("spouseFirstName", XSD.xstring);
		SYNC_SCHEMA.put("workPhone", XSD.xstring);
		SYNC_SCHEMA.put("homePhone", XSD.xstring);
		SYNC_SCHEMA.put("mobilePhone", XSD.xstring);
		SYNC_SCHEMA.put("nationality", XSD.xstring);
		SYNC_SCHEMA.put("religion", XSD.xstring);
		//SYNC_SCHEMA.put("birthDate", XSD.dateTime);		
		SYNC_SCHEMA.put("province", XSD.xstring);
		SYNC_SCHEMA.put("postalCode", XSD.xstring);
		SYNC_SCHEMA.put("email", XSD.xstring);
		SYNC_SCHEMA.put("houseInfo", XSD.xstring);
		SYNC_SCHEMA.put("permHouseInfo", XSD.xstring);	
		
		SCHEMA_CONVERT_MAP_MYSQL.clear();
		SCHEMA_CONVERT_MAP_MYSQL.put("customer","customer"); 
		SCHEMA_CONVERT_MAP_MYSQL.put("id","id");
		SCHEMA_CONVERT_MAP_MYSQL.put("firstName","firstName");
		SCHEMA_CONVERT_MAP_MYSQL.put("middleName","middleName");
		SCHEMA_CONVERT_MAP_MYSQL.put("lastName","lastName");
		SCHEMA_CONVERT_MAP_MYSQL.put("fathersFirstName","fathersFirstName");
		SCHEMA_CONVERT_MAP_MYSQL.put("mothersFirstName","mothersFirstName");
		SCHEMA_CONVERT_MAP_MYSQL.put("spouseFirstName","spouseFirstName");
		SCHEMA_CONVERT_MAP_MYSQL.put("workPhone","workPhone");
		SCHEMA_CONVERT_MAP_MYSQL.put("homePhone","homePhone");
		SCHEMA_CONVERT_MAP_MYSQL.put("mobilePhone","mobilePhone");
		SCHEMA_CONVERT_MAP_MYSQL.put("nationality","nationality");
		SCHEMA_CONVERT_MAP_MYSQL.put("religion","religion");
		//SCHEMA_CONVERT_MAP_MYSQL.put("birthDate","birthDate");
		SCHEMA_CONVERT_MAP_MYSQL.put("province","province");
		SCHEMA_CONVERT_MAP_MYSQL.put("postalCode","postalCode");
		SCHEMA_CONVERT_MAP_MYSQL.put("email","email");
		SCHEMA_CONVERT_MAP_MYSQL.put("houseInfo","houseInfo");
		SCHEMA_CONVERT_MAP_MYSQL.put("permHouseInfo","permHouseInfo");

		SCHEMA_CONVERT_MAP_MSEXCEL.clear();
		SCHEMA_CONVERT_MAP_MSEXCEL.put("ClientLists","customer");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("clientid","id");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("fnam","firstName");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("mnam","middleName");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("lnam","lastName");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("fmnam","fathersFirstName");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("mothername","mothersFirstName");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("husbandname","spouseFirstName");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("offph","workPhone");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("resph","homePhone");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("mobil","mobilePhone");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("nationa","nationality");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("relig","religion");
		//SCHEMA_CONVERT_MAP_MSEXCEL.put("bdat","birthDate");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("maildist","province");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("postcode","postalCode");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("pemail","email");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("madd","houseInfo");
		SCHEMA_CONVERT_MAP_MSEXCEL.put("padd","permHouseInfo");

		SplitAdapter adapterMysql = HibernateSyncAdapterFactory.createHibernateAdapter(
				"jdbc:mysql:///javarosa2", 
				"root", 
				"", 
				com.mysql.jdbc.Driver.class,
				org.hibernate.dialect.MySQLDialect.class,
				"customer", 
				"http://mesh4x", 
				TestHelper.baseDirectoryRootForTest(),
				NullIdentityProvider.INSTANCE,
				null, 
				SYNC_SCHEMA,
				SCHEMA_CONVERT_MAP_MYSQL);
		
		SplitAdapter adapterMsexcel = MsExcelRDFSyncAdapterFactory.createSyncAdapter(
				new MsExcel("d://HG_PP.xls"), 
				"ClientLists", 
				new String[]{"clientid"}, 
				null, 
				NullIdentityProvider.INSTANCE, 
				"http://mesh4x",
				SYNC_SCHEMA,
				SCHEMA_CONVERT_MAP_MSEXCEL);

/*		String sourceFileName = getMsAccessFileNameToTest("mesh4j_access_SchemaMappedRDFTest.mdb");
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://mesh4x");

		SplitAdapter adapterMsaccess = factory.createSyncAdapterFromFile(
				"user_x", 
				sourceFileName, 
				"user_x", 
				NullIdentityProvider.INSTANCE, 
				SYNC_SCHEMA, 
				SCHEMA_CONVERT_MAP_MSACCESS);
*/		
		SyncEngine syncEngine = new SyncEngine(adapterMsexcel, adapterMysql);
		
		TestHelper.assertSync(syncEngine);
//		
//		adapterMysql.beginSync();
//		adapterMsexcel.beginSync();
//		
//		String id = TestHelper.newID();
//		IContent content = makeMysqlContent(testContentSchema, id, "sharif", "123", 1234l, 123456.0, (HibernateContentAdapter) adapterMysql.getContentAdapter());
//		adapterMysql.getContentAdapter().save(content);
//		
//		syncEngine = new SyncEngine(adapterMysql , adapterMsexcel);
//		TestHelper.assertSync(syncEngine);
		
		//FileUtils.delete(sourceFileName);
	}
	
	private RDFSchema schema;
	
	protected IHibernateSessionFactoryBuilder getBuilder(Map<String, Resource> syncSchema, Map<String, String> schemaConversionMap) {
		schema = new SchemaMappedRDFSchema("user2", "http://mesh4x/user2#", "user2", syncSchema, schemaConversionMap);
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
	
	private String getMsAccessFileNameToTest(String localName) {
		try{
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
		testContentSchema = new RDFSchema("user_x", "http://mesh4x/user_x#", "user_x");
		testContentSchema.addStringProperty("id_x", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addStringProperty("pass_x", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addStringProperty("name_x", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addLongProperty("phone_x", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.addDoubleProperty("balance_x", "balance", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchema.setIdentifiablePropertyName("id_x");
	}
	
	private RDFSchema testContentSchemaLessField;
	{
		testContentSchemaLessField = new RDFSchema("user2", "http://mesh4x/user2#", "user2");
		testContentSchemaLessField.addStringProperty("id", "id", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addStringProperty("pass", "password", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addStringProperty("name", "name", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.addLongProperty("phone", "phone", IRDFSchema.DEFAULT_LANGUAGE);
		testContentSchemaLessField.setIdentifiablePropertyName("id");
	}
	
	protected IContent makeMysqlContent(RDFSchema contentSchema, String id, String name, String pass, Long phone, Double balance, HibernateContentAdapter adapter) throws DocumentException {
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
