package com.sun.syndication.feed.module.feedsync.modules;

import java.util.List;

import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.WebContentSyndicationFormat;
import com.sun.syndication.feed.synd.SyndEntry;

public class ConflictItem {

	// MODEL VARIABLES
	private SyndEntry syndEntry;

	// BUSINESS METHODS
	public static void parse(Element syncRoot,
			WebContentSyndicationFormat webContentSyndicationFormat,
			SyncModule sync) {
		{
			Element conflictElement = syncRoot.getChild("conflicts",
					FeedSyncModule.NAMESPACE);
			if (conflictElement != null) {
				List<Element> itemElements = webContentSyndicationFormat
						.getConflictItems(conflictElement);
				for (Element itemElement : itemElements) {
					Element root = getRoot(itemElement);
					ConflictItem conflictItem = new ConflictItem();
					webContentSyndicationFormat.parseConflictItem(root, itemElement, conflictItem);
					sync.addConflict(conflictItem);
				}
			}
		}
	}

	private static Element getRoot(Element start) {
		Element root;
		for (root = start; root.getParent() != null
				&& (root.getParent() instanceof Element); root = (Element) root
				.getParent())
			;
		return root;
	}

	public static void generate(Element elementRoot, WebContentSyndicationFormat webContentSyndicationFormat, ConflictItem conflictItem) {
		webContentSyndicationFormat.generateConflictItem(elementRoot, conflictItem);
	}

	public SyndEntry getSyndEntry() {
		return syndEntry;
	}

	public void setSyndEntry(SyndEntry syndEntry) {
		this.syndEntry = syndEntry;
	}

}
