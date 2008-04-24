package com.sun.syndication.feed.module.feedsync.modules;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

import com.sun.syndication.feed.module.feedsync.WebContentSyndicationFormat;

public class History {

	// CONSTANS
	private static final long serialVersionUID = 7123272084159399934L;
	
	// MODEL VARIABLES
	private int sequence;
	private String when;
	private String by;
		
	// BUSINESS METHODs
	
	private int getSequence() {
		return sequence;
	}

	private void setSequence(int sequence) {
		this.sequence = sequence;
	}

	private String getWhen() {
		return when;
	}

	private void setWhen(String when) {
		this.when = when;
	}

	private String getBy() {
		return by;
	}

	private void setBy(String by) {
		this.by = by;
	}

	
	public static void parse(Element root,
			WebContentSyndicationFormat webContentSyndicationFormat,
			SyncModule sync) {
		{
			List<Element> historyElements = root.getChildren("history", FeedSyncModule.NAMESPACE);
			for (Element element : historyElements) {
				Attribute sequenceAttribute = element.getAttribute("sequence");
				Attribute whenAttribute = element.getAttribute("when");
				Attribute byAttribute = element.getAttribute("by");
				
				History history = new History();
				history.setSequence(sequenceAttribute == null ? 1 : Integer.parseInt(sequenceAttribute.getValue().trim()));
				history.setWhen(whenAttribute == null ? null : whenAttribute.getValue().trim());
				history.setBy(byAttribute == null ? null : byAttribute.getValue().trim());
				sync.addHistory(history);
			}
		}
	}
	
	public static void generate(Element elementRoot, WebContentSyndicationFormat webContentSyndicationFormat, History history) {
		Element element = new Element("history", FeedSyncModule.NAMESPACE);
		element.setAttribute("sequence", String.valueOf(history.getSequence()));
		element.setAttribute("when",  history.getWhen() == null ? "" : history.getWhen());
		element.setAttribute("by",  history.getBy() == null ? "" : history.getBy());
		elementRoot.addContent(element);
	}

}
