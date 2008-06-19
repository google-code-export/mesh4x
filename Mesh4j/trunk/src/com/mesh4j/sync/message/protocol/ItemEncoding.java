package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.XMLHelper;

public class ItemEncoding {

	private final static String FIELD_SEPARATOR = "|";
	private final static String HISTORY_SEPARATOR = "#";
	private final static String HISTORY_FIELD_SEPARATOR = "$";

	public static Item decode(ISyncSession syncSession, String encodingItem) {
		
		int xmlPos = 3;
		
		StringTokenizer st = new StringTokenizer(encodingItem, FIELD_SEPARATOR);
		
		Sync sync = null;
		
		String syncID = st.nextToken();
		xmlPos = xmlPos + syncID.length();
		
		Item localItem = syncSession.get(syncID);
		if(localItem == null){
			sync = new Sync(syncID);
		} else {
			sync = localItem.getSync().clone();
		}
		
		boolean deleted = "T".equals(st.nextToken());
		xmlPos = xmlPos + 1;
		
		String historiesString = st.nextToken();
		xmlPos = xmlPos + historiesString.length();
		
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
			String xml = encodingItem.substring(xmlPos, encodingItem.length());
			Element payload = XMLHelper.parseElement(xml);
			XMLContent content = new XMLContent(syncID, "", "", payload);
			return new Item(content, sync);
		}
	}

	public static String encode(ISyncSession syncSession, Item item) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(item.getSyncId());
		sb.append(FIELD_SEPARATOR);
		sb.append(item.getSync().isDeleted() ? "T" : "F");
		sb.append(FIELD_SEPARATOR);
		
		Iterator<History> itHistories = item.getSync().getUpdatesHistory().iterator();
		boolean addHistory = false; 
		while (itHistories.hasNext()) {
			History history = itHistories.next();
			if(syncSession.getLastSyncDate() == null || syncSession.getLastSyncDate().compareTo(history.getWhen()) <= 0){
				addHistory = true;
				sb.append(history.getBy());
				sb.append(HISTORY_FIELD_SEPARATOR);
				sb.append(history.getWhen().getTime());
				if(itHistories.hasNext()){
					sb.append(HISTORY_SEPARATOR);
				}
			}
		}
		
		if(!item.isDeleted()){
			if(addHistory){
				sb.append(FIELD_SEPARATOR);
			}
			sb.append(XMLHelper.canonicalizeXML(item.getContent().getPayload()));
		}

		return sb.toString();
	}

}
