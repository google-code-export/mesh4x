package org.mesh4j.sync.adapters.hibernate.msaccess;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.composite.CompositeSyncAdapter;
import org.mesh4j.sync.adapters.composite.IdentifiableSyncAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.feed.zip.ZipFeedsSyncAdapter;
import org.mesh4j.sync.adapters.hibernate.HibernateContentAdapter;
import org.mesh4j.sync.adapters.hibernate.mapping.HibernateMsAccessToRDFMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.MeshException;

public class HibernateWithMsAccessMultiTablesWithRelationshipTests {

	@Test
	public void shouldSyncTable1Table2(){
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("mytable1");
		tables.add("mytable2");
		
		baseTest(tables);
	}
	
	@Test
	public void shouldSyncTable2Table1(){
		TreeSet<String> tables = new TreeSet<String>();
		tables.add("mytable2");
		tables.add("mytable1");
		
		baseTest(tables);
	}
	
	private void baseTest(TreeSet<String> tables){		

		String mdbFileName = getMsAccessFileNameToTest();
		
		// zip adapter
		String zipFileName = mdbFileName.substring(0, mdbFileName.length() -4)+".zip";
		ZipFeedsSyncAdapter source = FeedSyncAdapterFactory.createSyncAdapterForMultiFilesAsZip(zipFileName, NullIdentityProvider.INSTANCE, TestHelper.baseDirectoryForTest());
		
		// excel
		InMemorySyncAdapter targetAdapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter target = MsAccessHibernateSyncAdapterFactory.createSyncAdapterForMultiTables(mdbFileName, tables, "http://mesh4x/test", TestHelper.baseDirectoryRootForTest(), NullIdentityProvider.INSTANCE, targetAdapterOpaque);
						
		// sync
		SyncEngine syncEngine = new SyncEngine(source, target);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());
	
		TestHelper.assertSync(syncEngine);
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());
		
		// add item mytable1
		source.beginSync();
		IdentifiableSyncAdapter adapter1 = (IdentifiableSyncAdapter)target.getAdapter("mytable1");
		HibernateMsAccessToRDFMapping mapping1 = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)((SplitAdapter)adapter1.getSyncAdapter()).getContentAdapter()).getMapping();
		IRDFSchema rdfSchema1 = mapping1.getSchema();
		
		String id1 = IdGenerator.INSTANCE.newID();
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put("ID", id1);
		properties.put("name", id1);
		RDFInstance instance = rdfSchema1.createNewInstanceFromProperties(id1, properties);
		
		IdentifiableContent identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping1, id1);
		Item item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		source.add(item);	

		
		// add item mytable2
		IdentifiableSyncAdapter adapter2 = (IdentifiableSyncAdapter)target.getAdapter("mytable2");
		HibernateMsAccessToRDFMapping mapping2 = (HibernateMsAccessToRDFMapping)((HibernateContentAdapter)((SplitAdapter)adapter2.getSyncAdapter()).getContentAdapter()).getMapping();
		IRDFSchema rdfSchema2 = mapping2.getSchema();
		
		String id2 = IdGenerator.INSTANCE.newID();
		properties = new HashMap<String, Object>();
		properties.put("ID", id2);
		properties.put("name", id2);
		properties.put("mytable1", id1);
		instance = rdfSchema2.createNewInstanceFromProperties(id2, properties);
		
		identifiableContent = new IdentifiableContent(instance.asElementRDFXML(), mapping2, id2);
		item = new Item(identifiableContent, new Sync(IdGenerator.INSTANCE.newID(), "jmt", new Date(), false));
		source.add(item);	
		
		source.endSync();
		
		// sync
		TestHelper.assertSync(syncEngine);
		Assert.assertEquals(0, source.getCompositeAdapter().getOpaqueAdapter().getAll().size());
		Assert.assertEquals(0, targetAdapterOpaque.getAll().size());
		
	}
	
	private String getMsAccessFileNameToTest() {
		try{
			String localFileName = this.getClass().getResource("msaccessWithRelationships.mdb").getFile();
			String fileName = TestHelper.fileName("msAccess"+IdGenerator.INSTANCE.newID()+".mdb");
			FileUtils.copyFile(localFileName, fileName);
			return fileName;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
}
