package org.mesh4j.sync.message.core;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.core.repository.SyncSessionFactory;

public class MockSyncSessionRepository implements ISyncSessionRepository {

	private SyncSessionFactory factory;
	
	public MockSyncSessionRepository() {
		this.factory = new SyncSessionFactory();
	}
	
	public MockSyncSessionRepository(SyncSessionFactory factory) {
		this.factory = factory;
	}

	@Override
	public ISyncSession createSession(String sessionID, int version, String sourceId, IEndpoint target,
			boolean fullProtocol) {
		return factory.createSession(sessionID, version, sourceId, target, fullProtocol);
	}

	@Override
	public void flush(ISyncSession syncSession) {}

	@Override
	public void snapshot(ISyncSession syncSession) {}

	@Override
	public void cancel(ISyncSession syncSession) {}

	@Override
	public ISyncSession getSession(String sessionId) {
		return factory.get(sessionId);
	}

	@Override
	public ISyncSession getSession(String sourceId, String endpointId) {
		return factory.get(sourceId, endpointId);
	}

	@Override
	public void registerSourceIfAbsent(IMessageSyncAdapter adapter) {
		factory.registerSourceIfAbsent(adapter);
	}

	@Override
	public IMessageSyncAdapter getSource(String sourceId) {
		return factory.getSource(sourceId);
	}
}
