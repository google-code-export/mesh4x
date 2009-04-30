package org.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.List;

import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.AbstractSyncEngineTest;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.msexcel.MsExcel;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelSyncRepository;
import org.mesh4j.sync.adapters.msexcel.MsExcelToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.validations.MeshException;


public class HibernateVsExcelSyncEngineTest extends AbstractSyncEngineTest {

	@Override
	protected ISyncAdapter makeRightRepository(Item... items) {
		
		IIdentityProvider identityProvider = NullIdentityProvider.INSTANCE;
		IIdGenerator idGenerator = IdGenerator.INSTANCE;
		String excelFileName = TestHelper.fileName("user.xls");
		String sourceAlias ="user";	
		String sheetName = "user";
		String idColumnName = "id";
		RDFSchema rdfSchema = null;

		MsExcel excel = new MsExcel(excelFileName);
		File excelFile = new File(excelFileName);
		MsExcelToRDFMapping rdfMapping;
		
		if(false){			
			try {
				rdfSchema = MsExcelToRDFMapping.extractRDFSchema(excel, sheetName);
			} catch (Exception e) {	throw new MeshException(e); }
			
			//check if  rdf schema of the shit! matches with the rdfSchema from other repo/adapter
			//if not remove the file execute rdfMapping.createDataSource(excelFileName)
			//or do appropriate change to that sheet to match with the rdf schema			
			rdfMapping = new MsExcelToRDFMapping(rdfSchema, idColumnName);
		}else{
			// get the rdf schema that is available in other repository/adapter!
			
			//providing a sample rdf schema for test
			rdfSchema = new RDFSchema(sheetName, "http://mesh4x/ektooclient/"+sheetName+"#", sheetName);
			rdfSchema.addStringProperty("Id", "id", "en");
			rdfSchema.addStringProperty("Name", "name", "en");
			rdfSchema.addIntegerProperty("Pass", "pass", "en");
			
			rdfMapping = new MsExcelToRDFMapping(rdfSchema, idColumnName);	
			
			try {
				rdfMapping.createDataSource(excelFileName);
				excel = new MsExcel(excelFileName);
			} catch (Exception e) { throw new MeshException(e); }
		}
				
		MsExcelContentAdapter contentAdapter = new MsExcelContentAdapter(excel, rdfMapping, sheetName);	
		MsExcelSyncRepository syncRepo = new MsExcelSyncRepository(excel, identityProvider, idGenerator);
		SplitAdapter excelRepo = new SplitAdapter(syncRepo, contentAdapter, identityProvider);
		
		/*ISyncAdapter excelRepo;
		String sourceDefinition = MsExcelSyncAdapterFactory.createSourceDefinition(excelFileName, sheetName, idColumnName);
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(rdfSchema);		
		
		try {
			excelRepo = factory.createSyncAdapter(sourceAlias, sourceDefinition, identityProvider);
		} catch (Exception e) { throw new MeshException(e); }*/
	
		return excelRepo;
	}

	@Override
	protected ISyncAdapter makeLeftRepository(Item... items) {
		
		String user = "root";
		String password = "admin";
		String tableName = "mesh_sync_example";
		String syncTableName = "mesh_sync_info";
		String rdfURL = "http://mesh4x/MeshSyncExample#";
		String connectionURL = "jdbc:mysql:///mesh4xdb";
		Class driverClass = com.mysql.jdbc.Driver.class;
		Class dialectClass = org.hibernate.dialect.MySQLDialect.class;
		
		SplitAdapter hibernateRepo = (SplitAdapter) HibernateSyncAdapterFactory.createHibernateAdapter(connectionURL, user,
				password, driverClass, dialectClass, tableName, syncTableName,
				rdfURL, TestHelper.baseDirectoryRootForTest());
		
		return hibernateRepo;	
	}

	@Override
	protected String getUserName(Item item) {
		return item.getContent().getPayload().element("name").getText();
	}

	@Test
	@Override
	public void ShouldSynchronizeSince(){
		Item a = createItem("fizz", TestHelper.newID(), new History("kzu", TestHelper.nowSubtractDays(1)));
		Item b = createItem("buzz", TestHelper.newID(), new History("vga", TestHelper.nowSubtractDays(1)));

		ISyncAdapter left = this.makeLeftRepository(a);
		ISyncAdapter right = this.makeRightRepository(b);

		SyncEngine engine = new SyncEngine(left, right);

		List<Item> conflicts = engine.synchronize(TestHelper.now());

		Assert.assertEquals(0, conflicts.size());
		Assert.assertEquals(1, left.getAll().size());
		Assert.assertEquals(1, right.getAll().size());
	}
	
	private Item createItem(String title, String id, History history) {
		return createItem(title, id, history, new History[0]);
	}

	private Item createItem(String title, String id, History history,
			History[] otherHistory) {
		
		Element e = TestHelper.makeElement("<payload><user><id>"+id+"</id><name>"+title+"</name><pass>123</pass></user></payload>");
		XMLContent xml = new XMLContent(TestHelper.newID(), title, null, e);
		Sync sync = new Sync(id, history.getBy(), history.getWhen(),
				false);
		for (History h : otherHistory) {
			sync.update(h.getBy(), h.getWhen());
		}

		return new Item(xml, sync);
	}
	
	
}
