package org.mesh4j.sync.adapters.feed.rss;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XmlHelper;

public class RssSyndicationFormat implements ISyndicationFormat {

	public static final String NAME = "rss20";
	public static final String RSS_ELEMENT_ROOT = "rss";
	public static final String RSS_ELEMENT_ITEM = "item";
	public static final String RSS_ELEMENT_CHANNEL = "channel";
	public static final String RSS_ELEMENT_TITLE = "title";
	public static final String RSS_ELEMENT_DESCRIPTION = "description";
	public static final String RSS_ELEMENT_LINK = "link";
	public static final String RSS_ELEMENT_CONTENT_ENCODED = "encoded";
	public static final String RSS_ELEMENT_GUID = "guid";
	public static final String RSS_ELEMENT_PUB_DATE = "pubDate";
	public static final String CONTENT_NS_PREFIX = "content";
	public static final String CONTENT_NS = "http://purl.org/rss/1.0/modules/content/";
	public static final String QNAME_CONTENT_ENCODED = "content:encoded";
	
	public static final RssSyndicationFormat INSTANCE = new RssSyndicationFormat();

	public Element getBaseElement(Document document) {
		Element channel = document.getRootElement().getElement(null, RSS_ELEMENT_CHANNEL);
		return channel;
	}

	public boolean isFeedItem(Element element) {
		return RSS_ELEMENT_ITEM.equals(element.getName());
	}

	public Date parseDate(String dateAsString) {
		//return dateAsString == null ? null : DateHelper.parseRFC822(dateAsString);
		return dateAsString == null ? null : DateHelper.parseW3CDateTime(dateAsString);
	}
	
	public String formatDate(Date date) {
		//return date == null ? "" : DateHelper.formatRFC822(date);
		return date == null ? "" : DateHelper.formatW3CDateTime(date);
	}
	
	private String formatRFC822Date(Date date) {
		return date == null ? "" : DateHelper.formatRFC822(date);
	}

	public Element addRootElement(Document document) {
		Element rootElement = new Element();
		rootElement.setName(RSS_ELEMENT_ROOT);
		rootElement.setAttribute(null, "version", "2.0");
		rootElement.setPrefix(SX_PREFIX, SX_NAMESPACE);
		rootElement.setPrefix(CONTENT_NS_PREFIX, CONTENT_NS);
		document.addChild(Element.ELEMENT, rootElement);
		
		Element channel = new Element();
		channel.setName(RSS_ELEMENT_CHANNEL);
		rootElement.addChild(Element.ELEMENT, channel);
		return channel;
	}

	public Element addFeedItemElement(Element root) {
		Element item = new Element();
		item.setName(RSS_ELEMENT_ITEM);
		root.addChild(Element.ELEMENT, item);
		return item;
	}
	
	public void writeStartDocument(Writer writer) throws Exception {
		writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		writer.write("<rss version=\"2.0\" xmlns:");
		writer.write(ISyndicationFormat.SX_PREFIX);
		writer.write("=\"");
		writer.write(ISyndicationFormat.SX_NAMESPACE);
		writer.write(" xmlns:content=\"http://purl.org/rss/1.0/modules/content/");
		writer.write("\">");
		writer.write("<channel>");		
	}
	
	public void writeEndDocument(Writer writer) throws Exception {
		writer.write("</channel>");
		writer.write("</rss>");		
	}

	public void writeEndItem(Writer writer) throws Exception {
		writer.write("</item>");		
	}

	public void writeStartItem(Writer writer, Item item) throws Exception {
		writer.write("<item>");	
		writer.write("<guid isPermaLink=\"false\">");
		writer.write("urn:uuid:"+item.getSyncId());
		writer.write("</guid>");
		
		if(item.getLastUpdate() != null && item.getLastUpdate().getWhen() != null){
			writer.write("<pubDate>");
			writer.write(formatRFC822Date(item.getLastUpdate().getWhen()));
			writer.write("</pubDate>");
		}
	}

	public String getName() {
		return NAME;
	}

	public boolean isFeedItemDescription(Element element) {
		return RSS_ELEMENT_DESCRIPTION.equals(element.getName());
	}

	public boolean isFeedItemLink(Element element) {
		return RSS_ELEMENT_LINK.equals(element.getName());
	}

	public boolean isFeedItemTitle(Element element) {
		return RSS_ELEMENT_TITLE.equals(element.getName());
	}

