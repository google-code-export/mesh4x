package org.mesh4j.sync.adapters.feed.atom;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.kxml2.kdom.Document;
import org.kxml2.kdom.Element;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XmlHelper;

public class AtomSyndicationFormat implements ISyndicationFormat {

	private static final String NAME = "atom10";
	public static final String ATOM_NAMESPACE = "http://www.w3.org/2005/Atom";
	private static final String ATOM_ELEMENT_FEED = "feed";
	private static final String ATOM_ELEMENT_ENTRY = "entry";
	public static final String ATOM_ELEMENT_TITLE = "title";
	public static final String ATOM_ELEMENT_SUBTITLE = "subtitle";
	public static final String ATOM_ELEMENT_SUMMARY = "summary";
	public static final String ATOM_ELEMENT_LINK = "link";
	public static final String ATOM_ELEMENT_CONTENT = "content";
	public static final String ATOM_ELEMENT_ID = "id";
	public static final String ATOM_ELEMENT_UPDATED = "updated";
	
	public static final AtomSyndicationFormat INSTANCE = new AtomSyndicationFormat();

	public Element getBaseElement(Document document) {
		return document.getElement(null, ATOM_ELEMENT_FEED);
	}

	public boolean isFeedItem(Element element) {
		return ATOM_ELEMENT_ENTRY.equals(element.getName());
	}

	public Date parseDate(String dateAsString) {
		return dateAsString == null ? null : DateHelper.parseW3CDateTime(dateAsString);
	}

	public String formatDate(Date date) {
		return date == null ? "" : DateHelper.formatW3CDateTime(date);
	}

	public void writeStartDocument(Writer writer) throws Exception {
		writer.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		writer.write("<feed xmlns=\"http://www.w3.org/2005/Atom\" xmlns:");
		writer.write(ISyndicationFormat.SX_PREFIX);
		writer.write("=\"");
		writer.write(ISyndicationFormat.SX_NAMESPACE);
		writer.write("\">");
	}
	
	public void writeEndDocument(Writer writer) throws Exception {
		writer.write("</feed>");
	}

	public void writeEndItem(Writer writer) throws Exception {
		writer.write("</entry>");		
	}

	public void writeStartItem(Writer writer, Item item) throws Exception {
		writer.write("<entry>");	
		
		writer.write("<id>");
		writer.write("urn:uuid:"+item.getSyncId());
		writer.write("</id>");
		
		if(item.getLastUpdate() != null && item.getLastUpdate().getWhen() != null){
			writer.write("<updated>");
			writer.write(formatDate(item.getLastUpdate().getWhen()));
			writer.write("</updated>");
		}
	}

	public static boolean isAtom(Document document) {
		return "feed".equals(document.getRootElement().getName());
	}
	
	public String getName() {
		return NAME;
	}

	public boolean isFeedItemDescription(Element element) {
		return ATOM_ELEMENT_SUMMARY.equals(element.getName());
	}

	public boolean isFeedItemLink(Element element) {
		return ATOM_ELEMENT_LINK.equals(element.getName());
	}


	public boolean isFeedItemTitle(Element element) {
		return ATOM_ELEMENT_TITLE.equals(element.getName());
	}
	
	public void addAuthorElement(Writer writer, String by) throws IOException {
		writer.write("<author><name>");
		writer.write(by);
		writer.write("</name></author>");		
	}


	public void addFeedInformation(Writer writer, String title, String description, String link, Date lastUpdate)  throws IOException {
		writer.write("<id>");
		writer.write(link);
		writer.write("</id>");
		
		writer.write("<title>");
		writer.write(title);
		writer.write("</title>");
		
		if(description != null && description.trim().length() > 0){
			writer.write("<subtitle>");
			writer.write(description);
			writer.write("</subtitle>");	
		}
				
		writer.write("<link href=\"");
		writer.write(link);
		writer.write("\"/>");		
		
		writer.write("<updated>");
		writer.write(formatDate(lastUpdate));
		writer.write("</updated>");		
	}

	public String getContentType() {
		return "application/atom+xml";
	}


	public Element getFeedItemPayloadElement(Element itemElement) {
		Element contentElement = getContentElement(itemElement);
		if(contentElement == null){
			return null;
		}
		
		String dataXml = (String)contentElement.getText(0);
		dataXml = dataXml.trim();
		if(dataXml.length() > 0 && dataXml.startsWith("<")){
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
			if(element != null && ATOM_ELEMENT_CONTENT.equals(element.getName())){
				return element;
			}
		}
		
		return null;
	}
	

	public boolean isFeedId(Element element) {
		return ATOM_ELEMENT_ID.equals(element.getName());
	}

	public boolean isFeedUpdated(Element element) {
		return ATOM_ELEMENT_UPDATED.equals(element.getName());
	}

	public boolean isFeedItemUpdatedElement(Element element) {
		return ATOM_ELEMENT_ID.equals(element.getName());
	}

	public boolean isFeedItemIdElement(Element element) {
		return ATOM_ELEMENT_UPDATED.equals(element.getName());
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


	public boolean isFeedDescription(Element element) {
		return ATOM_ELEMENT_SUBTITLE.equals(element.getName());
	}


	public boolean isFeedItemAuthor(Element element) {
		return SX_ELEMENT_AUTHOR.equals(element.getName());
	}


	public boolean isFeedItemPayload(Element element) {
		return ATOM_ELEMENT_CONTENT.equals(element.getName());
	}


	public boolean isFeedLink(Element element) {
		return ATOM_ELEMENT_LINK.equals(element.getName());
	}


	public boolean isFeedTitle(Element element) {
		return ATOM_ELEMENT_TITLE.equals(element.getName());
	}

	public void addFeedItemDescriptionElement(Writer writer, String description) throws IOException {
		writer.write("<summary>");
		writer.write(description);
		writer.write("</summary>");		
	}

	public void addFeedItemLinkElement(Writer writer, String link)throws IOException {
		writer.write("<link href=\"");
		writer.write(link);
		writer.write("\"/>");	
	}

	
	public void addFeedItemTitleElement(Writer writer, String title)throws IOException {
		writer.write("<title>");
		writer.write(title);
		writer.write("</title>");
	}
	
	public void addFeedItemPayloadElement(Writer writer, String payload)throws IOException {
		String xml = XmlHelper.canonicalizeXML(payload);
		
		writer.write("<content type=\"text\">");
		writer.write("<![CDATA[");
		writer.write(xml);
		writer.write("]]>");
		writer.write("</content>");
	}

	
	public String getFeedDescription(Element element) {
		return element.getText(0);
	}

	public String getFeedItemDescription(Element element) {
		return element.getText(0);
	}

	public String getFeedItemLink(Element element) {
		return element.getAttributeValue(null, "href");
	}

	public String getFeedItemTitle(Element element) {
		return element.getText(0);		
	}

	public String getFeedLink(Element element) {
		return element.getAttributeValue(null, "href");
	}

	public String getFeedTitle(Element element) {
		return element.getText(0);
	}
}
