package org.mesh4j.sync.message.protocol;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.IMessageProcessor;

public class MockSyncProtocol implements IMessageSyncProtocol {

	// MODEL VARIABLES
	private IMessageSyncAdapter adapter;
	private ISyncSession session;
	
	// BUSINESS METHODS
	public MockSyncProtocol(IMessageSyncAdapter adapter, ISyncSession session) {
		super();
		this.adapter = adapter;
		this.session = session;
	}
	
	@Override
	public IMessage beginSync(String sourceId, IEndpoint endpoint,
			boolean fullProtocol, boolean shouldSendChanges,
			boolean shouldReceiveChanges) {
		return null;
	}

	@Override
	public IMessage cancelSync(String sourceId, IEndpoint target) {
		return null;
	}

	@Override
	public void cancelSync(ISyncSession syncSession) {
	}

	@Override
	public void endSync(ISyncSession syncSession, Date date) {
	}

	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		return this.adapter;
	}

	@Override
	public ISyncSession getSyncSession(String sourceId, IEndpoint target) {
		return this.session;
	}

	@Override
	public boolean isValidMessageProtocol(IMessage message) {
		return false;
	}

	@Override
	public void notifyBeginSync(ISyncSession syncSession) {

	}

	@Override
	public List<IMessage> processMessage(IMessage message) {
		return null;
	}

	@Override
	public void registerSource(IMessageSyncAdapter adapter) {

	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {

	}

	@Override
	public void registerSyncAware(IMessageSyncAware syncAware) {

	}

	public String getSourceType() {
		return this.adapter.getSourceType();
	}

	@Override
	public List<ISyncSession> getAllSyncSessions() {
		List<ISyncSession> all = new ArrayList<ISyncSession>();
		all.add(this.session);
		return all;
	}

	@Override
	public void removeSourceId(String sourceId) {
		// nothing to do
	}

	@Override
	public IMessageProcessor getMessageProcessor(String messageType) {
		return null;
	}

}
