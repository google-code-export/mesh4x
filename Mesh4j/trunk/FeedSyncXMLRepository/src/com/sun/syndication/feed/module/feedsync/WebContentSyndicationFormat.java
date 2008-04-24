package com.sun.syndication.feed.module.feedsync;

import java.util.Date;
import java.util.List;

import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.modules.ConflictItem;

public interface WebContentSyndicationFormat {

	public Date parseDateTime(String dateAsString);	
	public String formatDateTime(Date date);

	public void parseConflictItem(Element root, Element itemElement, ConflictItem item);
	public void generateConflictItem(Element elementRoot, ConflictItem item);
	public List<Element> getConflictItems(Element conflictElement);
}
