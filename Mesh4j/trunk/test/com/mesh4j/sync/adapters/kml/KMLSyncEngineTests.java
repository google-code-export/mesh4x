package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import junit.framework.Assert;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.compound.CompoundRepositoryAdapter;
import com.mesh4j.sync.adapters.file.FileSyncRepository;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullSecurity;

public class KMLSyncEngineTests {

	@Test
	public void spike() throws Exception{
		File kmlFile1 = new File(this.getClass().getResource("samples1.kml").getFile());
		File syncFile1 = new File(this.getClass().getResource("samples1_sync.xml").getFile());
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(kmlFile1);
		FileSyncRepository syncFileRepo1 = new FileSyncRepository(syncFile1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter source = new CompoundRepositoryAdapter(syncFileRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					 "<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>2</name>"+
					"</Document>"+
					"</kml>";
		
		File kmlFile2 = new File(this.getClass().getResource("samples2.kml").getFile());
		FileWriter fileWriter = new FileWriter(kmlFile2);
		fileWriter.write(xml);
		fileWriter.flush();
		fileWriter.close();
		
		File syncFile2 = new File(this.getClass().getResource("samples2_sync.xml").getFile());
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(kmlFile2);
		FileSyncRepository syncFileRepo2 = new FileSyncRepository(syncFile2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter target = new CompoundRepositoryAdapter(syncFileRepo2, kmlAdapter2, NullSecurity.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(source, target);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		Document doc1 = this.getDocument(kmlFile1);
		
		Document doc2 = this.getDocument(kmlFile2);
	}

	private Document getDocument(File kmlFile) {
		SAXReader saxReader = new SAXReader();
		try {
			return saxReader.read(kmlFile);
		} catch (DocumentException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			return null;
		}
	}
}
