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
	private ISyncSessionFactory syncSessionFactory;

	// METHODS
	public MessageSyncEngine(IMessageSyncProtocol protocol, IChannel channel, ISyncSessionFactory sessionFactory){
		Guard.argumentNotNull(protocol, "protocol");
		Guard.argumentNotNull(channel, "channel");
		Guard.argumentNotNull(sessionFactory, "sessionFactory");
		
		this.channel = channel;
		this.channel.registerMessageReceiver(this);
		this.syncProtocol = protocol;
		this.syncSessionFactory = sessionFactory;
	}
	
	public void synchronize(String sourceId, IEndpoint target) {
		ISyncSession syncSession = this.syncSessionFactory.get(sourceId, target.getEndpointId());
		if(syncSession != null && syncSession.isOpen()){
			Guard.throwsException("ERROR_MESSAGE_SYNC_SESSION_IS_OPEN", sourceId, target.getEndpointId());
		}
		if(syncSession == null){
			syncSession = this.syncSessionFactory.createSession(sourceId, target);
		}
		IMessage message = this.syncProtocol.beginSync(syncSession);
		if(message != null){
			this.channel.send(message);
		}
	}
	
	public void cancelSynchronization(String sourceId, IEndpoint target) {
		ISyncSession syncSession = this.syncSessionFactory.get(sourceId, target.getEndpointId());
		if(syncSession == null || !syncSession.isOpen()){
			Guard.throwsException("ERROR_MESSAGE_SYNC_SESSION_IS_NOT_OPEN", sourceId, target.getEndpointId());
		}
		IMessage message = this.syncProtocol.cancelSync(syncSession);
		if(message != null){
			this.channel.send(message);
		}
	}

	@Override
	public void receiveMessage(IMessage message){
		if(this.syncProtocol.isValidMessageProtocol(message)){
			ISyncSession syncSession = this.syncSessionFactory.get(message.getSourceId(), message.getEndpointId());
			if(syncSession == null){
				syncSession = this.syncSessionFactory.createSession(message.getSourceId(), message.getEndpoint());
			}
			List<IMessage> response = this.syncProtocol.processMessage(syncSession, message);
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
				syncSession.addConflict(incomingItem.getSyncId());
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
			syncSession.update(result.getProposed());
		}
		if (!result.isMergeNone() && result.getProposed() != null
				&& result.getProposed().hasSyncConflicts()) {
			return result.getProposed();
		}
		return null;
	}
	
	// TODO (JMT) MeshSms: ack and messages retries/timeout - Timer/Quartz
	// TODO (JMT) MeshSms: sync session id in messages?
}
