package com.mesh4j.sync.adapters.kml;

import java.io.File;

import org.dom4j.DocumentException;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.compound.CompoundRepositoryAdapter;
import com.mesh4j.sync.adapters.file.FileSyncRepository;
import com.mesh4j.sync.security.NullSecurity;

public class KMLSyncEngineTests {

	@Test
	public void spike() throws Exception{
		File kmlFile1 = new File("D:\\temp_dev\\files\\kml\\samples1.kml");
		File syncFile1 = new File("D:\\temp_dev\\files\\kml\\samples1_sync.xml");
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(kmlFile1);
		FileSyncRepository syncFileRepo1 = new FileSyncRepository(syncFile1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter source = new CompoundRepositoryAdapter(syncFileRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		
		File kmlFile2 = new File("D:\\temp_dev\\files\\kml\\samples2.kml");
		File syncFile2 = new File("D:\\temp_dev\\files\\kml\\samples2_sync.xml");
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(kmlFile2);
		FileSyncRepository syncFileRepo2 = new FileSyncRepository(syncFile2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter target = new CompoundRepositoryAdapter(syncFileRepo2, kmlAdapter2, NullSecurity.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		syncEngine.synchronize();
	}
}
