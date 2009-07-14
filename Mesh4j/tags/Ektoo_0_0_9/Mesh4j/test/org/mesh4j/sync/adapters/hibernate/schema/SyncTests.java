package org.mesh4j.sync.adapters.hibernate.schema;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateSyncAdapterFactory;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class SyncTests {

	@Test
	public void shouldSyncDBReadingRDFSchemaFromAdapterAandCreateingTableInAdapterB() throws Exception{
		ISyncAdapter adapterA = HibernateSyncAdapterFactory.createHibernateAdapter(
			"jdbc:mysql:///mesh4xdb", 
			"root", 
			"", 
			com.mysql.jdbc.Driver.class,
			org.hibernate.dialect.MySQLDialect.class,
			"mesh_example", 
			"http://mesh4x/test", 
			TestHelper.baseDirectoryRootForTest(),
			NullIdentityProvider.INSTANCE,
			null);
			//new File(this.getClass().getResource("test_mysql_hibernate.properties").getFile()));

		ISyncAdapter adapterB = new InMemorySyncAdapter("mesh_example", NullIdentityProvider.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(adapterA, adapterB);
		TestHelper.assertSync(syncEngine);
		
	}

}
