package org.mesh4j.ektoo.test;

import java.util.List;

import org.junit.Test;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class InterRepositoryTest {
	
	@Test
	public void syncMySQLToExcel(){
		String user = "root";
		String password = "test1234";
		String tableName = "user";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
		
		
		SplitAdapter splitAdapter = (SplitAdapter)sourceAsMySql;
		
		ISchema sourceSchema = ((HibernateContentAdapter)splitAdapter.getContentAdapter()).getMapping().getSchema();
		
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter((IRDFSchema)sourceSchema, "c:/jtest/contentFile.xls", "user", "id");
	
		SyncEngine engine = new SyncEngine(splitAdapter,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		
	}
}
