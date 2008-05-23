package com.mesh4j.sync.adapters.kml;

import java.io.File;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.test.utils.TestHelper;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.MeshException;

public class KMLDOMLoaderTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullFileName(){
		new KMLDOMLoader(null, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpEmptyFileName(){
		new KMLDOMLoader("", NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpInvalidExtension(){
		new KMLDOMLoader("a.kmz", NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullIdentityProvider(){
		new KMLDOMLoader("a.kml", null, KMLMeshDOMLoaderFactory.getDefaultXMLView());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullXMLView(){
		new KMLDOMLoader("a.kml", NullIdentityProvider.INSTANCE, null);
	}
	
	@Test(expected=MeshException.class)
	public void shouldReadThrowsExceptionBecauseFileHasInvalidContent(){
		String fileName = this.getClass().getResource("templateWithInvalidXML.kml").getFile();
		 
		KMLDOMLoader loader = new KMLDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		loader.read();
	}
	
	@Test
	public void shouldReadDoNotCreateFile(){
		String fileName = TestHelper.fileName(IdGenerator.newID()+".kml");
		 
		KMLDOMLoader loader = new KMLDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		loader.read();
		Assert.assertNotNull(loader.getDocument());
		
		File file = new File(fileName);
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void shouldRead() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document xmlns:mesh4x=\"http://mesh4x.org/kml\">"+
		"<name>dummy</name>"+
	   	"<ExtendedData>"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"2096103467\">"+
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
		
		File file = TestHelper.makeNewXMLFile(xml, ".kml");
		Assert.assertTrue(file.exists());
		
		KMLDOMLoader loader = new KMLDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		loader.read();
		
		Assert.assertNotNull(loader.getDocument());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertEquals(doc.asXML(), loader.getDocument().asXML());
	}

	@Test
	public void shouldReadDocWithExternalChanges() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
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
		
		File file = TestHelper.makeNewXMLFile(xml, ".kml");
		Assert.assertTrue(file.exists());
		
		KMLDOMLoader loader = new KMLDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		loader.read();
		
		Assert.assertNotNull(loader.getDocument());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertFalse(doc.asXML().equals(loader.getDocument().asXML()));
	}
	
	@Test
	public void shouldWriteCreateFile() throws DocumentException{

		String fileName = TestHelper.fileName(IdGenerator.newID()+".kml");
		File file = new File(fileName);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>"
					+file.getName()+"</name><ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\"></ExtendedData></Document></kml>";
				 
		KMLDOMLoader loader = new KMLDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		loader.read();
		Assert.assertNotNull(loader.getDocument());
		
		Assert.assertFalse(file.exists());
		
		loader.write();
		Assert.assertTrue(file.exists());
		
		Assert.assertNotNull(loader.getDocument());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertEquals(doc.asXML(), loader.getDocument().asXML());		
	}
	
	@Test
	public void shouldWrite() throws DocumentException{
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
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
		
		File file = TestHelper.makeNewXMLFile(xml, ".kml");
		Assert.assertTrue(file.exists());
		
		KMLDOMLoader loader = new KMLDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
		loader.read();
		
		Assert.assertNotNull(loader.getDocument());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertFalse(doc.asXML().equals(loader.getDocument().asXML()));
		
		doc = XMLHelper.readDocument(file);
		doc.normalize();
		Assert.assertFalse(doc.asXML().equals(loader.getDocument().asXML()));
		
		loader.write();
		
		doc = XMLHelper.readDocument(file);
		doc.normalize();
		Assert.assertTrue(doc.asXML().equals(loader.getDocument().asXML()));

	}


}
