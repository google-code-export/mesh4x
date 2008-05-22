package com.mesh4j.sync.utils;

import java.io.File;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.kml.KmlNames;
import com.mesh4j.sync.test.utils.TestHelper;

public class ZipUtilsTests {

	@Test(expected=IOException.class)
	public void shouldReadKMZFromFileNameThrowsExceptionBecauseFileDoesNotExist() throws IOException{
		ZipUtils.getTextEntryContent("thisIsAnInvalidFileName.kmz", "ThisIsABadEntry");
	}
	
	@Test(expected=IOException.class)
	public void shouldReadKMZFromFileThrowsExceptionBecauseFileDoesNotExist() throws IOException{
		ZipUtils.getTextEntryContent(new File("thisIsAnInvalidFileName.kmz"), "ThisIsABadEntry");
	}	
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReadKMZFromFileNameThrowsExceptionBecauseEntryDoesnNotExist() throws IOException{
		String kmzFileName = this.getClass().getResource("exampleGoogleEarhFile.kmz").getFile();
		ZipUtils.getTextEntryContent(kmzFileName, "ThisIsABadEntry");
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldReadKMZFromFileThrowsExceptionBecauseEntryDoesnNotExist() throws IOException{
		String kmzFileName = this.getClass().getResource("exampleGoogleEarhFile.kmz").getFile();
		ZipUtils.getTextEntryContent(new File(kmzFileName), "ThisIsABadEntry");
	}
	
	@Test
	public void shouldReadKMZFromFileName() throws IOException, DocumentException{
		String kmzFileName = this.getClass().getResource("exampleGoogleEarhFile.kmz").getFile();
		String xml = ZipUtils.getTextEntryContent(kmzFileName, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		
		Assert.assertNotNull(xml);
		
		Document docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		String kmlFileName = this.getClass().getResource("exampleGoogleEarhFile.kml").getFile();
		Document docKml = XMLHelper.readDocument(new File(kmlFileName));
		docKml.normalize();
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());
	}

	@Test
	public void shouldReadKMZFromFile() throws IOException, DocumentException{
		String kmzFileName = this.getClass().getResource("exampleGoogleEarhFile.kmz").getFile();
		File file = new File(kmzFileName);
		String xml = ZipUtils.getTextEntryContent(file, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		
		Assert.assertNotNull(xml);
		
		Document docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		String kmlFileName = this.getClass().getResource("exampleGoogleEarhFile.kml").getFile();
		Document docKml = XMLHelper.readDocument(new File(kmlFileName));
		docKml.normalize();
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());
	}
	
	@Test
	public void shouldCreateKMZFile() throws IOException, DocumentException{
		String kmlFileName = this.getClass().getResource("exampleGoogleEarhFile.kml").getFile();
		Document docKml = XMLHelper.readDocument(new File(kmlFileName));
		docKml.normalize();
		
		String fileName = TestHelper.fileName(IdGenerator.newID()+".kmz");
		ZipUtils.write(new File(fileName), KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, docKml.asXML());

		String xml = ZipUtils.getTextEntryContent(fileName, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		Assert.assertNotNull(xml);
		
		Document docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());
	}
	
	@Test
	public void shouldUpdateKMZFile() throws IOException, DocumentException{
		String kmlFileName = this.getClass().getResource("exampleGoogleEarhFile.kml").getFile();
		Document docKml = XMLHelper.readDocument(new File(kmlFileName));
		docKml.normalize();
		
		String fileName = TestHelper.fileName(IdGenerator.newID()+".kmz");
		ZipUtils.write(new File(fileName), KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, docKml.asXML());

		String xml = ZipUtils.getTextEntryContent(fileName, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		Assert.assertNotNull(xml);
		
		Document docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());
		
		
		String newXml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
		   	"<ExtendedData>"+
			"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
	      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
	      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
	      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
	      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
	     	"</sx:sync>"+
			"</mesh4x:sync>"+
	      	"</ExtendedData>"+
			"<Placemark mesh4x:id=\"1\">"+
			"<name>B</name>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>";
		ZipUtils.write(new File(fileName), KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, newXml);
		
		xml = ZipUtils.getTextEntryContent(fileName, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		Assert.assertNotNull(xml);
		
		docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		docKml = DocumentHelper.parseText(newXml);
		docKml.normalize();
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());
	}
	
	@Test
	public void shouldUpdateAddEntry() throws IOException, DocumentException{
		String kmlFileName = this.getClass().getResource("exampleGoogleEarhFile.kml").getFile();
		Document docKml = XMLHelper.readDocument(new File(kmlFileName));
		docKml.normalize();
		
		String fileName = TestHelper.fileName(IdGenerator.newID()+".kmz");
		ZipUtils.write(new File(fileName), KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML, docKml.asXML());

		String xml = ZipUtils.getTextEntryContent(fileName, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		Assert.assertNotNull(xml);
		
		Document docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());
		
		
		String newXml = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
			"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
			"<name>dummy</name>"+
		   	"<ExtendedData>"+
			"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1\">"+
	      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
	      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
	      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
	      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
	     	"</sx:sync>"+
			"</mesh4x:sync>"+
	      	"</ExtendedData>"+
			"<Placemark mesh4x:id=\"1\">"+
			"<name>B</name>"+
			"</Placemark>"+
			"</Document>"+
			"</kml>";
		ZipUtils.write(new File(fileName), "MyFile", newXml);
		
		String newMyXML = ZipUtils.getTextEntryContent(fileName, "MyFile");
		Assert.assertNotNull(newMyXML);
		
		Document myDocKmlFromKmz = DocumentHelper.parseText(newMyXML);
		myDocKmlFromKmz.normalize();
		
		Document myDocKml = DocumentHelper.parseText(newMyXML);
		myDocKml.normalize();
		
		Assert.assertEquals(myDocKml.asXML(), myDocKmlFromKmz.asXML());
		
		xml = ZipUtils.getTextEntryContent(fileName, KmlNames.KMZ_DEFAULT_ENTRY_NAME_TO_KML);
		Assert.assertNotNull(xml);
		
		docKmlFromKmz = DocumentHelper.parseText(xml);
		docKmlFromKmz.normalize();
		
		Assert.assertEquals(docKml.asXML(), docKmlFromKmz.asXML());

	}
}
