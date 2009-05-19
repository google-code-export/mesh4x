package org.mesh4j.sync.adapters.feed.rss;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;


public class RssSyndicationFormat implements ISyndicationFormat {

	// CONSTANTS
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
	public static final QName QNAME_CONTENT_ENCODED = DocumentHelper.createQName(RSS_ELEMENT_CONTENT_ENCODED, DocumentHelper.createNamespace(CONTENT_NS_PREFIX, CONTENT_NS));

	public static final RssSyndicationFormat INSTANCE = new RssSyndicationFormat();

	// BUSINESS METHODS
	
	private RssSyndicationFormat(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Element> getRootElements(Element root) {
		return root.element(RSS_ELEMENT_CHANNEL).elements();
	}

	@Override
	public boolean isFeedItem(Element element) {
		return RSS_ELEMENT_ITEM.equals(element.getName());
	}

	@Override
	public Date parseDate(String dateAsString) {
		//return dateAsString == null ? null : DateHelper.parseRFC822(dateAsString);
		return dateAsString == null ? null : DateHelper.parseW3CDateTime(dateAsString);
	}
	
	@Override
	public String formatDate(Date date) {
		//return date == null ? "" : DateHelper.formatRFC822(date);
		return date == null ? "" : DateHelper.formatW3CDateTime(date);
	}
	
	private String formatRFC822Date(Date date) {
		return date == null ? "" : DateHelper.formatRFC822(date);
	}


	@Override
	public Element addRootElement(Document document) {
		Element rootElement = document.addElement(RSS_ELEMENT_ROOT);
		rootElement.add(new Namespace(SX_PREFIX, NAMESPACE));
		rootElement.add(new Namespace(CONTENT_NS_PREFIX, CONTENT_NS));
		rootElement.addAttribute("version", "2.0");
		Element channel = rootElement.addElement(RSS_ELEMENT_CHANNEL);
		return channel;
	}

	@Override
	public Element addFeedItemElement(Element root, Item item) {
		Element itemElement = root.addElement(RSS_ELEMENT_ITEM);
		
		Element idElement = itemElement.addElement(RSS_ELEMENT_GUID);
		idElement.setText("urn:uuid:"+item.getSyncId());
		idElement.addAttribute("isPermaLink ", "false");
		
		if(item.getLastUpdate() != null && item.getLastUpdate().getWhen() != null){
			Element updatedElement = itemElement.addElement(RSS_ELEMENT_PUB_DATE);
			updatedElement.setText(formatRFC822Date(item.getLastUpdate().getWhen()));
		}
		return itemElement;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void addFeedInformation(Element rootElement, String title, String description, String link, Date lastUpdated) {
		addFeedItemTitleElement(rootElement, title);
		addFeedItemDescriptionElement(rootElement, description);
		addFeedItemLinkElement(rootElement, link);
		addOrUpdateElement(RSS_ELEMENT_PUB_DATE, rootElement, formatRFC822Date(lastUpdated));
	}


	@Override
	public Element addFeedItemDescriptionElement(Element itemElement, String description) {
		return addOrUpdateElement(RSS_ELEMENT_DESCRIPTION, itemElement, description);

	}

	@Override
	public Element addFeedItemLinkElement(Element itemElement, String absoluteLink) {
		return addOrUpdateElement(RSS_ELEMENT_LINK, itemElement, absoluteLink);
	}

	@Override
	public Element addFeedItemTitleElement(Element itemElement, String title) {
		return addOrUpdateElement(RSS_ELEMENT_TITLE, itemElement, title);
	}
	
	
	@Override
	public Element getFeedItemDescriptionElement(Element itemElement) {
		return itemElement.element(RSS_ELEMENT_DESCRIPTION);
	}

	@Override
	public Element getFeedItemLinkElement(Element itemElement) {
		return itemElement.element(RSS_ELEMENT_LINK);
	}

	@Override
	public Element getFeedItemTitleElement(Element itemElement) {
		return itemElement.element(RSS_ELEMENT_TITLE);
	}

	@Override
	public boolean isFeedItemDescription(Element element) {
		return RSS_ELEMENT_DESCRIPTION.equals(element.getName());
	}

	@Override
	public boolean isFeedItemLink(Element element) {
		return RSS_ELEMENT_LINK.equals(element.getName());
	}

	@Override
	public boolean isFeedItemTitle(Element element) {
		return RSS_ELEMENT_TITLE.equals(element.getName());
	}

	@Override
	public void addAuthorElement(Element itemElement, String author) {
		Element authorElement = itemElement.element(SX_ELEMENT_AUTHOR);
		if(authorElement == null){
			authorElement = itemElement.addElement(SX_ELEMENT_AUTHOR);
		}
		
		String email = makeEmailAuthor(author);
		authorElement.setText(email);
	}
	
	private String makeEmailAuthor(String author) {
		return author+"@"+"mesh4x.example";
	}

	@Override
	public String getContentType() {
		return "application/rss+xml";
	}

	@Override
	public void addFeedItemPayloadElement(Element itemElement, Element payload) {
		String xml = XMLHelper.canonicalizeXML(payload);
		Element contentElement = itemElement.element(QNAME_CONTENT_ENCODED);
		if(contentElement == null){
			contentElement = itemElement.addElement(QNAME_CONTENT_ENCODED);
		}
		contentElement.add(DocumentHelper.createCDATA(xml));
	}

	@Override
	public Element getFeedItemPayloadElement(Element itemElement) {
		Element contentElement = itemElement.element(QNAME_CONTENT_ENCODED);
		if(contentElement == null){
			return null;
		}
		
		String dataXml = (String)contentElement.getData();
		dataXml = dataXml.trim();
		if(dataXml.length() > 0){
			return XMLHelper.parseElement(dataXml);
		} else {
			return null;
		}
	}

	@Override
	public boolean isFeedItemAuthor(Element element) {
		return SX_ELEMENT_AUTHOR.equals(element.getName());
	}

	@Override
	public boolean isFeedItemPayload(Element element) {
		return QNAME_CONTENT_ENCODED.getName().equals(element.getName());
	}

	private boolean isFeedItemUpdatedElement(Element element) {
		return RSS_ELEMENT_GUID.equals(element.getName());
	}

	private boolean isFeedItemIdElement(Element element) {
		return RSS_ELEMENT_PUB_DATE.equals(element.getName());
	}
	
	@Override
	public boolean isAditionalFeedItemPayload(Element element) {
		return !isFeedItemAuthor(element) 
		&& !isFeedItemPayload(element)
		&& !isFeedItemIdElement(element)
		&& !isFeedItemUpdatedElement(element);
	}

	@Override
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

	@Override
	public boolean isFeedDescription(Element element) {
		return RSS_ELEMENT_DESCRIPTION.equals(element.getName());
	}

	@Override
	public boolean isFeedLink(Element element) {
		return RSS_ELEMENT_LINK.equals(element.getName());
	}

	@Override
	public boolean isFeedTitle(Element element) {
		return RSS_ELEMENT_TITLE.equals(element.getName());
	}
	
	private Element addOrUpdateElement(String elementName, Element rootElement, String text) {
		Element element = rootElement.element(elementName);
		if(element == null){
			element = DocumentHelper.createElement(elementName);
			rootElement.add(element);
		}
		element.setText(text);
		return element;
	}
	
	public String getFeedTitle(Element element){
		return element.getText();
	}
	
	public String getFeedDescription(Element element){
		return element.getText();
	}
	
	public String getFeedLink(Element element){
		return element.getText();	
	}
	
	public String getFeedItemTitle(Element element){
		return element.getText();
	}
	
	public String getFeedItemDescription(Element element){
		return element.getText();	
	}
	
	public String getFeedItemLink(Element element){
		return element.getText();
	}
}
