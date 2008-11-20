package org.mesh4j.sync.adapters.feed.rss;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.utils.DateHelper;


public class RssSyndicationFormat implements ISyndicationFormat {

	public static final String NAME = "rss20";
	public static final String RSS_ELEMENT_ROOT = "rss";
	public static final String RSS_ELEMENT_ITEM = "item";
	public static final String RSS_ELEMENT_CHANNEL = "channel";
	public static final String FEED_ITEM_ELEMENT_TITLE = "title";
	public static final String FEED_ITEM_ELEMENT_DESCRIPTION = "description";
	public static final String FEED_ITEM_ELEMENT_LINK = "link";
	public static final RssSyndicationFormat INSTANCE = new RssSyndicationFormat();

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
		return dateAsString == null ? null : DateHelper.parseRFC822(dateAsString);
	}
	
	@Override
	public String formatDate(Date date) {
		return date == null ? "" : DateHelper.formatRFC822(date);
	}


	@Override
	public Element addRootElement(Document document) {
		Element rootElement = document.addElement(RSS_ELEMENT_ROOT);
		rootElement.add(new Namespace(SX_PREFIX, NAMESPACE));
		rootElement.addAttribute("version", "2.0");
		Element channel = rootElement.addElement(RSS_ELEMENT_CHANNEL);
		return channel;
	}

	@Override
	public Element addFeedItemElement(Element root) {
		Element item = root.addElement(RSS_ELEMENT_ITEM);
		return item;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void addFeedInformation(Element rootElement, String title, String description, String link) {
		
		Element titleElement = addFeedItemTitleElement(rootElement);
		titleElement.setText(title);
		
		Element contentElement = addFeedItemDescriptionElement(rootElement);
		contentElement.setText(description);
		
		Element linkElement = addFeedItemLinkElement(rootElement);
		linkElement.setText(link);

	}


	@Override
	public Element addFeedItemDescriptionElement(Element itemElement) {
		Element element = DocumentHelper.createElement(FEED_ITEM_ELEMENT_DESCRIPTION);
		itemElement.add(element);
		return element;
	}

	@Override
	public Element addFeedItemLinkElement(Element itemElement) {
		Element element = DocumentHelper.createElement(FEED_ITEM_ELEMENT_LINK);
		itemElement.add(element);
		return element;
	}

	@Override
	public Element addFeedItemTitleElement(Element itemElement) {
		Element element = DocumentHelper.createElement(FEED_ITEM_ELEMENT_TITLE);
		itemElement.add(element);
		return element;
	}

	@Override
	public Element getFeedItemDescriptionElement(Element itemElement) {
		return itemElement.element(FEED_ITEM_ELEMENT_DESCRIPTION);
	}

	@Override
	public Element getFeedItemLinkElement(Element itemElement) {
		return itemElement.element(FEED_ITEM_ELEMENT_LINK);
	}

	@Override
	public Element getFeedItemTitleElement(Element itemElement) {
		return itemElement.element(FEED_ITEM_ELEMENT_TITLE);
	}

	@Override
	public boolean isFeedItemDescription(Element element) {
		return FEED_ITEM_ELEMENT_DESCRIPTION.equals(element.getName());
	}

	@Override
	public boolean isFeedItemLink(Element element) {
		return FEED_ITEM_ELEMENT_LINK.equals(element.getName());
	}

	@Override
	public boolean isFeedItemTitle(Element element) {
		return FEED_ITEM_ELEMENT_TITLE.equals(element.getName());
	}

}
