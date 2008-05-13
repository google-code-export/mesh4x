package com.mesh4j.sync.adapters.multi.repositories;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.compound.CompoundRepositoryAdapter;
import com.mesh4j.sync.adapters.feed.FeedAdapter;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.adapters.file.FileSyncRepository;
import com.mesh4j.sync.adapters.kml.KMLContentAdapter;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.XMLHelper;

public class Feed2KMLTets {

	@Test
	public void spike() throws Exception {
		
		File file = makeFileFromTemplate("FeedKML.xml");
		File kmlFile = makeFileFromTemplate("KML.kml");
		
		FeedAdapter feedAdapter =  new FeedAdapter(file, RssSyndicationFormat.INSTANCE, NullSecurity.INSTANCE);
		
		KMLContentAdapter kmlContentAdapter = new KMLContentAdapter(kmlFile);
		FileSyncRepository syncRepo = new FileSyncRepository(TestHelper.fileName("KML_SYNC.xml"), NullSecurity.INSTANCE);
		CompoundRepositoryAdapter kmlAdapter = new CompoundRepositoryAdapter(syncRepo, kmlContentAdapter, NullSecurity.INSTANCE);
		
		SyncEngine syncEngine = new SyncEngine(feedAdapter, kmlAdapter);
		syncEngine.synchronize();		
	}

	private File makeFileFromTemplate(String templateFileName) throws DocumentException {
		File templateFile = new File(this.getClass().getResource(templateFileName).getFile());
		SAXReader saxReader = new SAXReader();
		Document documentTempate = saxReader.read(templateFile);		
		File file = new File(TestHelper.fileName(templateFileName));
		XMLHelper.write(documentTempate, file);
		return file;
	}
}
