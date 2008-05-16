package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.ArrayList;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.MeshException;

public class KMLAdapterTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullFileName(){
		new KMLAdapter(null, NullSecurity.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptEmptyFileName(){
		new KMLAdapter("", NullSecurity.INSTANCE);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptNullSecurity(){
		new KMLAdapter(getDummyFileName(), null);
	}
	
	@Test	
	public void shouldCreateFileIfDoesNotExist() throws DocumentException{
		String fileName = TestHelper.fileName(IdGenerator.newID()+".kml");
		new KMLAdapter(fileName, NullSecurity.INSTANCE);
		
		File file = new File(fileName);
		Assert.assertTrue(file.exists());
		
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);		
		Assert.assertNotNull(document);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
					"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
					"<Document>"+
					"<name>"+fileName+"</name>"+
					"</Document>"+
					"</kml>";
		
		Assert.assertEquals(xml, document.asXML());
	}

	@Test(expected=MeshException.class)
	public void shouldThrowsExceptionBecauseFileHasNotElements(){
		new KMLAdapter(getFileWithOutElements(), NullSecurity.INSTANCE);
	}
	
	@Test(expected=MeshException.class)
	public void shouldThrowsExceptionIfFileExistButHasNotKMLElement(){
		new KMLAdapter(getFileWithOutKMLElement(), NullSecurity.INSTANCE);
	}

	@Test(expected=MeshException.class)
	public void shouldThrowsExceptionIfFileExistHasKMLElementButHasNotDocumentElement(){
		new KMLAdapter(getFileWithOutDocumentElement(), NullSecurity.INSTANCE);
	}
		
	@Test
	public void shouldReturnsAuthenticatedUser(){
		KMLAdapter kmlAdapter = new KMLAdapter(getDummyFileName(), NullSecurity.INSTANCE);
		String user = kmlAdapter.getAuthenticatedUser();
		Assert.assertNotNull(user);
		Assert.assertEquals(user, NullSecurity.INSTANCE.getAuthenticatedUser());
	}

	@Test
	public void shouldNotSupportMerge(){
		KMLAdapter kmlAdapter = new KMLAdapter(getDummyFileName(), NullSecurity.INSTANCE);
		Assert.assertFalse(kmlAdapter.supportsMerge());	
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void shouldMergeThrowsUnsupportedOperationException(){
		KMLAdapter kmlAdapter = new KMLAdapter(getDummyFileName(), NullSecurity.INSTANCE);
		kmlAdapter.merge(new ArrayList<Item>());
	}
	
	@Test
	public void shouldReturnsFriendlyName(){
		KMLAdapter kmlAdapter = new KMLAdapter(getDummyFileName(), NullSecurity.INSTANCE);
		String name = kmlAdapter.getFriendlyName();
		Assert.assertNotNull(name);
		Assert.assertEquals(name, "KML Adapter");		
	}
	
	@Test
	public void shoulPrepareKMLToSync() throws DocumentException{
		
		String xml ="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"<name>example</name>"+
		"<Folder>"+
		"	<name>Folder1</name>"+
		"	<Placemark>"+
		"		<name>Placemark_A</name>"+
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
		"</Folder>"+
		"<Placemark>"+
		"	<name>Placemark_B</name>"+
		"	<visibility>0</visibility>"+
		"	<LookAt>"+
		"		<longitude>-86.29603105340947</longitude>"+
		"		<latitude>36.94722720798701</latitude>"+
		"		<altitude>0</altitude>"+
		"		<range>15133106.44824071</range>"+
		"		<tilt>0</tilt>"+
		"		<heading>1.090894378010394</heading>"+
		"	</LookAt>"+
		"	<styleUrl>#msn_ylw-pushpin</styleUrl>"+
		"	<Point>"+
		"		<coordinates>-86.29603105340947,36.94722720798701,0</coordinates>"+
		"	</Point>"+
		"</Placemark>"+			
		"<Folder>"+
		"	<name>Folder2</name>"+
		"</Folder>"+
		"</Document>"+
		"</kml>";

		File file = TestHelper.makeNewXMLFile(xml);		
		
		KMLAdapter kmlAdapter = new KMLAdapter(file.getAbsolutePath(), NullSecurity.INSTANCE);
		
		Document document = XMLHelper.readDocument(file);
		String newXML = document.asXML();
		// TODO (JMT) test
		
	}

	private String getDummyFileName() {
		return this.getClass().getResource("dummy.kml").getFile();
	}

	
	private String getFileWithOutKMLElement() {
		return this.getClass().getResource("templateWithOutKMLElement.kml").getFile();
	}
	
	private String getFileWithOutDocumentElement() {
		return this.getClass().getResource("templateWithOutDocumentElement.kml").getFile();
	}
	
	private String getFileWithOutElements() {
		return this.getClass().getResource("templateWithOutElements.kml").getFile();
	}
}
