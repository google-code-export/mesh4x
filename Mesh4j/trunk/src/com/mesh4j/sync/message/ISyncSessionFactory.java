package com.mesh4j.sync.message;


public interface ISyncSessionFactory {

	void registerSource(IMessageSyncAdapter source);
	
	ISyncSession createSession(String sourceId, IEndpoint target);

	ISyncSession get(String sourceId, String targetId);

}
