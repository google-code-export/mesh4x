package org.mesh4j.sync.adapters.composite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.InMemorySyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.msexcel.MsExcelContentAdapter;
import org.mesh4j.sync.adapters.msexcel.MsExcelRDFSyncAdapterFactory;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class HttpMultiTypesTests {

	@Test
	public void shouldSyncMultiTypes() throws Exception{
		
		String baseURL = "http://localhost:8080/mesh4x/feeds";
		String meshGroup = "HTTPMSExcel"+IdGenerator.INSTANCE.newID().substring(0, 5);
		
		// ms excel creation - multi sheet adapter
		String excelFileName = MsExcelMultiSheetsTests.createMsExcelFile(meshGroup+".xlsx");
		
		Map<String, String[]> sheets = new HashMap<String, String[]>();
		sheets.put("sheet1", new String[]{"Code"});
		sheets.put("sheet2", new String[]{"Code"});
		sheets.put("sheet3", new String[]{"Code"});
		
		InMemorySyncAdapter opaqueAdapter = new InMemorySyncAdapter("opaque", NullIdentityProvider.INSTANCE);
		CompositeSyncAdapter msExcelMultiSheetsAdapter = MsExcelRDFSyncAdapterFactory.createSyncAdapterForMultiSheets(excelFileName, NullIdentityProvider.INSTANCE, sheets, opaqueAdapter, baseURL);
		
		// extract rdfSchemas
		List<ISchema> rdfSchemas = new ArrayList<ISchema>();
		for (IIdentifiableSyncAdapter identifiableAdapter : msExcelMultiSheetsAdapter.getAdapters()) {
			SplitAdapter splitAdapter = (SplitAdapter)((IdentifiableSyncAdapter)identifiableAdapter).getSyncAdapter();
			MsExcelContentAdapter contentAdapter = (MsExcelContentAdapter)splitAdapter.getContentAdapter();
			
			rdfSchemas.add(contentAdapter.getSchema());	
		}
				
		// create http sync adapter
		
		HttpSyncAdapter httpSyncAdapter = HttpSyncAdapterFactory.createSyncAdapterForMultiDataset(baseURL, meshGroup, NullIdentityProvider.INSTANCE, rdfSchemas);
				
		// sync
		SyncEngine syncEngine = new SyncEngine(httpSyncAdapter, msExcelMultiSheetsAdapter);
		TestHelper.assertSync(syncEngine);
		
		// asserts
		Assert.assertEquals(0, opaqueAdapter.getAll().size());

	}

}
