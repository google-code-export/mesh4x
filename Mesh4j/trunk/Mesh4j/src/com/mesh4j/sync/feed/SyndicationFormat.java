package com.mesh4j.sync.feed;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

public interface SyndicationFormat {

	boolean isFeedItem(Element element);

	List<Element> getRootElements(Element root);

	Date parseDate(String dateAsString);
	String formatDate(Date date);

	Element addRootElement(Document document);
	Element addFeedItemElement(Element root);

}
