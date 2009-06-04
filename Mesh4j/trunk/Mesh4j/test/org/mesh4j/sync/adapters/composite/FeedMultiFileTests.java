package org.mesh4j.sync.adapters.composite;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.FeedSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class FeedMultiFileTests {

	@Test
	public void shouldSyncAllFeedFiles() throws Exception{
		
		// Composite adapter
		InMemorySyncAdapter adapterOpaque = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		
		Map<String, String> feeds = new HashMap<String, String>();
		feeds.put("sheet1", TestHelper.makeFileAndDeleteIfExists("composite_Feed_MsExcel_sheet1.xml").getCanonicalPath());
		feeds.put("sheet2", TestHelper.makeFileAndDeleteIfExists("composite_Feed_MsExcel_sheet2.xml").getCanonicalPath());
		feeds.put("sheet3", TestHelper.makeFileAndDeleteIfExists("composite_Feed_MsExcel_sheet3.xml").getCanonicalPath());
		
		ISyncAdapter adapterSource = FeedSyncAdapterFactory.createSyncAdapterForMultiFiles(feeds, NullIdentityProvider.INSTANCE, adapterOpaque);
		
		// Sync example
		String excelFileName = MsExcelMultiSheetsTests.createMsExcelFile("composite_Feed_MsExcel.xlsx");
		
		InMemorySyncAdapter adapterOpaqueTarget = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory("http://localhost:8080/mesh4x/feeds");
		
		Map<String, String> sheets = new HashMap<String, String>();
		sheets.put("sheet1", "Code");
		sheets.put("sheet2", "Code");
		sheets.put("sheet3", "Code");
		
		ISyncAdapter adapterTarget = factory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, adapterOpaqueTarget);
				
		SyncEngine syncEngine = new SyncEngine(adapterSource, adapterTarget);
		TestHelper.assertSync(syncEngine);
		
		Assert.assertEquals(0, adapterOpaque.getAll().size());
		Assert.assertEquals(0, adapterOpaqueTarget.getAll().size());
	}
	
}
