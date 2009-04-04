package org.mesh4j.sync.adapters.feed.atom;

import java.util.Date;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;

public class AtomSyndicationFormat implements ISyndicationFormat {

	// CONSTANTS
	
	private static final String NAME = "atom10";
	public static final Namespace ATOM_NAMESPACE = new Namespace("", "http://www.w3.org/2005/Atom");
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

	// BUSINESS METHODS
	
	private AtomSyndicationFormat(){
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Element> getRootElements(Element root) {
		return root.elements();
	}

	@Override
	public boolean isFeedItem(Element element) {
		return ATOM_ELEMENT_ENTRY.equals(element.getName());
	}

	@Override
	public Date parseDate(String dateAsString) {
		return dateAsString == null ? null : DateHelper.parseW3CDateTime(dateAsString);
	}

	@Override
	public String formatDate(Date date) {
		return date == null ? "" : DateHelper.formatW3CDateTime(date);
	}

	@Override
	public Element addRootElement(Document document) {
		Element rootElement = document.addElement(ATOM_ELEMENT_FEED);
		rootElement.add(ATOM_NAMESPACE);
		rootElement.add(new Namespace(SX_PREFIX, NAMESPACE));
		return rootElement;
	}

	@Override
	public Element addFeedItemElement(Element root, Item item) {
		Element itemElement = root.addElement(DocumentHelper.createQName(ATOM_ELEMENT_ENTRY, ATOM_NAMESPACE));
		
		Element idElement = itemElement.addElement(DocumentHelper.createQName(ATOM_ELEMENT_ID, ATOM_NAMESPACE));
		idElement.setText("urn:uuid:"+item.getSyncId());
		
		if(item.getLastUpdate() != null && item.getLastUpdate().getWhen() != null){
			Element updatedElement = itemElement.addElement(DocumentHelper.createQName(ATOM_ELEMENT_UPDATED, ATOM_NAMESPACE));
			updatedElement.setText(formatDate(item.getLastUpdate().getWhen()));
		}
		return itemElement;
	}

	public static boolean isAtom(Document document) {
		return "feed".equals(document.getRootElement().getName());
	}

	public static boolean isAtom(String protocol) {
		return "feed".equals(protocol);
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Element addFeedItemDescriptionElement(Element itemElement, String description) {
		Element descriptionElement = getFeedItemDescriptionElement(itemElement);
		if(descriptionElement == null){
			descriptionElement = DocumentHelper.createElement(DocumentHelper.createQName(ATOM_ELEMENT_SUMMARY, ATOM_NAMESPACE));
			itemElement.add(descriptionElement);
		}
		descriptionElement.setText(description);
		return descriptionElement;
	}

	@Override
	public Element addFeedItemLinkElement(Element itemElement, String absoluteLink) {
		Element linkElement = getFeedItemLinkElement(itemElement);
		if(linkElement == null){
			linkElement = DocumentHelper.createElement(DocumentHelper.createQName(ATOM_ELEMENT_LINK, ATOM_NAMESPACE));
			itemElement.add(linkElement);
			linkElement.addAttribute("href", absoluteLink);
		} else {
			Attribute attr = linkElement.attribute("href");
			if(attr == null){
				linkElement.addAttribute("href", absoluteLink);	
			} else {
				attr.setText(absoluteLink);
			}
		}
		return linkElement;
	}

	@Override
	public Element addFeedItemTitleElement(Element itemElement, String title) {
		Element titleElement = getFeedItemTitleElement(itemElement);
		if(titleElement == null){
			titleElement = DocumentHelper.createElement(DocumentHelper.createQName(ATOM_ELEMENT_TITLE, ATOM_NAMESPACE));
			itemElement.add(titleElement);
		}
		titleElement.setText(title);
		return titleElement;
	}

	@Override
	public Element getFeedItemDescriptionElement(Element itemElement) {
		return itemElement.element(ATOM_ELEMENT_SUMMARY);
	}

	@Override
	public Element getFeedItemLinkElement(Element itemElement) {
		return itemElement.element(ATOM_ELEMENT_LINK);
	}

	@Override
	public Element getFeedItemTitleElement(Element itemElement) {
		return itemElement.element(ATOM_ELEMENT_TITLE);
	}

	@Override
	public boolean isFeedItemDescription(Element element) {
		return ATOM_ELEMENT_SUMMARY.equals(element.getName());
	}

	@Override
	public boolean isFeedItemLink(Element element) {
		return ATOM_ELEMENT_LINK.equals(element.getName());
	}

	@Override
	public boolean isFeedItemTitle(Element element) {
		return ATOM_ELEMENT_TITLE.equals(element.getName());
	}

	@Override
	public void addAuthorElement(Element itemElement, String author) {
		Element authorElement = itemElement.element(SX_ELEMENT_AUTHOR);
		if(authorElement == null){
			authorElement = itemElement.addElement(SX_ELEMENT_AUTHOR);
			Element name = authorElement.addElement(SX_ELEMENT_NAME);
			name.setText(author);
		} else {
			Element name = authorElement.element(SX_ELEMENT_NAME);
			if(name == null){
				name = authorElement.addElement(SX_ELEMENT_NAME);
				name.setText(author);
			} else if(!name.getTextTrim().equals(author.trim())){
				name.setText(author);
			}
		}
	}

	@Override
	public String getContentType() {
		return "application/atom+xml";
	}
	
	///<content type="xhtml">
    //<div xmlns="http://www.w3.org/1999/xhtml">
    //<h1>Show Notes</h1>
    //<ul>
    //  <li>00:01:00 -- Introduction</li>
    //  <li>00:15:00 -- Talking about Atom 1.0</li>
    //  <li>00:30:00 -- Wrapping up</li>
   // </ul>
  //</div>
//</content>

	@Override
	public void addFeedItemPayloadElement(Element itemElement, Element payload) {
		
		Element contentElement = itemElement.element(ATOM_ELEMENT_CONTENT);
		if(contentElement == null){
			contentElement = itemElement.addElement(ATOM_ELEMENT_CONTENT);
			contentElement.addAttribute("type", "text");
		}

		String xml = XMLHelper.canonicalizeXML(payload);
		contentElement.add(DocumentHelper.createCDATA(xml));
	}

	@Override
	public Element getFeedItemPayloadElement(Element itemElement) {
		Element contentElement = itemElement.element(ATOM_ELEMENT_CONTENT);
		if(contentElement == null){
			return null;
		}
		
		String dataXml = (String)contentElement.getData();
		dataXml = dataXml.trim();
		if(dataXml.length() > 0 && dataXml.startsWith("<")){
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
		return ATOM_ELEMENT_CONTENT.equals(element.getName());
	}

	@Override
	public boolean isAditionalFeedItemPayload(Element element) {
		return !isFeedItemAuthor(element) 
			&& !isFeedItemPayload(element)
			&& !isFeedItemIdElement(element)
			&& !isFeedItemUpdatedElement(element);
	}

	private boolean isFeedItemUpdatedElement(Element element) {
		return ATOM_ELEMENT_ID.equals(element.getName());
	}

	private boolean isFeedItemIdElement(Element element) {
		return ATOM_ELEMENT_UPDATED.equals(element.getName());
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
		return ATOM_ELEMENT_UPDATED.equals(element.getName());
	}

	private boolean isFeedId(Element element) {
		return ATOM_ELEMENT_ID.equals(element.getName());
	}

	@Override
	public boolean isFeedDescription(Element element) {
		return ATOM_ELEMENT_SUBTITLE.equals(element.getName());
	}

	@Override
	public boolean isFeedLink(Element element) {
		return ATOM_ELEMENT_LINK.equals(element.getName());
	}

	@Override
	public boolean isFeedTitle(Element element) {
		return ATOM_ELEMENT_TITLE.equals(element.getName());
	}

	@Override
	public void addFeedInformation(Element rootElement, String title, String description, String link, Date lastUpdated) {
		addOrUpdateElement(ATOM_ELEMENT_ID, rootElement, link);
		addOrUpdateElement(ATOM_ELEMENT_TITLE, rootElement, title);
		
		if(description != null && description.trim().length() > 0){
			addOrUpdateElement(ATOM_ELEMENT_SUBTITLE, rootElement, description);	
		}
				
		addFeedLinkElement(rootElement, link);
		addOrUpdateElement(ATOM_ELEMENT_UPDATED, rootElement, formatDate(lastUpdated));
	}

	private void addFeedLinkElement(Element rootElement, String absoluteLink) {
		Element linkElement = rootElement.element(ATOM_ELEMENT_LINK);
		if(linkElement == null){
			linkElement = DocumentHelper.createElement(DocumentHelper.createQName(ATOM_ELEMENT_LINK, ATOM_NAMESPACE));
			rootElement.add(linkElement);
			linkElement.addAttribute("href", absoluteLink);
		} else {
			Attribute attr = linkElement.attribute("href");
			if(attr == null){
				linkElement.addAttribute("href", absoluteLink);	
			} else {
				attr.setText(absoluteLink);
			}
		}	
	}

	private void addOrUpdateElement(String elementName, Element rootElement, String text) {
		Element element = rootElement.element(elementName);
		if(element == null){
			element = DocumentHelper.createElement(DocumentHelper.createQName(elementName, ATOM_NAMESPACE));
			rootElement.add(element);
		}
		element.setText(text);			
	}

	
	public String getFeedTitle(Element element){
		return element.getText();
	}
	
	public String getFeedDescription(Element element){
		return element.getText();
	}
	
	public String getFeedLink(Element element){
		return element.attributeValue("href");
	}
	
	public String getFeedItemTitle(Element element){
		return element.getText();
	}
	
	public String getFeedItemDescription(Element element){
		return element.getText();
	}
	
	public String getFeedItemLink(Element element){
		return element.attributeValue("href");
	}
}
