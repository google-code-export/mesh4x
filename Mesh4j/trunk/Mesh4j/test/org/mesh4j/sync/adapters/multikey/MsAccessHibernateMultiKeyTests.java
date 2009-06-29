package org.mesh4j.sync.adapters.multikey;

import java.util.List;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class MsAccessHibernateMultiKeyTests {

//	//@Test
//	public void testReplicationID()throws Exception{
//		String filename = TestHelper.baseDirectoryRootForTest() + "ms-access/multikey.mdb";
//		String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=";
//		database+= filename.trim() + ";DriverID=22;READONLY=false}"; // add on to the end 
// 
//		JdbcOdbcDriver driver = (JdbcOdbcDriver)Class.forName("sun.jdbc.odbc.JdbcOdbcDriver").newInstance();
//		Assert.assertNotNull(driver);
//		
//		java.util.Properties prop = new java.util.Properties();
//	     //prop.put("charSet", "UTF-16");
//	       prop.put("user", "");
//	       prop.put("password", "");
//
//		Connection conn = DriverManager.getConnection(database, prop);
//		
//		Statement command = conn.createStatement();
//		ResultSet rs = command.executeQuery("select settlement0_.id as id2_, settlement0_.Field1 as Field2_2_, settlement0_.Field2 as Field3_2_, settlement0_.Field3 as Field4_2_, settlement0_.Field4 as Field5_2_, settlement0_.Field5 as Field6_2_, settlement0_.Field6 as Field7_2_, settlement0_.Field7 as Field8_2_, settlement0_.Field8 as Field9_2_, settlement0_.Field9 as Field10_2_, settlement0_.Field10 as Field11_2_, settlement0_.Field11 as Field12_2_, settlement0_.Field12 as Field13_2_ from Settlement_2_Query settlement0_");
//		while (rs.next())
//		{
//			System.out.println("2802E845-7DB0-412D-B318-820116F801EF");
//			byte[] bytes0 = "2802E8457DB0412DB318820116F801EF".getBytes();
//			printBytes(bytes0);
//			
//			byte[] bytesutf16 = "2802E845-7DB0-412D-B318-820116F801EF".getBytes(Charset.forName("UTF-16"));
//			printBytes(bytesutf16);
//
//			byte[] bytes1 = rs.getBytes("id2_");
//			printBytes(bytes1);
//			
//			InputStream is = rs.getBinaryStream("id2_");
//			byte[] bytes = readInputStream(is, 32);
//			printBytes(bytes);
//		}
//		
//	}
//
//	private void printBytes(byte[] bytesutf16) {
//		
//		System.out.print("[");
//		for (byte b : bytesutf16) {
//			System.out.print(b);
//			System.out.print(" ");
//		}
//		System.out.println("]");
//		
//	}
//	
//	public byte[] readInputStream(InputStream inputStream, int maxCycles) throws Exception{
//        byte[]    buffer = new byte[maxCycles];
//        int       bytesRead = 0;
//        int    cycle = 0;
//            
//        // read first byte
//
//        bytesRead = inputStream.read(buffer, cycle++, 1);
//        while (bytesRead != -1 && cycle < maxCycles) {
//                // read next byte.  give offset of previously read bytes.
//        	bytesRead = inputStream.read(buffer, cycle++, 1);
//        }
//        return buffer;
//	}
		
	@Test
	public void shouldGetAll() throws Exception{
		
		String tableName = "multiKeyTable";
		String fileName = TestHelper.fileName("msAccess_multikey_1_"+IdGenerator.INSTANCE.newID()+".mdb");
		String mdbFileName = getMsAccessFileNameToTest(fileName);
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://localhost:8008/mesh4x/feeds");
		
		SplitAdapter adapter = factory.createSyncAdapterFromFile(tableName, mdbFileName, tableName, NullIdentityProvider.INSTANCE);
	
		List<Item> items = adapter.getAll();
		for (Item item : items) {
			System.out.println(item.getContent().getId());
			System.out.println(item.getContent().getPayload().asXML());
		}
	}
	
	//@Test
	public void shouldSync() throws Exception{
	
		String fileName = TestHelper.fileName("msAccess_multikey_"+IdGenerator.INSTANCE.newID());
		FeedAdapter feedAdapter = FeedSyncAdapterFactory.createSyncAdapter(fileName+".xml", NullIdentityProvider.INSTANCE);
		
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(TestHelper.baseDirectoryForTest(), "http://localhost:8008/mesh4x/feeds");
		
		String tableName = "multiKeyTable";
		String mdbFileName = getMsAccessFileNameToTest(fileName+".mdb");
		SplitAdapter msaccessAdapter = factory.createSyncAdapterFromFile(tableName, mdbFileName, tableName, NullIdentityProvider.INSTANCE);
	
		SyncEngine syncEngine = new SyncEngine(feedAdapter, msaccessAdapter);
		
		TestHelper.assertSync(syncEngine);
		
		TestHelper.assertSync(syncEngine);
		
	}
	
	private String getMsAccessFileNameToTest(String fileName) {
		try{
			String localFileName = this.getClass().getResource("DevDB2003.mdb").getFile();
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	
//	@Test
//	public void shouldtest(){
//		RDFSchema schema = new RDFSchema("multiKeyTable", "http://mesh4x/multiKeyTable#", "multiKeyTable");
//		schema.addStringProperty("id1", "id1", "en");
//		schema.addStringProperty("id2", "id2", "en");
//		schema.addStringProperty("name", "name", "en");
//		schema.setIdentifiablePropertyNames(Arrays.asList(new String[]{"id1", "id2"}));
//		
//		String fileName = TestHelper.fileName("msAccess_multikey_3_"+IdGenerator.INSTANCE.newID()+".mdb");
//		String mdbFileName = getMsAccessFileNameToTest(fileName+".mdb");
//		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + mdbFileName.trim() + ";DriverID=22;READONLY=false}";
//		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
//		builder.setProperty("hibernate.dialect", MsAccessDialect.class.getName());
//		builder.setProperty("hibernate.connection.driver_class", JdbcOdbcDriver.class.getName());
//		builder.setProperty("hibernate.connection.url", dbURL);
//		builder.setProperty("hibernate.connection.username", "");
//		builder.setProperty("hibernate.connection.password", "");
//		builder.addMapping(new File(TestHelper.fileName("multiKeyTable.hbm.xml")));
//		builder.addMapping(new File(TestHelper.fileName("multiKeyTable_sync.hbm.xml")));
//		builder.addRDFSchema("multiKeyTable", schema);
//		
//		HibernateContentAdapter adapter = new HibernateContentAdapter(builder, "multiKeyTable");
//		List<IContent> result = adapter.getAll();
//	}
	
}
