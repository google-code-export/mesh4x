package com.sun.syndication.feed.synd.impl.extension;

import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.feed.synd.SyndEntry;

public class ConverterForRSS20 extends
		com.sun.syndication.feed.synd.impl.ConverterForRSS20 {

	public ConverterForRSS20() {
		this("rss_2.0");
	}

	protected ConverterForRSS20(String type) {
		super(type);
	}

	public SyndEntry createSyndEntry(Item item) {
		return super.createSyndEntry(item);
	}
	
	public Item createRSSItem(SyndEntry sEntry){
		return super.createRSSItem(sEntry);
	}

}
