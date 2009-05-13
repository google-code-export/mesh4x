package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.utils.DiffUtils;
import org.mesh4j.sync.utils.XMLHelper;


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
		
		StringTokenizer st = new StringTokenizer(encodingItem, ELEMENT_SEPARATOR);
		
		String header = st.nextToken();
			
		String syncID = header.substring(0, 36);
		
		boolean deleted = "T".equals(header.substring(36, 37));
		
		Sync sync = new Sync(syncID);
		
		Item localItem = syncSession.get(syncID);
		if(localItem != null){
			ArrayList<History> newHistory = new ArrayList<History>();
			newHistory.addAll(localItem.getSync().getUpdatesHistory());
			Iterator<History> itLocalHistories = newHistory.iterator();
			while(itLocalHistories.hasNext()){
				History history = itLocalHistories.next();
				if(syncSession.getLastSyncDate() == null || syncSession.getLastSyncDate().compareTo(history.getWhen()) > 0){
					sync.update(history.getBy(), history.getWhen());
				}
			}
		}
		
		String historiesString = header.substring(37, header.length());
		
		StringTokenizer stHistories = new StringTokenizer(historiesString, FIELD_SEPARATOR);
		while(stHistories.hasMoreTokens()){
			String historyString = stHistories.nextToken();
			long datetime = Long.parseLong(historyString.substring(0, 13));
			String by = historyString.substring(13, historyString.length());
			
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
			String xmlDiff = encodingItem.substring(header.length() + 1, encodingItem.length());
			HashMap<Integer, String> diffs = new HashMap<Integer, String>();
			
			StringTokenizer stDiffs = new StringTokenizer(xmlDiff, FIELD_SEPARATOR);
			while(stDiffs.hasMoreTokens()){
				String blockDiff = stDiffs.nextToken();
				String[] elements = blockDiff.split(SUB_FIELD_SEPARATOR);
				String index = elements[0];
				String textDiff = "";
				if(elements.length == 2){
					textDiff = elements[1];
				}
				diffs.put(Integer.valueOf(index), textDiff);
			}
			
			String xml = localItem == null ? "" : XMLHelper.canonicalizeXML(localItem.getContent().getPayload());
			
			String xmlResult = DiffUtils.appliesDiff(xml, this.diffBlockSize, diffs);
			Element payload = XMLHelper.parseElement(xmlResult);
			XMLContent content = new XMLContent(syncID, "", "", "", payload);
			return new Item(content, sync);
		}
	}

	@Override
	public String encode(ISyncSession syncSession, Item item, int[] diffHashCodes) {
		StringBuilder sb = new StringBuilder();
		sb.append(item.getSyncId());
		sb.append(item.getSync().isDeleted() ? "T" : "F");
		
		Iterator<History> itHistories = item.getSync().getUpdatesHistory().iterator();
		boolean addHistory = false; 
		while (itHistories.hasNext()) {
			History history = itHistories.next();
			if(syncSession.getLastSyncDate() == null || syncSession.getLastSyncDate().compareTo(history.getWhen()) <= 0){
				addHistory = true;				
				sb.append(history.getWhen().getTime());
				sb.append(history.getBy());
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

	@Override
	public int[] calculateDiffBlockHashCodes(Element element) {
		String xml = XMLHelper.canonicalizeXML(element);
		return DiffUtils.calculateBlockHashCodes(xml, this.diffBlockSize);
	}

	@Override
	public String getSyncID(String data) {
		StringTokenizer st = new StringTokenizer(data, ELEMENT_SEPARATOR);
		String header = st.nextToken();			
		return header.substring(0, 36);
	}

}
