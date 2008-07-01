package com.mesh4j.sync.message.core.repository;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessageSyncAdapter;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.model.Item;

public interface ISyncSessionFactory {

	void registerSource(IMessageSyncAdapter source);
	
	IMessageSyncAdapter getSource(String sourceId);
	
	ISyncSession get(String sessionId);
	
	ISyncSession get(String sourceId, String targetId);

	ISyncSession createSession(String sessionId, String sourceId, IEndpoint endpoint, boolean fullProtocol);

	ISyncSession createSession(String sessionId, String sourceId,
		String endpointId, boolean isFull, boolean isOpen, Date date,
		List<Item> currentSyncSnapshot, List<Item> lastSyncSnapshot,
		List<String> conflicts, List<String> acks);

}
