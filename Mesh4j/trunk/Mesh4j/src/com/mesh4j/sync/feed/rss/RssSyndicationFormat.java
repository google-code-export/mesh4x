package com.mesh4j.sync.feed.rss;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.feed.SyndicationFormat;
import com.mesh4j.sync.utils.DateHelper;

public class RssSyndicationFormat implements SyndicationFormat {

	public static final RssSyndicationFormat INSTANCE = new RssSyndicationFormat();

	@SuppressWarnings("unchecked")
	@Override
	public List<Element> getRootElements(Element root) {
		return root.element("channel").elements();
	}

	@Override
	public boolean isFeedItem(Element element) {
		return "item".equals(element.getName());
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
		Element rootElement = document.addElement("rss");
		rootElement.add(new Namespace("sx", "http://www.microsoft.com/schemas/sse"));
		rootElement.addAttribute("version", "2.0");
		Element channel = rootElement.addElement("channel");
		return channel;
	}

	@Override
	public Element addFeedItemElement(Element root) {
		Element item = root.addElement("item");
		return item;
	}

}
