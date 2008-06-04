package com.mesh4j.sync.adapters.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.NullContent;

public class KMLContentTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateNotAccetpNullPayload(){
		new KMLContent(null, "1");
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateNotAccetpNullSyncID(){
		new KMLContent(DocumentHelper.createElement("payload"), null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateNotAccetpEmptySyncID(){
		new KMLContent(DocumentHelper.createElement("payload"), "");
	}

	@Test
	public void shouldClone(){
		KMLContent original = new KMLContent(DocumentHelper.createElement("payload"), "1");
		KMLContent clone = original.clone();
		
		Assert.assertFalse(original == clone);
		Assert.assertSame(original.getPayload(), clone.getPayload());
		Assert.assertEquals(original.getId(), clone.getId());
		Assert.assertEquals(original.getVersion(), clone.getVersion());
	}
	
	@Test
	public void shouldAddToFeedPayload(){
		Element feedPayload = DocumentHelper.createElement("feedPayload");
		Element payload = DocumentHelper.createElement("payload");
		
		KMLContent content = new KMLContent(payload, "1");
		
		content.addToFeedPayload(feedPayload);
		
		Assert.assertNotNull(feedPayload.element(ISyndicationFormat.SX_ATTRIBUTE_ITEM_TITLE));
		Assert.assertNotNull(feedPayload.element(ISyndicationFormat.SX_ATTRIBUTE_ITEM_TITLE).getText());
		Assert.assertNotNull(feedPayload.element(ISyndicationFormat.SX_ATTRIBUTE_ITEM_DESCRIPTION));
		Assert.assertNotNull(feedPayload.element(ISyndicationFormat.SX_ATTRIBUTE_ITEM_DESCRIPTION).getText());
		Assert.assertNotNull(feedPayload.element("payload"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldNormalizeNotAcceptNullXMLView(){
		KMLContent.normalizeContent(new NullContent("1"), null);
	}
	
	@Test
	public void shouldNormalizeKMLContent(){

		Element payload = DocumentHelper.createElement("payload");
		KMLContent content = new KMLContent(payload, "1");
		
		Assert.assertSame(content, KMLContent.normalizeContent(content, DOMLoaderFactory.createKMLView()));
	}
	
	@Test
	public void shouldNormalizePayload(){
		assertNormalizePayload(DocumentHelper.createElement(KmlNames.KML_ELEMENT_FOLDER));
		assertNormalizePayload(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE));
		assertNormalizePayload(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE_MAP));
		assertNormalizePayload(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PLACEMARK));
		assertNormalizePayload(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PHOTO_OVERLAY));
		assertNormalizePayload(DocumentHelper.createElement(KmlNames.KML_ELEMENT_GROUND_OVERLAY));
		assertNormalizePayload(DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY));
//		assertNormalizePayload(DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE));
	}
	
	private void assertNormalizePayload(Element element){
		element.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, "1");
			
		Element payload = DocumentHelper.createElement("payload");
		payload.add(element);
		
		MockContent content = new MockContent(payload);
		
		KMLContent normalizedContent = KMLContent.normalizeContent(content, DOMLoaderFactory.createKMLView());
		Assert.assertNotNull(normalizedContent);
		Assert.assertSame(element, normalizedContent.getPayload());
		Assert.assertEquals("1", normalizedContent.getId());
	}
	
	@Test
	public void shouldNormalizePayloadFailsBecauseDoesNotContainsSyncID(){
		assertNormalizePayloadFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_FOLDER));
		assertNormalizePayloadFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE));
		assertNormalizePayloadFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE_MAP));
		assertNormalizePayloadFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PLACEMARK));
		assertNormalizePayloadFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PHOTO_OVERLAY));
		assertNormalizePayloadFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_GROUND_OVERLAY));
		assertNormalizePayloadFails(DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY));
		assertNormalizePayloadFails(DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE));
	}

	private void assertNormalizePayloadFails(Element element){
			
		Element payload = DocumentHelper.createElement("payload");
		payload.add(element);
		
		MockContent content = new MockContent(payload);
		
		KMLContent normalizedContent = KMLContent.normalizeContent(content, DOMLoaderFactory.createKMLView());
		Assert.assertNull(normalizedContent);
	}
	
	@Test
	public void shouldNormalize(){
		assertNormalize(DocumentHelper.createElement(KmlNames.KML_ELEMENT_FOLDER));
		assertNormalize(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE));
		assertNormalize(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE_MAP));
		assertNormalize(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PLACEMARK));
		assertNormalize(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PHOTO_OVERLAY));
		assertNormalize(DocumentHelper.createElement(KmlNames.KML_ELEMENT_GROUND_OVERLAY));
		assertNormalize(DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY));
//		assertNormalize(DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE));
	}
	
	private void assertNormalize(Element element){
		element.addAttribute(MeshNames.MESH_QNAME_SYNC_ID, "1");
			
		MockContent content = new MockContent(element);
		
		KMLContent normalizedContent = KMLContent.normalizeContent(content, DOMLoaderFactory.createKMLView());
		Assert.assertNotNull(normalizedContent);
		Assert.assertSame(element, normalizedContent.getPayload());
		Assert.assertEquals("1", normalizedContent.getId());
	}
	
	@Test
	public void shouldNormalizeFailsBecauseDoesNotContainsSyncID(){
		assertNormalizeFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_FOLDER));
		assertNormalizeFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE));
		assertNormalizeFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_STYLE_MAP));
		assertNormalizeFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PLACEMARK));
		assertNormalizeFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_PHOTO_OVERLAY));
		assertNormalizeFails(DocumentHelper.createElement(KmlNames.KML_ELEMENT_GROUND_OVERLAY));
		assertNormalizeFails(DocumentHelper.createElement(MeshNames.MESH_QNAME_HIERARCHY));
		assertNormalizeFails(DocumentHelper.createElement(MeshNames.MESH_QNAME_FILE));
	}
	
	private void assertNormalizeFails(Element element){
		MockContent content = new MockContent(element);
		KMLContent normalizedContent = KMLContent.normalizeContent(content, DOMLoaderFactory.createKMLView());
		Assert.assertNull(normalizedContent);
	}
	
	
	@Test
	public void shouldNormalizeFailsBecauseInvalidPayload(){
		assertNormalizeFails(DocumentHelper.createElement("Foo"));
	}

	private class MockContent implements IContent{

		private Element payload;
		
		public MockContent(Element payload) {
			super();
			this.payload = payload;
		}
		
		@Override
		public void addToFeedPayload(Element rootPayload) {
		}

		@Override
		public String getId() {
			return "1";
		}

		@Override
		public Element getPayload() {
			return payload;
		}

		@Override
		public int getVersion() {
			return 0;
		}
		
		public MockContent clone(){
			return this;
		}
	}
}
