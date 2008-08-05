package org.mesh4j.sync.adapters.kml;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.dom.parsers.FileManager;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.Base64Helper;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.utils.IdGenerator;
import org.mesh4j.sync.utils.ZipUtils;
import org.mesh4j.sync.validations.MeshException;


public class KMZDOMLoaderTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullFileName(){
		FileManager fileManager = new FileManager();
		new KMZDOMLoader(null, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpEmptyFileName(){
		FileManager fileManager = new FileManager();
		new KMZDOMLoader("", NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpInvalidExtension(){
		FileManager fileManager = new FileManager();
		new KMZDOMLoader("a.kml", NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullIdentityProvider(){
		FileManager fileManager = new FileManager();
		new KMZDOMLoader("a.kmz", null, KMLDOMLoaderFactory.createView(fileManager), fileManager);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullXMLView(){
		FileManager fileManager = new FileManager();
		new KMZDOMLoader("a.kmz", NullIdentityProvider.INSTANCE, null, fileManager);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAccetpNullFileManager(){
		FileManager fileManager = new FileManager();
		new KMZDOMLoader("a.kmz", NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), null);
	}
	
	@Test(expected=MeshException.class)
	public void shouldReadThrowsExceptionBecauseFileHasInvalidContent(){
		String fileName = this.getClass().getResource("templateWithInvalidXML.kmz").getFile();
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		loader.read();
	}
	
	@Test
	public void shouldReadDoNotCreateFile(){
		String fileName = TestHelper.fileName(IdGenerator.newID()+".kmz");
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		loader.read();
		Assert.assertNotNull(loader.getDOM());
		
		File file = new File(fileName);
		Assert.assertFalse(file.exists());
		Assert.assertEquals(0, fileManager.getFileContents().size());
	}
	
	@Test
	public void shouldRead() throws DocumentException{
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"<name>dummy</name>"+
	   	"<ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\" >"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"1547376435\">"+
      	"<sx:sync id=\"1\" updates=\"3\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"3\" when=\"2005-05-21T11:43:33Z\" by=\"JEO2000\"/>"+
      	"<sx:history sequence=\"2\" when=\"2005-05-21T10:43:33Z\" by=\"REO1750\"/>"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
		"<mesh4x:hierarchy xml:id=\"2\" mesh4x:childId=\"1\" />"+
		"<mesh4x:sync xmlns:sx=\"http://feedsync.org/2007/feedsync\" version=\"233732010\">"+
      	"<sx:sync id=\"2\" updates=\"1\" deleted=\"false\" noconflicts=\"false\">"+
      	"<sx:history sequence=\"1\" when=\"2005-05-21T09:43:33Z\" by=\"REO1750\"/>"+
     	"</sx:sync>"+
		"</mesh4x:sync>"+
      	"</ExtendedData>"+
		"<Placemark xml:id=\"1\">"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
		
		File file = TestHelper.makeNewKMZFile(xml);
		Assert.assertTrue(file.exists());
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		loader.read();
		
		Assert.assertNotNull(loader.getDOM());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertEquals(doc.asXML(), loader.getDOM().asXML());
		Assert.assertEquals(0, fileManager.getFileContents().size());
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
		"<Placemark xml:id=\"1\">"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
		
		File file = TestHelper.makeNewKMZFile(xml);
		Assert.assertTrue(file.exists());
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		loader.read();
		
		Assert.assertNotNull(loader.getDOM());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertFalse(doc.asXML().equals(loader.getDOM().asXML()));
		Assert.assertEquals(0, fileManager.getFileContents().size());
	}
	
	@Test
	public void shouldWriteCreateFile() throws DocumentException{

		String fileName = TestHelper.fileName(IdGenerator.newID()+".kmz");
		File file = new File(fileName);
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.2\"><Document><name>"
					+file.getName()+"</name><ExtendedData xmlns:mesh4x=\"http://mesh4x.org/kml\"></ExtendedData></Document></kml>";
		
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		loader.read();
		Assert.assertNotNull(loader.getDOM());
		
		Assert.assertFalse(file.exists());
		
		loader.write();
		Assert.assertTrue(file.exists());
		
		Assert.assertNotNull(loader.getDOM());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertEquals(doc.asXML(), loader.getDOM().asXML());
		Assert.assertEquals(0, fileManager.getFileContents().size());
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
		"<Placemark xml:id=\"1\">"+
		"<name>B</name>"+
		"</Placemark>"+
		"</Document>"+
		"</kml>";
		
		File file = TestHelper.makeNewKMZFile(xml);
		Assert.assertTrue(file.exists());
		
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		loader.read();
		
		Assert.assertNotNull(loader.getDOM());
		
		Document doc = DocumentHelper.parseText(xml);
		doc.normalize();
		Assert.assertFalse(doc.asXML().equals(loader.getDOM().asXML()));
		
		doc = TestHelper.readKMZDocument(file);
		doc.normalize();
		Assert.assertFalse(doc.asXML().equals(loader.getDOM().asXML()));
		
		loader.write();
		
		doc = TestHelper.readKMZDocument(file);
		doc.normalize();
		Assert.assertTrue(doc.asXML().equals(loader.getDOM().asXML()));
		Assert.assertEquals(0, fileManager.getFileContents().size());
	}

	@Test
	public void shouldReturnFriendlyName(){
		String fileName = this.getClass().getResource("templateWithInvalidXML.kmz").getFile();
		 
		FileManager fileManager = new FileManager();
		KMZDOMLoader loader = new KMZDOMLoader(fileName, NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		
		String name = loader.getFriendlyName();
		Assert.assertNotNull(name);
		Assert.assertTrue(name.trim().length() > 0);
	}
	
	@Test
	public void shouldWriteKMZWithFiles() throws IOException{
		File file = new File(TestHelper.fileName("kmzExample_"+IdGenerator.newID()+".kmz"));
		
		byte[] file2 = FileUtils.read(this.getClass().getResource("kmzExample_star.jpg").getFile());
		byte[] file3 = FileUtils.read(this.getClass().getResource("kmzExample_camera_mode.png").getFile());
		
		FileManager fileManager = new FileManager();
		

		fileManager.setFileContent("files/star.jpg", Base64Helper.encode(file2));
		fileManager.setFileContent("files/camera_mode.png", Base64Helper.encode(file3));

		KMZDOMLoader kmzLoader = new KMZDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		kmzLoader.read();
		kmzLoader.write();

		Map<String, byte[]> loadedEntries = ZipUtils.getEntries(file);
		
		Assert.assertNotNull(loadedEntries);
		Assert.assertEquals(3, loadedEntries.size());
		
		byte[] fileZip1 = loadedEntries.get("doc.kml");
		byte[] fileZip2 = loadedEntries.get("files/star.jpg");
		byte[] fileZip3 = loadedEntries.get("files/camera_mode.png");

		Assert.assertEquals(kmzLoader.getDOM().asXML(), new String(fileZip1));
		Assert.assertArrayEquals(file2, fileZip2);
		Assert.assertArrayEquals(file3, fileZip3);

	}
	
	@Test
	public void shouldLoadKMZWithFiles() throws IOException{
		File file = new File(this.getClass().getResource("kmzExample.kmz").getFile());
		
		Map<String, byte[]> loadedEntries = ZipUtils.getEntries(file);
		
		Assert.assertNotNull(loadedEntries);
		Assert.assertEquals(3, loadedEntries.size());
		
		byte[] fileZip1 = loadedEntries.get("doc.kml");
		byte[] fileZip2 = loadedEntries.get("files/star.jpg");
		byte[] fileZip3 = loadedEntries.get("files/camera_mode.png");

		byte[] file1 = FileUtils.read(this.getClass().getResource("kmzExample_doc.kml").getFile());
		byte[] file2 = FileUtils.read(this.getClass().getResource("kmzExample_star.jpg").getFile());
		byte[] file3 = FileUtils.read(this.getClass().getResource("kmzExample_camera_mode.png").getFile());
	
		Assert.assertArrayEquals(file1, fileZip1);
		Assert.assertArrayEquals(file2, fileZip2);
		Assert.assertArrayEquals(file3, fileZip3);
		
		FileManager fileManager = new FileManager();
		
		KMZDOMLoader kmzLoader = new KMZDOMLoader(file.getAbsolutePath(), NullIdentityProvider.INSTANCE, KMLDOMLoaderFactory.createView(fileManager), fileManager);
		kmzLoader.read();
		
		Assert.assertNotNull(kmzLoader.getDOM().asXML());
		Assert.assertArrayEquals(file2, Base64Helper.decode(fileManager.getFileContent("files/star.jpg")));
		Assert.assertArrayEquals(file3, Base64Helper.decode(fileManager.getFileContent("files/camera_mode.png")));

	}
}
