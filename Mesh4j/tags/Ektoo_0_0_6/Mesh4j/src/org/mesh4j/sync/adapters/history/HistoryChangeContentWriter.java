package org.mesh4j.sync.adapters.history;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.DateHelper;

public class HistoryChangeContentWriter implements IContentWriter {

	@Override
	public boolean mustWriteSync(Item item) {
		return false;
	}

	@Override
	public void writeContent(ISyndicationFormat syndicationFormat, Element itemElement, Item item) {
		HistoryChange historyChange = FeedHistoryRepository.makeHistoryChange(item.getContent().getPayload());
		
		StringBuffer sb = new StringBuffer();
		sb.append("Action: ");
		sb.append(historyChange.getHistoryType().name());
		sb.append("  by: ");
		sb.append(historyChange.getSyncHistory().getBy());
		sb.append("   when: ");
		sb.append(DateHelper.formatW3CDateTime(historyChange.getSyncHistory().getWhen()));
		sb.append("   sequence: ");
		sb.append(historyChange.getSyncHistory().getSequence());
				
		syndicationFormat.addFeedItemTitleElement(itemElement, sb.toString());
		syndicationFormat.addFeedItemDescriptionElement(itemElement, "history change");
		syndicationFormat.addFeedItemPayloadElement(itemElement, item.getContent().getPayload().createCopy());
		//syndicationFormat.addFeedItemPayloadElement(itemElement, XMLHelper.parseElement(historyChange.getPayload()));
	}

}
