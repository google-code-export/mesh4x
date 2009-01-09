package org.mesh4j.sync.message;

import java.util.List;

import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


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
		
		if(channel instanceof IMessageSyncAware){
			this.syncProtocol.registerSyncAware((IMessageSyncAware) channel);
		}
	}
	
	public void synchronize(IMessageSyncAdapter adapter, IEndpoint target) {
		synchronize(adapter, target, false);
	}
	
	public void synchronize(IMessageSyncAdapter adapter, IEndpoint target, boolean fullProtocol) {
		this.registerSourceIfAbsent(adapter);
		IMessage message = this.syncProtocol.beginSync(adapter.getSourceId(), target, fullProtocol, true, true);
		if(message != null){
			this.channel.send(message);
		}
	}
	
	public void synchronize(IMessageSyncAdapter adapter, IEndpoint target, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges) {
		this.registerSourceIfAbsent(adapter);
		IMessage message = this.syncProtocol.beginSync(adapter.getSourceId(), target, fullProtocol, shouldSendChanges, shouldReceiveChanges);
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
					msg.setOrigin(message.getOrigin());
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
	
	public ISyncSession getSyncSession(String sourceId, IEndpoint target){
		return this.syncProtocol.getSyncSession(sourceId, target);
	}

	public void registerSource(IMessageSyncAdapter adapter) {
		this.syncProtocol.registerSource(adapter);
	}
	
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		this.syncProtocol.registerSourceIfAbsent(adapter);
	}

	public IChannel getChannel() {
		return this.channel;
	}

	public IMessageSyncAdapter getSource(String sourceId) {
		return this.syncProtocol.getSource(sourceId);
	}
	
	public IMessageSyncProtocol getSyncProtocol(){
		return this.syncProtocol;
	}
}