	public void addAuthorElement(Writer writer, String by) throws IOException {
		writer.write("<author>");
		writer.write(makeEmailAuthor(by));
		writer.write("</author>");
	}
	
	private String makeEmailAuthor(String author) {
		return author+"@"+"mesh4x.example";
	}

	public void addFeedInformation(Writer writer, String title, String description, String link, Date lastUpdate) throws IOException {
		addFeedItemTitleElement(writer, title);
		addFeedItemDescriptionElement(writer, description);
		addFeedItemLinkElement(writer, link);
		
		writer.write("<pubDate>");
		writer.write(formatRFC822Date(lastUpdate));
		writer.write("</pubDate>");
	}

	public void addFeedItemDescriptionElement(Writer writer, String description)throws IOException {
		writer.write("<description>");
		writer.write(description);
		writer.write("</description>");
	}

	public void addFeedItemLinkElement(Writer writer, String link)throws IOException {
		writer.write("<link>");
		writer.write(link);
		writer.write("</link>");
	}

	public void addFeedItemPayloadElement(Writer writer, String payload)throws IOException {
		String xml = XmlHelper.canonicalizeXML(payload);
		
		writer.write("<content:encoded>");
		writer.write("<![CDATA[");
		writer.write(xml);
		writer.write("]]>");
		writer.write("</content:encoded>");
	}

	public void addFeedItemTitleElement(Writer writer, String title)throws IOException {
		writer.write("<title>");
		writer.write(title);
		writer.write("</title>");		
	}

	public String getContentType() {
		return "application/rss+xml";
	}

	public Element getFeedItemPayloadElement(Element itemElement) {
		Element contentElement = getContentElement(itemElement);
		if(contentElement == null){
			return null;
		}
		
		String dataXml = (String)contentElement.getText(0);
		dataXml = dataXml.trim();
		if(dataXml.length() > 0){
			return XmlHelper.getElement(dataXml);
		} else {
			return null;
		}
	}
	
	
	private Element getContentElement(Element itemElement){
		int elementCount = itemElement.getChildCount();
		Element element;
		for (int i = 0; i < elementCount; i++) {
			element = itemElement.getElement(i);
			if(element != null && QNAME_CONTENT_ENCODED.equals(element.getName())){
				return element;
			}
		}
		
		return null;
	}	

	public boolean isFeedItemAuthor(Element element) {
		return SX_ELEMENT_AUTHOR.equals(element.getName());
	}

	public boolean isFeedItemPayload(Element element) {
		return QNAME_CONTENT_ENCODED.equals(element.getName());
	}

	private boolean isFeedItemUpdatedElement(Element element) {
		return RSS_ELEMENT_GUID.equals(element.getName());
	}

	private boolean isFeedItemIdElement(Element element) {
		return RSS_ELEMENT_PUB_DATE.equals(element.getName());
	}
	
	public boolean isAditionalFeedItemPayload(Element element) {
		return !isFeedItemAuthor(element) 
		&& !isFeedItemPayload(element)
		&& !isFeedItemIdElement(element)
		&& !isFeedItemUpdatedElement(element);
	}

	public boolean isAditionalFeedPayload(Element element) {
		return !isFeedId(element) &&
			!isFeedTitle(element) &&
			!isFeedDescription(element) &&		
			!isFeedLink(element) &&
			!isFeedUpdated(element);
	}
	
	private boolean isFeedUpdated(Element element) {
		return RSS_ELEMENT_PUB_DATE.equals(element.getName());
	}

	private boolean isFeedId(Element element) {
		return RSS_ELEMENT_GUID.equals(element.getName());
	}

	public boolean isFeedDescription(Element element) {
		return RSS_ELEMENT_DESCRIPTION.equals(element.getName());
	}

	public boolean isFeedLink(Element element) {
		return RSS_ELEMENT_LINK.equals(element.getName());
	}

	public boolean isFeedTitle(Element element) {
		return RSS_ELEMENT_TITLE.equals(element.getName());
	}


	public String getFeedDescription(Element element) {
		return element.getText(0);
	}


	public String getFeedItemDescription(Element element) {
		return element.getText(0);
	}


	public String getFeedItemLink(Element element) {
		return element.getText(0);
	}


	public String getFeedItemTitle(Element element) {
		return element.getText(0);
	}


	public String getFeedLink(Element element) {
		return element.getText(0);
	}


	public String getFeedTitle(Element element) {
		return element.getText(0);
	}
}
