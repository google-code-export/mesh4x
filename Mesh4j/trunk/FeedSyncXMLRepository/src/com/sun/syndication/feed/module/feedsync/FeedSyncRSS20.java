package com.sun.syndication.feed.module.feedsync;

import java.util.Date;
import java.util.List;

import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.modules.ConflictItem;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.impl.extension.ConverterForRSS20;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.RSS20Generator;
import com.sun.syndication.io.impl.RSS20Parser;

public class FeedSyncRSS20 extends FeedSyncParser {

	// MODEL VARIABLES
	private RSS20Parser rssParser;
	private RSS20Generator parentGenerator;
	private	ConverterForRSS20 converter = new ConverterForRSS20();

	// BUSINESS METHODS
	public FeedSyncRSS20(){}
	
	public void setFeedParser(WireFeedParser feedParser) {
		this.rssParser = (RSS20Parser) feedParser;		
	}

    public boolean isSharingRoot(Element element)
    {
     	return "channel".equals(element.getName());
    }
	
	public boolean isSyncRoot(Element element){
		return "item".equals(element.getName());
	}
	    
	public void setFeedGenerator(WireFeedGenerator feedGenerator) {
		this.parentGenerator = (RSS20Generator) feedGenerator;
	}

	public Date parseDateTime(String dateAsString) {
		return dateAsString == null ? null : DateParser.parseRFC822(dateAsString);
	}

	public String formatDateTime(Date date) {
		return date == null ? "" :DateParser.formatRFC822(date);
	}

	@Override
	public void parseConflictItem(Element root, Element itemElement, ConflictItem conflictItem) {
		Item item = rssParser.parseItem(root, itemElement);
		SyndEntry syndEntry = converter.createSyndEntry(item);
		conflictItem.setSyndEntry(syndEntry);
	}

	@Override
	public void generateConflictItem(Element elementRoot, ConflictItem conflictItem) {
        Item item = converter.createRSSItem(conflictItem.getSyndEntry());		
        Element itemElement = new Element("item");        
        parentGenerator.populateItem(item, itemElement, 0);
        parentGenerator.generateItemModules(item.getModules(), itemElement);
        elementRoot.addContent(itemElement);		
	}

	@Override
	public List<Element> getConflictItems(Element conflictElement) {
		return conflictElement.getChildren("item");
	}
    
 }
