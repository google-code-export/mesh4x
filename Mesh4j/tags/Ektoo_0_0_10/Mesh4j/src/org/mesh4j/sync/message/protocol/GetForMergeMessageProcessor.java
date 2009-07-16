package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class GetForMergeMessageProcessor implements IMessageProcessor {

	public static final String MESSAGE_TYPE = "4";
	
	// MODEL VARIABLES
	private MergeMessageProcessor mergeMessage;
	private IItemEncoding itemEncoding;
	
	// METHODS	
	
	public GetForMergeMessageProcessor(IItemEncoding itemEncoding, MergeMessageProcessor mergeMessage) {
		super();
		this.mergeMessage = mergeMessage;
		this.itemEncoding = itemEncoding;
	}

	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}
	
	public IMessage createMessage(ISyncSession syncSession, String syncID) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNullOrEmptyString(syncID, "syncID");
		
		Item item = syncSession.get(syncID);
		return basicCreateMessage(syncSession, syncID, item);
	}
	
	public IMessage createMessage(ISyncSession syncSession, Item item) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(item, "item");
		return basicCreateMessage(syncSession, item.getSyncId(), item);
	}
	
	private IMessage basicCreateMessage(ISyncSession syncSession, String syncID, Item item) {
		syncSession.waitForAck(syncID);
		String data = encode(syncID, item);
		
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				data,
				syncSession.getTarget());
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {
		
		if(syncSession.isOpen() && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){						
			String syncId = getSyncID(message.getData());
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
			
			int[] hashs = this.itemEncoding.calculateDiffBlockHashCodes(item.getContent().getPayload());
			for (int j = 0; j < hashs.length; j++) {
				sb.append(hashs[j]);
				if(j+1 != hashs.length){
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

	public static String getSyncID(String data) {
		StringTokenizer st = new StringTokenizer(data, IProtocolConstants.ELEMENT_SEPARATOR);
		return st.nextToken();
	}
}
