package com.mesh4j.sync.adapters.feed.atom;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.utils.DateHelper;

public class AtomSyndicationFormat implements ISyndicationFormat {

	public static final AtomSyndicationFormat INSTANCE = new AtomSyndicationFormat();

	@SuppressWarnings("unchecked")
	@Override
	public List<Element> getRootElements(Element root) {
		return root.elements();
	}

	@Override
	public boolean isFeedItem(Element element) {
		return "entry".equals(element.getName());
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
		Element rootElement = document.addElement("feed");
		rootElement.add(new Namespace("sx", "http://www.microsoft.com/schemas/sse"));
		return rootElement;
	}

	@Override
	public Element addFeedItemElement(Element root) {
		Element item = root.addElement("entry");
		return item;
	}
}
