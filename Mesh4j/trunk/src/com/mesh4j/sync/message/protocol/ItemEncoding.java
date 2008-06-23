package com.mesh4j.sync.message.protocol;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.History;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.DiffUtils;
import com.mesh4j.sync.utils.XMLHelper;

public class ItemEncoding implements IItemEncoding, IProtocolConstants{
	
	// MODEL VARIABLES
	private int diffBlockSize = 100;

	// BUSINESS METHODS
	public ItemEncoding(int diffBlockSize) {
		super();
		this.diffBlockSize = diffBlockSize;
	}

	@Override
	public Item decode(ISyncSession syncSession, String encodingItem) {
		
		int xmlPos = 3;
		
		StringTokenizer st = new StringTokenizer(encodingItem, ELEMENT_SEPARATOR);
		
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
		
		StringTokenizer stHistories = new StringTokenizer(historiesString, FIELD_SEPARATOR);
		while(stHistories.hasMoreTokens()){
			String historyString = stHistories.nextToken();
			StringTokenizer stHistory = new StringTokenizer(historyString, SUB_FIELD_SEPARATOR);
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
			String xmlDiff = encodingItem.substring(xmlPos, encodingItem.length());
			HashMap<Integer, String> diffs = new HashMap<Integer, String>();
			
			StringTokenizer stDiffs = new StringTokenizer(xmlDiff, FIELD_SEPARATOR);
			while(stDiffs.hasMoreTokens()){
				String blockDiff = stDiffs.nextToken();
				String[] blockDiffs = blockDiff.split(SUB_FIELD_SEPARATOR);
				diffs.put(Integer.valueOf(blockDiffs[0]), blockDiffs[1]);
			}
			
			String xml = localItem == null ? "" : XMLHelper.canonicalizeXML(localItem.getContent().getPayload());
			
			String xmlResult = DiffUtils.appliesDiff(xml, this.diffBlockSize, diffs);
			Element payload = XMLHelper.parseElement(xmlResult);
			XMLContent content = new XMLContent(syncID, "", "", payload);
			return new Item(content, sync);
		}
	}

	@Override
	public String encode(ISyncSession syncSession, Item item, int[] diffHashCodes) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(item.getSyncId());
		sb.append(ELEMENT_SEPARATOR);
		sb.append(item.getSync().isDeleted() ? "T" : "F");
		sb.append(ELEMENT_SEPARATOR);
		
		Iterator<History> itHistories = item.getSync().getUpdatesHistory().iterator();
		boolean addHistory = false; 
		while (itHistories.hasNext()) {
			History history = itHistories.next();
			if(syncSession.getLastSyncDate() == null || syncSession.getLastSyncDate().compareTo(history.getWhen()) <= 0){
				addHistory = true;
				sb.append(history.getBy());
				sb.append(SUB_FIELD_SEPARATOR);
				sb.append(history.getWhen().getTime());
				if(itHistories.hasNext()){
					sb.append(FIELD_SEPARATOR);
				}
			}
		}
		
		if(!item.isDeleted()){
			if(addHistory){
				sb.append(ELEMENT_SEPARATOR);
			}
			String xml = XMLHelper.canonicalizeXML(item.getContent().getPayload());
			
			Map<Integer, String> diffs = DiffUtils.obtainsDiff(xml, this.diffBlockSize, diffHashCodes);
			Iterator<Integer> it = diffs.keySet().iterator();
			while(it.hasNext()) {
				int i = it.next();
				sb.append(i);
				sb.append(SUB_FIELD_SEPARATOR);
				sb.append(diffs.get(i));
				if(it.hasNext()){
					sb.append(FIELD_SEPARATOR);
				}
			}
		}

		return sb.toString();
	}

	@Override
	public int[] calculateDiffBlockHashCodes(String xml) {
		return DiffUtils.calculateBlockHashCodes(xml, this.diffBlockSize);
	}

}
