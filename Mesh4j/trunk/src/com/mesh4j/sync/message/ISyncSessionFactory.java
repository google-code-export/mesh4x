package com.mesh4j.sync.message;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.model.Item;


public interface ISyncSessionFactory {

	void registerSource(IMessageSyncAdapter source);

	ISyncSession get(String sessionId);
	ISyncSession get(String sourceId, String targetId);

	ISyncSession createSession(String sessionId, String sourceId, IEndpoint target, boolean fullProtocol);

	ISyncSession createSession(String sessionId, String sourceId,
		String endpointId, boolean isFull, boolean isOpen, Date date,
		List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot,
		List<String> conflicts, List<String> acks);
}
