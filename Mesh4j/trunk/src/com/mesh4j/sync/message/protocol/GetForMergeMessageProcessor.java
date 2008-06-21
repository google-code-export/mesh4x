package com.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.mesh4j.sync.diff.Diff;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.core.IMessageProcessor;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.utils.XMLHelper;

public class GetForMergeMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private MergeMessageProcessor mergeMessage;
	
	// METHODS	
	
	public GetForMergeMessageProcessor(MergeMessageProcessor mergeMessage) {
		super();
		this.mergeMessage = mergeMessage;
	}

	@Override
	public String getMessageType() {
		return "4";
	}
	
	public IMessage createMessage(ISyncSession syncSession, String syncID) {
		Item item = syncSession.get(syncID);
		return basicCreateMessage(syncSession, syncID, item);
	}
	
	public IMessage createMessage(ISyncSession syncSession, Item item) {
		return basicCreateMessage(syncSession, item.getSyncId(), item);
	}
	
	private IMessage basicCreateMessage(ISyncSession syncSession, String syncID, Item item) {
		syncSession.waitForAck(syncID);
		String data = encode(syncID, item);
		
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				data,
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && this.getMessageType().equals(message.getMessageType())){						
			String syncId = decodeSyncID(message.getData());
			int[] diffHashCodes = decodeDiffHashCodes(message.getData());
				
			List<IMessage> response = new ArrayList<IMessage>();
			response.add(this.mergeMessage.createMessage(syncSession, syncId, diffHashCodes));
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}
	
	private String encode(String syncID, Item item) {
		if(item == null){
			return syncID;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(syncID);
			sb.append(IProtocolConstants.ELEMENT_SEPARATOR);
			
			String xml = XMLHelper.canonicalizeXML(item.getContent().getPayload());
			
			Diff diff = new Diff();
			int[] hashs = diff.calculateBlockHashCodes(xml, 100);
			for (int j = 0; j < hashs.length; j++) {
				sb.append(hashs[j]);
				if(j != hashs.length){
					sb.append(IProtocolConstants.FIELD_SEPARATOR);
				}
			}
			return sb.toString();
		}
	}
	
	private int[] decodeDiffHashCodes(String data) {
		StringTokenizer st = new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		st.nextToken();  // Skip syncID
		if(st.hasMoreTokens()){
			String diffHashCodesString = st.nextToken();
			String[] diffHashCodes = diffHashCodesString.split(IProtocolConstants.FIELD_SEPARATOR);
			int[] hashs = new int[diffHashCodes.length];
			
			for (int i = 0; i < diffHashCodes.length; i++) {
				hashs[i] = Integer.valueOf(diffHashCodes[i]);
			}
			return hashs;
		} else{
			return new int[0];
		}
	}

	private String decodeSyncID(String data) {
		StringTokenizer st = new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		return st.nextToken();
	}
}
