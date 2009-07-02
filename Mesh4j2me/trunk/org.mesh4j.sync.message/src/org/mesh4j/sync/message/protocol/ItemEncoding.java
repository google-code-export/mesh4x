package org.mesh4j.sync.message.protocol;

import java.util.Date;

import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.utils.DiffUtils;
import org.mesh4j.sync.utils.XmlHelper;

import de.enough.polish.util.HashMap;
import de.enough.polish.util.Map;
import de.enough.polish.util.StringTokenizer;


public class ItemEncoding implements IItemEncoding, IProtocolConstants{
	
	// MODEL VARIABLES
	private int diffBlockSize = 100;

	// BUSINESS METHODS
	public ItemEncoding(int diffBlockSize) {
		super();
		this.diffBlockSize = diffBlockSize;
	}


	public Item decode(ISyncSession syncSession, String encodingItem) {
		
		StringTokenizer st = new StringTokenizer(encodingItem, ELEMENT_SEPARATOR);
		
		String header = st.nextToken();
			
		String syncID = header.substring(0, 36);
		boolean deleted = "T".equals(header.substring(36, 37));
		
		Sync sync = new Sync(syncID);
		
		Item localItem = syncSession.get(syncID);
		if(localItem != null){
			int size =  localItem.getSync().getUpdatesHistory().size();
			for (int i = 0; i < size; i++) {
				History history =  localItem.getSync().getUpdatesHistory().elementAt(i);
				if(syncSession.getLastSyncDate() == null || syncSession.getLastSyncDate().getTime() > history.getWhen().getTime()){
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
			HashMap diffs = new HashMap();
			
			StringTokenizer stDiffs = new StringTokenizer(xmlDiff, FIELD_SEPARATOR);
			while(stDiffs.hasMoreTokens()){
				String blockDiff = stDiffs.nextToken();
				String index = blockDiff.substring(0, 1);
				String textDiff = blockDiff.substring(1, blockDiff.length());
				diffs.put(Integer.valueOf(index), textDiff);
			}
			
			String xml = localItem == null ? "" : XmlHelper.canonicalizeXML(localItem.getContent().getPayload());
			
			String payload = DiffUtils.appliesDiff(xml, this.diffBlockSize, diffs);
			//String xmlResult = DiffUtils.appliesDiff(xml, this.diffBlockSize, diffs);
			//Element payload = XmlHelper.parseElement(xmlResult);
			XMLContent content = new XMLContent(syncID, "", "", "", payload);
			return new Item(content, sync);
		}
	}


	public String encode(ISyncSession syncSession, Item item, int[] diffHashCodes) {
		StringBuilder sb = new StringBuilder();
		sb.append(item.getSyncId());
		sb.append(item.getSync().isDeleted() ? "T" : "F");
		
		boolean addHistory = false; 
		int size =  item.getSync().getUpdatesHistory().size();
		for (int i = 0; i < size; i++) {
			History history =  item.getSync().getUpdatesHistory().elementAt(i);
			if(syncSession.getLastSyncDate() == null || syncSession.getLastSyncDate().getTime() <= history.getWhen().getTime()){
				addHistory = true;				
				sb.append(history.getWhen().getTime());
				sb.append(history.getBy());
				if(i < size){
					sb.append(FIELD_SEPARATOR);
				}
			}
		}
		
		if(!item.isDeleted()){
			if(addHistory){
				sb.append(ELEMENT_SEPARATOR);
			}
			String xml = XmlHelper.canonicalizeXML(item.getContent().getPayload());
			
			Map diffs = DiffUtils.obtainsDiff(xml, this.diffBlockSize, diffHashCodes);
			Integer i = null;
			int diffsSize = diffs.keys().length;
			for (int j = 0; j < diffsSize; j++) {
				i = (Integer)diffs.keys()[j];
				sb.append(i);
				sb.append(diffs.get(i));
				if(j < diffsSize){
					sb.append(FIELD_SEPARATOR);
				}
			}
		}

		return sb.toString();
	}


	public int[] calculateDiffBlockHashCodes(String xml) {
		return DiffUtils.calculateBlockHashCodes(xml, this.diffBlockSize);
	}

	public static String getSyncID(String data){
		StringTokenizer st = new StringTokenizer(data, ELEMENT_SEPARATOR);
		
		String header = st.nextToken();
			
		String syncID = header.substring(0, 36);
		return syncID;
	}
}
