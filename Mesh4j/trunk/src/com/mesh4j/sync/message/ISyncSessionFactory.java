package com.mesh4j.sync.message;


public interface ISyncSessionFactory {

	void registerSource(IMessageSyncAdapter source);

	ISyncSession get(String sessionId);
	ISyncSession get(String sourceId, String targetId);

	ISyncSession createSession(String sessionId, String sourceId, IEndpoint target, boolean fullProtocol);
}
