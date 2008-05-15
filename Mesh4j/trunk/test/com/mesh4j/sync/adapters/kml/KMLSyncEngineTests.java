package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.SyncEngine;
import com.mesh4j.sync.adapters.compound.CompoundRepositoryAdapter;
import com.mesh4j.sync.adapters.file.FileSyncRepository;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;

public class KMLSyncEngineTests {

	@Test
	public void shouldSyncKMLFilesAddStyle(){
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Style id=\"sn_ylw-pushpin\">"+
		"		<IconStyle>"+
		"			<color>ff00ff55</color>"+
		"			<scale>1.1</scale>"+
		"			<Icon>"+
		"				<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
		"			</Icon>"+
		"			<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
		"		</IconStyle>"+
		"		<LabelStyle>"+
		"			<color>ff00ff55</color>"+
		"		</LabelStyle>"+
		"	</Style>"+
		"</Document>"+
		"</kml>";
		
		String xml2 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"</Document>"+
		"</kml>";
		
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml2);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(0, contents.size());

		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		String xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("sn_ylw-pushpin_"+xmlID, items.get(0).getContent().getPayload().attributeValue("id"));
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("sn_ylw-pushpin_"+xmlID, items.get(0).getContent().getPayload().attributeValue("id"));
	}
	
	@Test
	public void shouldSyncKMLFilesAddStyleMap(){
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<StyleMap id=\"msn_ylw-pushpin\">"+
		"		<Pair>"+
		"			<key>normal</key>"+
		"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"		<Pair>"+
		"			<key>highlight</key>"+
		"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"	</StyleMap>"+	
		"</Document>"+
		"</kml>";
		
		String xml2 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"</Document>"+
		"</kml>";
		
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml2);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(0, contents.size());

		// SYNC
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		String xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID, items.get(0).getContent().getPayload().attributeValue("id"));
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("msn_ylw-pushpin_"+xmlID, items.get(0).getContent().getPayload().attributeValue("id"));
	}

	@Test
	public void shouldSyncKMLFilesAddFolder(){
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Folder>"+
		"		<name>myFolder</name>"+
		"	</Folder>"+	
		"</Document>"+
		"</kml>";
		
		String xml2 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"</Document>"+
		"</kml>";
		
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml2);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(0, contents.size());

		// SYNC
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		String xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("myFolder", items.get(0).getContent().getPayload().element("name").getText());
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("myFolder", items.get(0).getContent().getPayload().element("name").getText());
	}
	
	@Test
	public void shouldSyncKMLFilesAddPlacemark(){
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Placemark>"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"</Document>"+
		"</kml>";
		
		String xml2 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"</Document>"+
		"</kml>";
		
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml2);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(0, contents.size());

		// SYNC
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		String xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("a", items.get(0).getContent().getPayload().element("name").getText());
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		
		xmlID = items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID);
		Assert.assertTrue(xmlID1.equals(xmlID));
		Assert.assertEquals("a", items.get(0).getContent().getPayload().element("name").getText());
	}

	@Test
	public void shouldSyncKMLFilesDeleteStyle(){
		
		String id = IdGenerator.newID();
		
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Style id=\"sn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
		"		<IconStyle>"+
		"			<color>ff00ff55</color>"+
		"			<scale>1.1</scale>"+
		"			<Icon>"+
		"				<href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href>"+
		"			</Icon>"+
		"			<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>"+
		"		</IconStyle>"+
		"		<LabelStyle>"+
		"			<color>ff00ff55</color>"+
		"		</LabelStyle>"+
		"	</Style>"+
		"</Document>"+
		"</kml>";
				
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml1);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID2 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Sync
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		xmlID2 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Delete style from repo1
		Item item = repo1.get(id);
		Assert.assertNotNull(item);
		
		kmlAdapter1.delete((KMLContent)item.getContent());   // Emulate a file change from Google Earth, the sync file is not updated, repo1 must be update the sync file during the next sync process. 
		Assert.assertEquals(0, kmlAdapter1.getAll().size());

		// Sync
		conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).getSync().isDeleted());
		Assert.assertTrue(items.get(0).getContent() instanceof NullContent);
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).getSync().isDeleted());
		Assert.assertTrue(items.get(0).getContent() instanceof NullContent);
	}
	
	@Test
	public void shouldSyncKMLFilesDeleteStyleMap(){
		String id = IdGenerator.newID();
		
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<StyleMap id=\"msn_ylw-pushpin_"+id+"\" xml:id=\""+id+"\">"+
		"		<Pair>"+
		"			<key>normal</key>"+
		"			<styleUrl>#sn_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"		<Pair>"+
		"			<key>highlight</key>"+
		"			<styleUrl>#sh_ylw-pushpin</styleUrl>"+
		"		</Pair>"+
		"	</StyleMap>"+		
		"</Document>"+
		"</kml>";
				
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml1);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		String xmlID2 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Sync
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(1, contents.size());
		xmlID2 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Delete style from repo1
		Item item = repo1.get(id);
		Assert.assertNotNull(item);
		
		kmlAdapter1.delete((KMLContent)item.getContent());   // Emulate a file change from Google Earth, the sync file is not updated, repo1 must be update the sync file during the next sync process. 
		Assert.assertEquals(0, kmlAdapter1.getAll().size());

		// Sync
		conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).getSync().isDeleted());
		Assert.assertTrue(items.get(0).getContent() instanceof NullContent);
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(items.get(0).getSync().isDeleted());
		Assert.assertTrue(items.get(0).getContent() instanceof NullContent);
	}

	@Test
	public void shouldSyncKMLFilesDeleteFolder(){
		String id = IdGenerator.newID();
		
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Folder xml:id=\""+ id +"\">"+
		"	<name>Folder1</name>"+
		"	<Placemark xml:id=\""+IdGenerator.newID()+"\">"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+	
		"	</Folder>"+	
		"</Document>"+
		"</kml>";
				
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		String xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml1);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		String xmlID2 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Sync
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		xmlID1 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		xmlID2 = contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Delete style from repo1
		Item item = repo1.get(id);
		Assert.assertNotNull(item);
		
		kmlAdapter1.delete((KMLContent)item.getContent());   // Emulate a file change from Google Earth, the sync file is not updated, repo1 must be update the sync file during the next sync process. 
		Assert.assertEquals(0, kmlAdapter1.getAll().size());

		// Sync
		conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertTrue(items.get(0).getSync().isDeleted());
		Assert.assertTrue(items.get(0).getContent() instanceof NullContent);
		Assert.assertTrue(items.get(1).getSync().isDeleted());
		Assert.assertTrue(items.get(1).getContent() instanceof NullContent);
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertTrue(items.get(0).getSync().isDeleted());
		Assert.assertTrue(items.get(0).getContent() instanceof NullContent);
		Assert.assertTrue(items.get(1).getSync().isDeleted());
		Assert.assertTrue(items.get(1).getContent() instanceof NullContent);
	}
	
	@Test
	public void shouldSyncKMLFilesDeletePlacemark(){
		String id = IdGenerator.newID();
		
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Folder xml:id=\"IDFOLDER\">"+
		"	<name>Folder1</name>"+
		"	<Placemark xml:id=\""+id+"\">"+
		"		<name>a</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+	
		"	</Folder>"+	
		"</Document>"+
		"</kml>";
				
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		String xmlID1 = contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		File file2 = TestHelper.makeNewXMLFile(xml1);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		String xmlID2 = contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Sync
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		xmlID1 = contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID1);
		
		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(2, contents.size());
		xmlID2 = contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME);
		Assert.assertNotNull(xmlID2);
		
		Assert.assertEquals(id, xmlID1);
		Assert.assertEquals(id, xmlID2);
		
		// Delete style from repo1
		Item item = repo1.get(id);
		Assert.assertNotNull(item);
		
		kmlAdapter1.delete((KMLContent)item.getContent());   // Emulate a file change from Google Earth, the sync file is not updated, repo1 must be update the sync file during the next sync process. 
		Assert.assertEquals(1, kmlAdapter1.getAll().size());

		// Sync
		conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertFalse(items.get(0).getSync().isDeleted());
		Assert.assertEquals("Folder", items.get(0).getContent().getPayload().getName());
		Assert.assertTrue(items.get(1).getSync().isDeleted());
		Assert.assertTrue(items.get(1).getContent() instanceof NullContent);
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(2, items.size());
		Assert.assertFalse(items.get(0).getSync().isDeleted());
		Assert.assertEquals("Folder", items.get(0).getContent().getPayload().getName());
		Assert.assertTrue(items.get(1).getSync().isDeleted());
		Assert.assertTrue(items.get(1).getContent() instanceof NullContent);
	}
	
	@Test
	public void shouldSyncKMLFilesMovePlacemark() throws DocumentException{
		String id = IdGenerator.newID();
		String folderID1 = IdGenerator.newID();
		String folderID2 = IdGenerator.newID();
		
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" >"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Folder xml:id=\""+folderID1+"\">"+
		"	<name>Folder1</name>"+
			"	<Placemark xlink:href=\""+folderID1+"\" xml:id=\""+id+"\">"+
			"		<name>a</name>"+
			"		<visibility>0</visibility>"+
			"		<LookAt>"+
			"			<longitude>-86.29603105340947</longitude>"+
			"			<latitude>36.94722720798701</latitude>"+
			"			<altitude>0</altitude>"+
			"			<range>15133106.44824071</range>"+
			"			<tilt>0</tilt>"+
			"			<heading>1.090894378010394</heading>"+
			"		</LookAt>"+
			"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
			"		<Point>"+
			"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
			"		</Point>"+
			"	</Placemark>"+	
		"	</Folder>"+
		"	<Folder xml:id=\""+folderID2+"\">"+
		"	<name>Folder1</name>"+
		"	</Folder>"+
		"</Document>"+
		"</kml>";
				
		File file1 = TestHelper.makeNewXMLFile(xml1);
		File fileSync1 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		FileSyncRepository syncRepo1 = new FileSyncRepository(fileSync1, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo1 = new CompoundRepositoryAdapter(syncRepo1, kmlAdapter1, NullSecurity.INSTANCE);
		
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(3, contents.size());
		Assert.assertEquals(folderID1, contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, contents.get(2).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID1, contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(contents.get(2).getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
			
		File file2 = TestHelper.makeNewXMLFile(xml1);		
		KMLContentAdapter kmlAdapter2 = new KMLContentAdapter(file2);
		File fileSync2 = new File(TestHelper.fileName(IdGenerator.newID()+".xml"));
		FileSyncRepository syncRepo2 = new FileSyncRepository(fileSync2, NullSecurity.INSTANCE);
		CompoundRepositoryAdapter repo2 = new CompoundRepositoryAdapter(syncRepo2, kmlAdapter2, NullSecurity.INSTANCE);

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(3, contents.size());
		Assert.assertEquals(folderID1, contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, contents.get(2).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID1, contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(contents.get(2).getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
					
		// Move placemark from repo1
		KMLContent content = kmlAdapter1.get(id);
		Assert.assertNotNull(content);

		String newXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" >"+
		"<Document>"+
		"<name>a.kml</name>"+
		"	<Placemark xlink:href=\""+folderID2+"\" xml:id=\""+id+"\">"+
		"		<name>zz</name>"+
		"		<visibility>0</visibility>"+
		"		<LookAt>"+
		"			<longitude>-86.29603105340947</longitude>"+
		"			<latitude>36.94722720798701</latitude>"+
		"			<altitude>0</altitude>"+
		"			<range>15133106.44824071</range>"+
		"			<tilt>0</tilt>"+
		"			<heading>1.090894378010394</heading>"+
		"		</LookAt>"+
		"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"		<Point>"+
		"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"		</Point>"+
		"	</Placemark>"+
		"</Document>"+
		"</kml>";

		Element newElement = DocumentHelper.parseText(newXML).getRootElement().element("Document").element("Placemark");				
		content = new KMLContent(newElement, id);
		kmlAdapter1.save(content);   // Emulate a file change from Google Earth, the sync file is not updated, repo1 must be update the sync file during the next sync process.
		
		contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(3, contents.size());
		Assert.assertEquals(folderID1, contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, contents.get(2).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(contents.get(2).getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));

		contents = kmlAdapter2.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(3, contents.size());
		Assert.assertEquals(folderID1, contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, contents.get(2).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID1, contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(contents.get(2).getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));

		
		// Sync
		SyncEngine syncEngine = new SyncEngine(repo1, repo2);
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertTrue(conflicts.isEmpty());
		
		List<Item> items = repo1.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());
		Assert.assertEquals(folderID1, items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, items.get(1).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, items.get(2).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, items.get(2).getContent().getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(items.get(2).getContent().getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), items.get(2).getContent().getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		
		items = repo2.getAll();
		Assert.assertNotNull(items);
		Assert.assertEquals(3, items.size());
		Assert.assertEquals(folderID1, items.get(0).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, items.get(1).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, items.get(2).getContent().getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, items.get(2).getContent().getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(items.get(2).getContent().getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), items.get(2).getContent().getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
	}
	
	@Test
	public void shouldSyncKMLFilesMovePlacemarkOutside() throws DocumentException{
		String id = IdGenerator.newID();
		String folderID1 = IdGenerator.newID();
		String folderID2 = IdGenerator.newID();
		
		String xml1 ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>a.kml</name>"+
		"	<Folder xml:id=\""+folderID1+"\">"+
		"	<name>Folder1</name>"+
			"	<Placemark xml:parent=\""+folderID2+"\" xml:id=\""+id+"\">"+
			"		<name>a</name>"+
			"		<visibility>0</visibility>"+
			"		<LookAt>"+
			"			<longitude>-86.29603105340947</longitude>"+
			"			<latitude>36.94722720798701</latitude>"+
			"			<altitude>0</altitude>"+
			"			<range>15133106.44824071</range>"+
			"			<tilt>0</tilt>"+
			"			<heading>1.090894378010394</heading>"+
			"		</LookAt>"+
			"		<styleUrl>#msn_ylw-pushpin</styleUrl>"+
			"		<Point>"+
			"			<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
			"		</Point>"+
			"	</Placemark>"+	
		"	</Folder>"+
		"	<Folder xml:id=\""+folderID2+"\">"+
		"	<name>Folder1</name>"+
		"	</Folder>"+
		"</Document>"+
		"</kml>";

		File file1 = TestHelper.makeNewXMLFile(xml1);
		KMLContentAdapter kmlAdapter1 = new KMLContentAdapter(file1);
		List<IContent> contents = kmlAdapter1.getAll();
		Assert.assertNotNull(contents);
		Assert.assertEquals(3, contents.size());
		Assert.assertEquals(folderID1, contents.get(0).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID2, contents.get(1).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(id, contents.get(2).getPayload().attributeValue(KmlNames.XML_ID_QNAME));
		Assert.assertEquals(folderID1, contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
		Assert.assertEquals(contents.get(2).getPayload().getParent().attributeValue(KmlNames.XML_ID_QNAME), contents.get(2).getPayload().attributeValue(KmlNames.PARENT_ID_QNAME));
	}
}