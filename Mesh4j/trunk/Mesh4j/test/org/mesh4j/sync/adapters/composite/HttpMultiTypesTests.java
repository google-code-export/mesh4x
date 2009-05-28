package org.mesh4j.sync.adapters.composite;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class HttpMultiTypesTests {

	@Test
	public void shouldSyncMultiTypes() throws Exception{
		
		String baseURL = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "compositeHTTPMSExcel";
		
		// ms excel creation - multi sheet adapter
		String excelFileName = MsExcelMultiSheetsTests.createMsExcelFile("composite_HTTP_MsExcel.xlsx");
		
		Map<String, String> sheets = new HashMap<String, String>();
		sheets.put("sheet1", "Code");
		sheets.put("sheet2", "Code");
		sheets.put("sheet3", "Code");
		
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		MsExcelRDFSyncAdapterFactory factory = new MsExcelRDFSyncAdapterFactory(baseURL);
		CompositeSyncAdapter msExcelMultiSheetsAdapter = factory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, opaqueAdapter);
		
		// create mesh group
		HttpSyncAdapter.uploadMeshDefinition(baseURL, meshGroup, RssSyndicationFormat.NAME, "my mesh", null, null, "jmt");
		
		// create mesh data sets
		for (IIdentifiableSyncAdapter identifiableAdapter : msExcelMultiSheetsAdapter.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			MsExcelContentAdapter contentAdapter = (MsExcelContentAdapter)splitAdapter.getContentAdapter();
			
			IRDFSchema rdfSchema = (IRDFSchema)contentAdapter.getSchema();
			String feedName = contentAdapter.getType();
			
			HttpSyncAdapter.uploadMeshDefinition(baseURL, meshGroup + "/" + feedName, RssSyndicationFormat.NAME, "my description", rdfSchema, null, "jmt");	
		}
				
		// create http sync adapter
		String url = HttpSyncAdapter.makeMeshGroupURLToSync(baseURL + "/" + meshGroup);
		HttpSyncAdapter httpSyncAdapter = HttpSyncAdapterFactory.createSyncAdapter(url, NullIdentityProvider.INSTANCE);
				
		// sync
		SyncEngine syncEngine = new SyncEngine(httpSyncAdapter, msExcelMultiSheetsAdapter);
		TestHelper.assertSync(syncEngine);
		
		// asserts
		Assert.assertEquals(0, opaqueAdapter.getAll().size());

	}

}
