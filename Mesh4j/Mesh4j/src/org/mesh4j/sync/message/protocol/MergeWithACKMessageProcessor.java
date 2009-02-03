package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class MergeWithACKMessageProcessor implements IMessageProcessor {

	public static final String MESSAGE_TYPE = "6";
	
	// MODEL VARIABLES
	private ACKMergeMessageProcessor ackMessage;
	private IItemEncoding itemEncoding;
	
	// METHODS
	public MergeWithACKMessageProcessor(IItemEncoding itemEncoding, ACKMergeMessageProcessor ackMessage) {
		super();
		this.ackMessage = ackMessage;
		this.itemEncoding = itemEncoding;
	}

	@Override
	public String getMessageType() {
		return MESSAGE_TYPE;
	}
	
	public IMessage createMessage(ISyncSession syncSession, Item item) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(item, "item");
		
		syncSession.waitForAck(item.getSyncId());		
		int[] diffHashCodes = this.getLastSyncDiffsHashCodes(syncSession, item);
		
		StringBuilder sb = new StringBuilder();
		sb.append(syncSession.isFullProtocol() ? "T" : "F");
		sb.append(this.itemEncoding.encode(syncSession, item, diffHashCodes));
		
		return new Message(
				IProtocolConstants.PROTOCOL,
				getMessageType(),
				syncSession.getSessionId(),
				syncSession.getVersion(),
				sb.toString(),
				syncSession.getTarget());
	}
	
	private int[] getLastSyncDiffsHashCodes(ISyncSession syncSession, Item actualItem) {
		if(!actualItem.isDeleted()){
			List<Item> items = syncSession.getSnapshot();
			for (Item item : items) {
				if(item.getSyncId().equals(actualItem.getSyncId())){
					return this.itemEncoding.calculateDiffBlockHashCodes(item.getContent().getPayload());  
				}
			}
		}
		return new int[0];
	}

	@Override
	public List<IMessage> process(ISyncSession syncSession, IMessage message) {

		if(syncSession.isOpen()  && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){
			
			boolean isFullProtocol= message.getData().startsWith("T");

			String itemData = message.getData().substring(1, message.getData().length());

			Item incomingItem = this.itemEncoding.decode(syncSession, itemData);

			MessageSyncEngine.merge(syncSession, incomingItem);

			ArrayList<IMessage> response = new ArrayList<IMessage>();
			response.add(this.ackMessage.createMessage(
				syncSession,
				incomingItem.getSyncId(),
				isFullProtocol));
			return response;
		}
		
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public String getSyncID(String data) {
		String itemData = data.substring(1, data.length());
		return this.itemEncoding.getSyncID(itemData);
	}
}
