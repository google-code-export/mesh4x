package com.mesh4j.sync.adapters.feed.atom;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;

import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.utils.DateHelper;

public class AtomSyndicationFormat implements ISyndicationFormat {

	private static final String ATOM_ELEMENT_FEED = "feed";
	private static final String ATOM_ELEMENT_ENTRY = "entry";
	public static final AtomSyndicationFormat INSTANCE = new AtomSyndicationFormat();

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
		rootElement.add(new Namespace(SX_PREFIX, NAMESPACE));
		return rootElement;
	}

	@Override
	public Element addFeedItemElement(Element root) {
		Element item = root.addElement(ATOM_ELEMENT_ENTRY);
		return item;
	}
}
