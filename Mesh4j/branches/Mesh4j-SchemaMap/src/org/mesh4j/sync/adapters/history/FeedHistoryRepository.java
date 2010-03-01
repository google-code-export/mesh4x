package org.mesh4j.sync.adapters.history;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.utils.DateHelper;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class FeedHistoryRepository implements IHistoryRepository{

	// MODEL VARIABLES
	private FeedAdapter feedAdapter;
	
	// BUINESS METHODS
	
	public FeedHistoryRepository(String title, String description, String link, String fileName, IIdentityProvider identityProvider, IIdGenerator idGenerator, ISyndicationFormat syndicationFormat){
		Guard.argumentNotNull(title, "title");
		Guard.argumentNotNull(description, "description");
		Guard.argumentNotNull(link, "link");
		
		Feed feed = new Feed(title, description, link);
		this.feedAdapter = new FeedAdapter(fileName, identityProvider, idGenerator, syndicationFormat, feed);
	}
	
	@Override
	public void addHistoryChange(HistoryChange historyChange) {
		Element payload = makeElement(historyChange);
		String syncId = IdGenerator.INSTANCE.newID();
		Sync sync = new Sync(syncId, this.feedAdapter.getAuthenticatedUser(), new Date(), false);
		IContent content = new XMLContent(syncId, "", "", payload);
		Item item = new Item(content, sync);
		this.feedAdapter.add(item);		
	}

	@Override
	public List<HistoryChange> getHistories(final String syncId) {
		
		List<Item> items = this.getHistoryItems(syncId);
		
		ArrayList<HistoryChange> changes = new ArrayList<HistoryChange>();
		for (Item item : items) {
			changes.add(makeHistoryChange(item.getContent().getPayload()));
		}
		return changes;
	}

	public static HistoryChange makeHistoryChange(Element historyChangeElement) {
		String syncId = historyChangeElement.attributeValue("syncId");
		HistoryType type = HistoryType.valueOf(historyChangeElement.attributeValue("type"));
		String payload = ((Element)historyChangeElement.elements().get(0)).asXML();
		String by = historyChangeElement.attributeValue("by");
		Date when = DateHelper.parseW3CDateTime(historyChangeElement.attributeValue("when"));
		int sequence = Integer.valueOf(historyChangeElement.attributeValue("sequence"));
		History history = new History(by, when, sequence);
		return new HistoryChange(syncId, history, payload, type);
	}

	private Element makeElement(HistoryChange historyChange) {
		String xml = asXML(historyChange);
		return XMLHelper.parseElement(xml);
	}
	
	private String asXML(HistoryChange historyChange){
		
		StringBuffer sb = new StringBuffer();
		sb.append("<HistoryChange syncId=\"");
		sb.append(historyChange.getSyncId());
		sb.append("\" type=\"");
		sb.append(historyChange.getHistoryType().name());
		sb.append("\" by=\"");
		sb.append(historyChange.getSyncHistory().getBy());
		sb.append("\" when=\"");
		sb.append(DateHelper.formatW3CDateTime(historyChange.getSyncHistory().getWhen()));
		sb.append("\" sequence=\"");
		sb.append(historyChange.getSyncHistory().getSequence());
		sb.append("\">");
		sb.append(historyChange.getPayload());
		sb.append("</HistoryChange>");
		return sb.toString();
	}

	public List<Item> getHistoryItems(final String syncId) {
		IFilter<Item> filter = new IFilter<Item>(){
			@Override
			public boolean applies(Item obj) {
				HistoryChange hc = makeHistoryChange(obj.getContent().getPayload());
				return hc.getSyncId().endsWith(syncId);
			}
		};
		List<Item> items = this.feedAdapter.getAll(filter);
		return items;
	}
}
