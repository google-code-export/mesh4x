package com.mesh4j.sync.adapters.feed.rss;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.utils.DateHelper;

public class RssSyndicationFormat implements ISyndicationFormat {

	public static final String RSS_ELEMENT_ROOT = "rss";
	public static final String RSS_ELEMENT_ITEM = "item";
	public static final String RSS_ELEMENT_CHANNEL = "channel";
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

}
