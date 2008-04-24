package com.sun.syndication.feed.module.feedsync;

import java.util.Date;
import java.util.List;

import org.jdom.Element;

import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.module.feedsync.modules.ConflictItem;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.impl.extension.ConverterForAtom10;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedGenerator;
import com.sun.syndication.io.WireFeedParser;
import com.sun.syndication.io.impl.DateParser;
import com.sun.syndication.io.impl.extension.Atom10Generator;
import com.sun.syndication.io.impl.extension.Atom10Parser;

public class FeedSyncAtom10 extends FeedSyncParser {

	// MODEL VARIABLES
	private Atom10Parser atomParser;
	private Atom10Generator parentGenerator;
	private ConverterForAtom10 converter = new ConverterForAtom10();

	// BUSINESS METHODS
	public FeedSyncAtom10() {
	}

	public void setFeedParser(WireFeedParser feedParser) {
		this.atomParser = (Atom10Parser) feedParser;
	}

	public boolean isSharingRoot(Element element) {
		return "feed".equals(element.getName());
	}
	
	public boolean isSyncRoot(Element element){
		return "entry".equals(element.getName());
	}
	
	public void setFeedGenerator(WireFeedGenerator feedGenerator) {
		this.parentGenerator = (Atom10Generator) feedGenerator;
	}

	public Date parseDateTime(String dateAsString) {
		return dateAsString == null ? null : DateParser.parseW3CDateTime(dateAsString);
	}

	public String formatDateTime(Date date) {
		return date == null ? "" : DateParser.formatW3CDateTime(date);
	}

	public void parseConflictItem(Element root, Element itemElement, ConflictItem conflictItem) {
		try {
			Entry entry = atomParser.parseEntry(root, itemElement);
			//conflictItem.setEntry(entry);
			SyndEntry syndEntry = converter.createSyndEntry(entry);
			conflictItem.setSyndEntry(syndEntry);
		} catch (FeedException e) {
			// TODO LOG
			e.printStackTrace();
		}
	}
	
	public List<Element> getConflictItems(Element conflictElement) {
		return conflictElement.getChildren("entry", atomParser.getAtomNamespace());
	}
	
	public void generateConflictItem(Element elementRoot, ConflictItem conflictItem) {
        try {
			Entry entry = converter.createAtomEntry(conflictItem.getSyndEntry());
        	Element itemElement = new Element("entry");
			parentGenerator.populateEntry(entry, itemElement);
			parentGenerator.generateItemModules(entry.getModules(), itemElement);
			elementRoot.addContent(itemElement);
		} catch (FeedException e) {
			// TODO Log
			e.printStackTrace();
		}
	}
}
