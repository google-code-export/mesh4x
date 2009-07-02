package org.mesh4j.sync.message.protocol;

import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.MessageSyncEngine;
import org.mesh4j.sync.message.core.IMessageProcessor;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.utils.XmlHelper;
import org.mesh4j.sync.validations.Guard;


public class MergeWithACKMessageProcessor implements IMessageProcessor {

	// CONSTANTS 
	public final static String MESSAGE_TYPE = "6";
	
	// MODEL VARIABLES
	private ACKMergeMessageProcessor ackMessage;
	private IItemEncoding itemEncoding;
	
	// METHODS
	public MergeWithACKMessageProcessor(IItemEncoding itemEncoding, ACKMergeMessageProcessor ackMessage) {
		super();
		this.ackMessage = ackMessage;
		this.itemEncoding = itemEncoding;
	}


	public String getMessageType() {
		return MESSAGE_TYPE;
	}
	
	public IMessage createMessage(ISyncSession syncSession, Item item) {
		Guard.argumentNotNull(syncSession, "syncSession");
		Guard.argumentNotNull(item, "item");
		
		syncSession.waitForAck(item.getSyncId());		
		int[] diffHashCodes = this.getLastSyncDiffsHashCodes(syncSession, item);
		
		String itemEncode = this.itemEncoding.encode(syncSession, item, diffHashCodes);
		StringBuilder sb = new StringBuilder();
		sb.append(syncSession.isFullProtocol() ? "T" : "F");
		sb.append(itemEncode);

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
			Item item = syncSession.getSnapshotItem(actualItem.getSyncId());
			if(item != null){
				String xml = XmlHelper.canonicalizeXML(item.getContent().getPayload());
				return this.itemEncoding.calculateDiffBlockHashCodes(xml);  
			}
		}
		return new int[0];
	}


	public Vector<IMessage> process(ISyncSession syncSession, IMessage message) {
		if(syncSession.isOpen()  && syncSession.getVersion() == message.getSessionVersion() && this.getMessageType().equals(message.getMessageType())){

			boolean isFullProtocol= message.getData().startsWith("T");
			
			String itemData = message.getData().substring(1, message.getData().length());
			
			Item incomingItem = this.itemEncoding.decode(syncSession, itemData);

			MessageSyncEngine.merge(syncSession, incomingItem);
			
			Vector<IMessage> response = new Vector<IMessage>();
			response.addElement(this.ackMessage.createMessage(
				syncSession, 
				incomingItem.getSyncId(),
				isFullProtocol));
			return response;
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}
}
