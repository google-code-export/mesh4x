package org.mesh4j.sync.adapters.feed;

import java.util.Date;

import junit.framework.Assert;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.payload.mappings.IMapping;

public class ContentWriterTests {

	@Test
	public void shouldDefaultInstanceMustWriteSync(){
		Assert.assertTrue(ContentWriter.INSTANCE.mustWriteSync(null));
	
		Item item = new Item(new NullContent("2"), new Sync("2").delete("jmt", new Date()));
		Assert.assertTrue(ContentWriter.INSTANCE.mustWriteSync(item));
	}
	
	@Test
	public void shouldDefaultInstanceNotFailsIfFormatIsNull(){
		Item item = new Item(new NullContent("2"), new Sync("2").delete("jmt", new Date()));
		
		Element itemElement = DocumentHelper.createElement("payload");
		String xml = itemElement.asXML();
		ContentWriter.INSTANCE.writeContent(null, itemElement, item);
		Assert.assertEquals(xml, itemElement.asXML());
	}
	
	@Test
	public void shouldDefaultInstanceNotFailsIfElementIsNull(){
		Item item = new Item(new NullContent("2"), new Sync("2").delete("jmt", new Date()));
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, null, item);
	}
	
	@Test
	public void shouldDefaultInstanceNotFailsIfItemIsNull(){
		Element itemElement = DocumentHelper.createElement("payload");
		String xml = itemElement.asXML();
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElement, null);
		Assert.assertEquals(xml, itemElement.asXML());
	}	
		
	@Test
	public void shouldDefaultInstanceWriteDeletedItem(){
		
		Item item = new Item(new NullContent("2"), new Sync("2").delete("jmt", new Date()));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>Element was DELETED, content id = 2, sync Id = 2</title><description>---DELETED---</description></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">Element was DELETED, content id = 2, sync Id = 2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">---DELETED---</summary></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItem(){
		
		IContent content = new IContent(){
			@Override public String getId() {return "1";}
			@Override public Element getPayload() {return DocumentHelper.createElement("foo");}
			@Override public int getVersion() {return 0;}
			@Override public IContent clone() {return this;}
		};
		
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>2</title><description>Id: 1 Version: 0</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">Id: 1 Version: 0</summary><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContent(){
		
		XMLContent content = new XMLContent("1", "myTitle", "myDescription", "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTitle</title><description>myDescription</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTitle</title><summary xmlns=\"http://www.w3.org/2005/Atom\">myDescription</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithNullTitle(){
		
		XMLContent content = new XMLContent("1", null, "myDescription", "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>2</title><description>myDescription</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">myDescription</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithEmptyTitle(){
		
		XMLContent content = new XMLContent("1", "", "myDescription", "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>2</title><description>myDescription</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">myDescription</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithNullDescription(){
		
		XMLContent content = new XMLContent("1", "myTtile", null, "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTtile</title><description>Id: 1 Version: -938014305</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTtile</title><summary xmlns=\"http://www.w3.org/2005/Atom\">Id: 1 Version: -938014305</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithEmptyDescription(){
		
		XMLContent content = new XMLContent("1", "myTtile", "", "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTtile</title><description>Id: 1 Version: -938014305</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTtile</title><summary xmlns=\"http://www.w3.org/2005/Atom\">Id: 1 Version: -938014305</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithNullLink(){
		
		XMLContent content = new XMLContent("1", "myTtile", "MyDescription", null, DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTtile</title><description>MyDescription</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTtile</title><summary xmlns=\"http://www.w3.org/2005/Atom\">MyDescription</summary><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithEmptyLink(){
		
		XMLContent content = new XMLContent("1", "myTtile", "MyDescription", "", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTtile</title><description>MyDescription</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTtile</title><summary xmlns=\"http://www.w3.org/2005/Atom\">MyDescription</summary><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithSinglePayload(){
		
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		payload.addElement("foo");
		
		XMLContent content = new XMLContent("1", "myTtile", "MyDescription", "http://localhost:8080/mesh4x", payload);
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTtile</title><description>MyDescription</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTtile</title><summary xmlns=\"http://www.w3.org/2005/Atom\">MyDescription</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}
	
	@Test
	public void shouldDefaultInstanceWriteItemXMLContentWithMultiPayload(){
		
		Element payload = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		payload.addElement("foo");
		payload.addElement("bar");
		
		XMLContent content = new XMLContent("1", "myTtile", "MyDescription", "http://localhost:8080/mesh4x", payload);
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTtile</title><description>MyDescription</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<payload><foo></foo><bar></bar></payload>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		ContentWriter.INSTANCE.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTtile</title><summary xmlns=\"http://www.w3.org/2005/Atom\">MyDescription</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<payload><foo></foo><bar></bar></payload>]]></content></payload>", itemElementAtom.asXML());
	}
	
	
	// MAPPINGS
	private IMapping makeMapping() {
		IMapping mapping = new IMapping(){
			@Override public String asXML() {return null;}
			@Override public String getMapping(String mappingName) {return null;}
			@Override public String getValue(Element element, String mappingName) {
				if(ISyndicationFormat.MAPPING_NAME_ITEM_TITLE.equals(mappingName)){
					return "mapTitle";
				}
				if(ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION.equals(mappingName)){
					return "mapDesc";
				}
				return null;
			}			
		};
		return mapping;
	}	
	
	private IMapping makeNullMapping() {
		IMapping mapping = new IMapping(){
			@Override public String asXML() {return null;}
			@Override public String getMapping(String mappingName) {return null;}
			@Override public String getValue(Element element, String mappingName) {return null;}			
		};
		return mapping;
	}	
	
	
	@Test
	public void shouldInstanceWithMappingWriteItem(){
		
		IContent content = new IContent(){
			@Override public String getId() {return "1";}
			@Override public Element getPayload() {return DocumentHelper.createElement("foo");}
			@Override public int getVersion() {return 0;}
			@Override public IContent clone() {return this;}
		};
		
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		
		ContentWriter writer = new ContentWriter( makeMapping());
		
		writer.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>mapTitle</title><description>mapDesc</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		writer.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">mapTitle</title><summary xmlns=\"http://www.w3.org/2005/Atom\">mapDesc</summary><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}

	@Test
	public void shouldInstanceWithMappingWriteItemXMLContent(){
		
		XMLContent content = new XMLContent("1", "myTitle", "myDescription", "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		ContentWriter writer = new ContentWriter( makeMapping());
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		writer.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>mapTitle</title><description>mapDesc</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		writer.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">mapTitle</title><summary xmlns=\"http://www.w3.org/2005/Atom\">mapDesc</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}	
		
	
	@Test
	public void shouldInstanceWithMappingWriteItemWithDefaultBehaviourIfMappingDoesNotExists(){
		
		IContent content = new IContent(){
			@Override public String getId() {return "1";}
			@Override public Element getPayload() {return DocumentHelper.createElement("foo");}
			@Override public int getVersion() {return 0;}
			@Override public IContent clone() {return this;}
		};
		
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		
		ContentWriter writer = new ContentWriter( makeNullMapping());
		
		writer.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>2</title><description>Id: 1 Version: 0</description><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		writer.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">2</title><summary xmlns=\"http://www.w3.org/2005/Atom\">Id: 1 Version: 0</summary><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}

	@Test
	public void shouldInstanceWithMappingWriteItemXMLContentWithDefaultBehaviourIfMappingDoesNotExists(){
		
		XMLContent content = new XMLContent("1", "myTitle", "myDescription", "http://localhost:8080/mesh4x", DocumentHelper.createElement("foo"));
		Item item = new Item(content, new Sync("2", "jmt", new Date(), false));
		
		ContentWriter writer = new ContentWriter( makeNullMapping());
		
		Element itemElementRss = DocumentHelper.createElement("payload");
		writer.writeContent(RssSyndicationFormat.INSTANCE, itemElementRss, item);
		Assert.assertEquals("<payload><title>myTitle</title><description>myDescription</description><link>http://localhost:8080/mesh4x</link><content:encoded xmlns:content=\"http://purl.org/rss/1.0/modules/content/\"><![CDATA[<foo></foo>]]></content:encoded></payload>", itemElementRss.asXML());
		
		Element itemElementAtom = DocumentHelper.createElement("payload");
		writer.writeContent(AtomSyndicationFormat.INSTANCE, itemElementAtom, item);
		Assert.assertEquals("<payload><title xmlns=\"http://www.w3.org/2005/Atom\">myTitle</title><summary xmlns=\"http://www.w3.org/2005/Atom\">myDescription</summary><link xmlns=\"http://www.w3.org/2005/Atom\" href=\"http://localhost:8080/mesh4x\"/><content type=\"text\"><![CDATA[<foo></foo>]]></content></payload>", itemElementAtom.asXML());
	}
}
