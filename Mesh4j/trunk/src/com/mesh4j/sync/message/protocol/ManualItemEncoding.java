package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.XMLHelper;

public class ManualItemEncoding implements IItemEncoding {

	private final static String FIELD_SEPARATOR = "|";
	private final static String HISTORY_SEPARATOR = "#";
	private final static String HISTORY_FIELD_SEPARATOR = "$";

	@Override
	public Item decode(String encodingItem) {
		StringTokenizer st = new StringTokenizer(encodingItem, FIELD_SEPARATOR);
		
		String syncID = st.nextToken();
		Sync sync = new Sync(syncID);
		
		boolean deleted = Boolean.valueOf(st.nextToken());
		
		String historiesString = st.nextToken();
		StringTokenizer stHistories = new StringTokenizer(historiesString, HISTORY_SEPARATOR);
		while(stHistories.hasMoreTokens()){
			String historyString = stHistories.nextToken();
			StringTokenizer stHistory = new StringTokenizer(historyString, HISTORY_FIELD_SEPARATOR);
			String by = stHistory.nextToken();
			long datetime = Long.parseLong(stHistory.nextToken());
			Date when = new Date(datetime);
			if(stHistories.hasMoreTokens()){
				sync.update(by, when, false);
			} else {
				sync.update(by, when, deleted);
			}
		}
		
		if(deleted){
			return new Item(new NullContent(syncID), sync);
		} else {
			String xml = st.nextToken();
			Element payload = XMLHelper.parseElement(xml);
			XMLContent content = new XMLContent(syncID, "", "", payload);
			return new Item(content, sync);
		}
	}

	@Override
	public String encode(Item item) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(item.getSyncId());
		sb.append(FIELD_SEPARATOR);
		sb.append(item.getSync().isDeleted());
		sb.append(FIELD_SEPARATOR);
		
		Iterator<History> itHistories = item.getSync().getUpdatesHistory().iterator();
		while (itHistories.hasNext()) {
			History history = itHistories.next();
			sb.append(history.getBy());
			sb.append(HISTORY_FIELD_SEPARATOR);
			sb.append(history.getWhen().getTime());
			if(itHistories.hasNext()){
				sb.append(HISTORY_SEPARATOR);
			}
		}
		
		if(!item.isDeleted()){
			sb.append(FIELD_SEPARATOR);
			sb.append(XMLHelper.canonicalizeXML(item.getContent().getPayload()));
		}

		return sb.toString();
	}

}
