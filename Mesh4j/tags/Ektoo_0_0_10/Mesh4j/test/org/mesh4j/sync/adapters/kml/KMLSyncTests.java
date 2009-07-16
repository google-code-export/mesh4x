package org.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.dom.DOMLoader;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.XMLHelper;


public class KMLSyncTests {

	@Test
	public void shouldSync() throws DocumentException{
		sync(KmlNames.KML_ELEMENT_GROUND_OVERLAY, "kmlSyncTestsGround.kml");
		sync(KmlNames.KML_ELEMENT_PLACEMARK, "kmlSyncTestsPlacemark.kml");
		//prepareToSync("kmlSyncTestsPlacemark.kml");
	}

	protected void prepareToSync(String fileName) throws DocumentException{
		DOMLoader loader = KMLDOMLoaderFactory.createDOMLoader(this.getClass().getResource(fileName).getFile(), NullIdentityProvider.INSTANCE);
		DOMAdapter kml = new DOMAdapter(loader);
		kml.beginSync();
		
		XMLHelper.write(kml.getDOM().toDocument(), new File(TestHelper.fileName(fileName)));
	}

	
	private void sync(String elementType, String fileNameA) throws DocumentException{

		String fileNamB = "kmlSyncTestsEmpty.kml";  // EMPTY FILE
				
		// generate files			
		DOMLoader loaderA = KMLDOMLoaderFactory.createDOMLoader(this.getClass().getResource(fileNameA).getFile(), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlA = new DOMAdapter(loaderA);
		kmlA.beginSync();
		XMLHelper.write(kmlA.getDOM().toDocument(), new File(TestHelper.fileName(fileNameA)));
		
		DOMLoader loaderB = KMLDOMLoaderFactory.createDOMLoader(this.getClass().getResource(fileNamB).getFile(), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlB = new DOMAdapter(loaderB);
		kmlB.beginSync();
		XMLHelper.write(kmlB.getDOM().toDocument(), new File(TestHelper.fileName(fileNamB)));
		
		// sync
		DOMLoader loaderAsync = KMLDOMLoaderFactory.createDOMLoader(TestHelper.fileName(fileNameA), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlAsync = new DOMAdapter(loaderAsync);
		
		DOMLoader loaderBsync = KMLDOMLoaderFactory.createDOMLoader(TestHelper.fileName(fileNamB), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlBsync = new DOMAdapter(loaderBsync);
		
		SyncEngine syncEngine = new SyncEngine(kmlAsync, kmlBsync);
		
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		// asserts DOC_B
		Document docB = kmlBsync.getDOM().toDocument();
		
		Element elementFolder = docB
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		Element element = elementFolder.element(elementType);
		
		Assert.assertNotNull(element);
		Assert.assertEquals("my old name", element.elementText("name"));
		
		// asserts DOC_A
		Document docA = kmlAsync.getDOM().toDocument();
		
		elementFolder = docA
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		element = elementFolder.element(elementType);
		
		Assert.assertNotNull(element);
		Assert.assertEquals("my old name", element.elementText("name"));
		
		// DOC_A MOVE ELEMENT TO ROOT
		docA = kmlAsync.getDOM().toDocument();
		Element elementToMove = docA
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER)
			.element(elementType);
		
		elementToMove.getParent().remove(elementToMove);
		docA.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT).add(elementToMove);
		XMLHelper.write(docA, new File(TestHelper.fileName(fileNameA)));
				
		// DOC_B RENAME ELEMENT
		docB = kmlBsync.getDOM().toDocument();
		Element elementToRename = docB
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER)
			.element(elementType);
		
		elementToRename.element("name").setText("my new name");
		XMLHelper.write(docB, new File(TestHelper.fileName(fileNamB)));
		
		// sync
		loaderAsync = KMLDOMLoaderFactory.createDOMLoader(TestHelper.fileName(fileNameA), NullIdentityProvider.INSTANCE);
		kmlAsync = new DOMAdapter(loaderAsync);
		
		loaderBsync = KMLDOMLoaderFactory.createDOMLoader(TestHelper.fileName(fileNamB), NullIdentityProvider.INSTANCE);
		kmlBsync = new DOMAdapter(loaderBsync);
		
		syncEngine = new SyncEngine(kmlAsync, kmlBsync);
		
		conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());
		
		// asserts DOC_B
		docB = kmlBsync.getDOM().toDocument();
		
		elementFolder = docB
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		Assert.assertNull(elementFolder.element(elementType));
		
		element = docB
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(elementType);
		
		Assert.assertNotNull(element);
		Assert.assertEquals("my new name", element.elementText("name"));
		
		// asserts DOC_A
		docA = kmlAsync.getDOM().toDocument();
		
		elementFolder = docA
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(KmlNames.KML_ELEMENT_FOLDER);
		
		Assert.assertNull(elementFolder.element(elementType));
		
		element = docA
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT)
			.element(elementType);
		
		Assert.assertNotNull(element);
		Assert.assertEquals("my new name", element.elementText("name"));
	}
	
	@Test
	public void shouldSyncExtendedData(){
//		File file = new File(this.getClass().getResource("kmlWithExtendedDataToSync.kml").getFile());
//		File fileEmpty = new File(this.getClass().getResource("kmlDummyForSync.kml").getFile());

		// generate files			
		DOMLoader loaderA = KMLDOMLoaderFactory.createDOMLoader(this.getClass().getResource("kmlWithExtendedDataToSync.kml").getFile(), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlA = new DOMAdapter(loaderA);
		kmlA.beginSync();
		XMLHelper.write(kmlA.getDOM().toDocument(), new File(TestHelper.fileName("kmlWithExtendedDataToSync.kml")));
		
		List<Item> items = kmlA.getAll();		
		Assert.assertNotNull(items);
		Assert.assertEquals(6, items.size());
		
		DOMLoader loaderB = KMLDOMLoaderFactory.createDOMLoader(this.getClass().getResource("kmlDummyForSync.kml").getFile(), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlB = new DOMAdapter(loaderB);
		kmlB.beginSync();
		XMLHelper.write(kmlB.getDOM().toDocument(), new File(TestHelper.fileName("kmlDummyForSync.kml")));

		items = kmlB.getAll();		
		Assert.assertNotNull(items);
		Assert.assertEquals(0, items.size());
		
		// sync
		DOMLoader loaderAsync = KMLDOMLoaderFactory.createDOMLoader(TestHelper.fileName("kmlWithExtendedDataToSync.kml"), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlAsync = new DOMAdapter(loaderAsync);
		
		DOMLoader loaderBsync = KMLDOMLoaderFactory.createDOMLoader(TestHelper.fileName("kmlDummyForSync.kml"), NullIdentityProvider.INSTANCE);
		DOMAdapter kmlBsync = new DOMAdapter(loaderBsync);
		
		SyncEngine syncEngine = new SyncEngine(kmlAsync, kmlBsync);
		
		List<Item> conflicts = syncEngine.synchronize();
		Assert.assertNotNull(conflicts);
		Assert.assertEquals(0, conflicts.size());

		items = kmlAsync.getAll();		
		Assert.assertNotNull(items);
		Assert.assertEquals(6, items.size());
		
		items = kmlBsync.getAll();		
		Assert.assertNotNull(items);
		Assert.assertEquals(6, items.size());
		
	}
}
