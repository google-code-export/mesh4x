package com.mesh4j.sync.message;

import java.util.List;

import com.mesh4j.sync.merge.MergeBehavior;
import com.mesh4j.sync.merge.MergeResult;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.validations.Guard;

public class MessageSyncEngine implements IMessageReceiver {

	// MODEL VARIABLES
	private IMessageSyncProtocol syncProtocol;
	private IChannel channel;

	// METHODS
	public MessageSyncEngine(IMessageSyncProtocol protocol, IChannel channel){
		Guard.argumentNotNull(protocol, "protocol");
		Guard.argumentNotNull(channel, "channel");
		
		this.channel = channel;
		this.channel.registerMessageReceiver(this);
		this.syncProtocol = protocol;
	}
	
	public void synchronize(String sourceId, IEndpoint target) {
		synchronize(sourceId, target, false);
	}
	
	public void synchronize(String sourceId, IEndpoint target, boolean fullProtocol) {
		IMessage message = this.syncProtocol.beginSync(sourceId, target, fullProtocol);
		if(message != null){
			this.channel.send(message);
		}
	}
	
	public void cancelSync(String sourceId, IEndpoint target) {
		IMessage message = this.syncProtocol.cancelSync(sourceId, target);
		if(message != null){
			this.channel.send(message);
		}
	}

	@Override
	public void receiveMessage(IMessage message){
		if(this.syncProtocol.isValidMessageProtocol(message)){
			List<IMessage> response = this.syncProtocol.processMessage(message);
			if(response != IMessageSyncProtocol.NO_RESPONSE){
				for (IMessage msg : response) {
					this.channel.send(msg);	
				}			
			}
		}
	}
	
	public static void merge(ISyncSession syncSession, Item incomingItem) {
		Item originalItem = syncSession.get(incomingItem.getSyncId());
		
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		if (!result.isMergeNone()) {
			Item conflicItem = importItem(result, syncSession);
			if(conflicItem != null){
				syncSession.addConflict(conflicItem);
			}
		}
//		else {   // update is not send
//			List<IMessage> response = new ArrayList<IMessage>();
//			response.add(createMessage(dataSetId, result.getOriginal()));
//			return response;
//		}
	}
	
	private static Item importItem(MergeResult result, ISyncSession syncSession) {
		if (result.getOperation() == null
				|| result.getOperation().isRemoved()) {
			throw new UnsupportedOperationException();
		} else if (result.getOperation().isAdded()) {
			syncSession.add(result.getProposed());
		} else if (result.getOperation().isUpdated()
				|| result.getOperation().isConflict()) {
			syncSession.update(result.getProposed());			// TODO (JMT) MeshSMS: Conflicts, save a conflict?
		}
		if (!result.isMergeNone() && result.getProposed() != null
				&& result.getProposed().hasSyncConflicts()) {
			return result.getProposed();
		}
		return null;
	}
	
	// TODO (JMT) MeshSms: ack and messages retries/timeout - Timer/Quartz
}
