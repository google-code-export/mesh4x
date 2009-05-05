package org.mesh4j.ektoo.test;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.ektoo.ISyncAdapterBuilder;
import org.mesh4j.ektoo.SyncAdapterBuilder;
import org.mesh4j.ektoo.properties.PropertiesProvider;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.msaccess.MsAccessRDFSchemaGenerator;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.test.utils.TestHelper;

public class InterRepositoryTest {
	
	@Test
	public void ShouldSyncMySQLToExcelByRDF(){
		String user = "root";
		String password = "test1234";
		String tableName = "user";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
		
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsMySql;
		ISchema sourceSchema = ((HibernateContentAdapter)splitAdapterSource.getContentAdapter()).getMapping().getSchema();
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", "user", "id", (IRDFSchema)sourceSchema);
		
		SyncEngine engine = new SyncEngine(splitAdapterSource,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		
	}
	
	@Test
	public void ShouldSyncExcelToMySql(){
		String user = "root";
		String password = "test1234";
		String tableName = "user";
		
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		ISyncAdapter sourceAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", "user", "id");
		
		ISyncAdapter targetAsMySql =  builder.createMySQLAdapter(user, password,"localhost" ,3306,"mesh4xdb",tableName);
		
		SyncEngine engine = new SyncEngine(sourceAsExcel,targetAsMySql);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
	}
	
	
	
	@Test
	public void ShouldSyncMsAccessToExcelByRDF() throws IOException{
		
		String rdfBaseURl = "http://localhost:8080/mesh4x/feeds" +"/aktoo"+"#";
		ISyncAdapterBuilder builder = new SyncAdapterBuilder(new PropertiesProvider());
		
		ISyncAdapter sourceAsAccess = builder.createMsAccessAdapter(TestHelper.baseDirectoryForTest() +"aktoo.mdb" , "aktoo");
		SplitAdapter splitAdapterSource = (SplitAdapter)sourceAsAccess;
		IRDFSchema sourceSchema = MsAccessRDFSchemaGenerator.extractRDFSchema(TestHelper.baseDirectoryForTest() +"aktoo.mdb", "aktoo", "aktoo", rdfBaseURl);
		
		ISyncAdapter targetAsExcel = builder.createMsExcelAdapter(TestHelper.baseDirectoryForTest() + "contentFile.xls", "aktoo", "id", sourceSchema);
		
		SyncEngine engine = new SyncEngine(splitAdapterSource,targetAsExcel);
		List<Item> listOfConflicts = engine.synchronize();
		Assert.assertEquals(0, listOfConflicts.size());
		
	}
	
}
