package com.mesh4j.sync.adapters.kml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.security.NullIdentityProvider;

public class KMLMeshDOMLoaderTests {

	
	private static final String kmlAsXML = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
		"<kml xmlns=\"http://earth.google.com/kml/2.2\">"+
		"<Document>"+
		"	<name>dummy</name>"+
		"</Document>"+
		"</kml>";
	
	//prepateSyncRepository(Element)
	@Test
	public void shouldInitializeSyncRepository() throws DocumentException{
		
		Document kmlDocument = DocumentHelper.parseText(kmlAsXML);
		
		Element documentElement = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		Assert.assertNotNull(documentElement);
		Element extendedData = documentElement.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		Assert.assertNull(extendedData);
		
		new MockLoader(kmlDocument).read();
		
		
		extendedData = documentElement.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		Assert.assertNotNull(extendedData);
		Assert.assertNotNull(extendedData.getNamespaceForPrefix(KmlNames.MESH_PREFIX));
	}
	
	@SuppressWarnings("unused")
	private class MockLoader extends KMLMeshDOMLoader{

		private Document document;
		
		public MockLoader(Document doc){
			super("a.kmj", NullIdentityProvider.INSTANCE, KMLMeshDOMLoaderFactory.getDefaultXMLView());
			this.document = doc;
		}
				
		@Override
		protected IKMLMeshDocument createDocument(String name) {
			return new KMLMeshDocument(document, getIdentityProvider(), getXMLView());
		}

		@Override
		protected void flush() {
			
		}

		@Override
		protected IKMLMeshDocument load() {
			return new KMLMeshDocument(document, getIdentityProvider(), getXMLView());
		}	
	}
	
}
