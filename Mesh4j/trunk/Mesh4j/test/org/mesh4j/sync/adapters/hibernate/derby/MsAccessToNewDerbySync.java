package org.mesh4j.sync.adapters.hibernate.derby;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.hibernate.msaccess.MsAccessHibernateSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;

import sun.jdbc.odbc.JdbcOdbcDriver;

/**
 * Tests mesh synchro between an access db and a Derby. The main idea is to test the creation of the schema in the Derby DB. 
 * 
 */
public class MsAccessToNewDerbySync {

	private static final Object[][] TEST_DATA = {
					new Object[] { 1, "Hello" },
					new Object[] { 2, "World" },
			};
	private ISyncAdapter adapterMsAccess;
	private ISyncAdapter adapterDerby;


	/**
	 * Before each test we have to:<br>
	 * <li> Delete and recreate the Derby DB
	 * <li> Delete the tables in the MDB
	 * <li> Fill with data the tables in the MDB
	 */
	@Before
	public void setUp() throws Exception {

		DerbyHelper.deleteDerbyDB();
		
		DerbyHelper.createDerbyDB();
		
		prepareMsAccessDB();
		Connection access = getAccessConnection();
		TestHelper.fillTestTableWithData(access);
		access.close();
		
	}

	/**
	 * Creates the MsAccess and Hibernate adapters.
	 */
	private void createAdapters() throws IOException {

		String mappingDirectory1 = TestHelper.getThisPath() + "/Mapping1/";
		String mappingDirectory2 = TestHelper.getThisPath() + "/Mapping2/";
		String rdf = "http://localhost:8080/mesh4x/feeds";

		// delete and recreate the directories 
		TestHelper.createNewDir(mappingDirectory1);
		TestHelper.createNewDir(mappingDirectory2);
		
		// create the access adapter
		MsAccessHibernateSyncAdapterFactory factory = new MsAccessHibernateSyncAdapterFactory(mappingDirectory1, rdf);
		adapterMsAccess = factory.createSyncAdapterFromFile("test", getAccessFile(), "test", NullIdentityProvider.INSTANCE);
		
		// create the hibernate adapter for derby
		HibernateContentAdapter hibernateContentAdapter = (HibernateContentAdapter)((SplitAdapter)adapterMsAccess).getContentAdapter();
		IRDFSchema schema = (IRDFSchema) hibernateContentAdapter.getSchema();

		adapterDerby = HibernateSyncAdapterFactory.createHibernateAdapter("jdbc:derby:" + DerbyHelper.getDerbyFile(), "", "", org.apache.derby.jdbc.EmbeddedDriver.class, org.hibernate.dialect.DerbyWithLongStringDialect.class, "test", schema, rdf, mappingDirectory2, NullIdentityProvider.INSTANCE, null);
	}

	/**
	 * @return the physical name of the access db.
	 */
	private String getAccessFile() {
		return TestHelper.getThisPath() + File.separator + "Test.mdb";
	}

	/**
	 * Prepares the mdb for action: deletes and recreates the tables.
	 */
	private void prepareMsAccessDB() throws Exception{
		Connection accessDB = null;
		
		accessDB = getAccessConnection();
		TestHelper.dropTestTables(accessDB);
		TestHelper.createTestTable(accessDB);
		accessDB.close();
	}

	/**
	 * Creates a connection to the mdb
	 */
	private Connection getAccessConnection()
			throws ClassNotFoundException, SQLException {
		Class.forName(JdbcOdbcDriver.class.getName());
		
		String accessMDB = getAccessFile(); 
		String dbURL = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + accessMDB + ";DriverID=22;READONLY=false}";
		Connection accessDB = DriverManager.getConnection(dbURL, "","");
		return accessDB;
	}
	
	/**
	 * Test the creation of the derby DB. 
	 */
	@Test
	public void syncWithAnEmptyDB() throws Exception {
		createAdapters();
		SyncEngine syncEngine = new SyncEngine(adapterMsAccess, adapterDerby);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
		Connection connection = DerbyHelper.getDerbyConnection();
		TestHelper.assertDataWasInserted(connection, TEST_DATA);
		connection.close();
	}
	
	@Test
	public void syncWithANonEmptyDB() throws Exception {
		
		Connection connection = DerbyHelper.getDerbyConnection();
		TestHelper.dropTestTables(connection);
		TestHelper.createTestTable(connection);
		TestHelper.fillTestTableWithData(connection);
		connection.close();
		
		createAdapters();
		SyncEngine syncEngine = new SyncEngine(adapterMsAccess, adapterDerby);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertEquals(0, conflicts.size());
		
	}

}
