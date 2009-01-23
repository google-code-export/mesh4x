package org.mesh4j.sync.message.core.repository;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.model.Item;


public interface ISyncSessionFactory {

	IMessageSyncAdapter getSource(String sourceId);
	
	IMessageSyncAdapter getSourceOrCreateIfAbsent(String sourceId);
	
	ISyncSession get(String sessionId);
	
	ISyncSession get(String sourceId, String targetId);

	ISyncSession createSession(String sessionId, int version, String sourceId, IEndpoint endpoint, boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges);

	ISyncSession createSession(String sessionId, int version, String sourceId,
			String endpointId, boolean fullProtocol, boolean shouldSendChanges,
			boolean shouldReceiveChanges, boolean isOpen, boolean isBroken, boolean isCancelled,
			Date startDate, Date endDate, Date lastSyncDate, int lastIn, int lastOut, List<Item> currentSyncSnapshot,
			List<Item> lastSyncSnapshot, List<String> conflicts,
			List<String> acks, int numberOfAddedItems,
			int numberOfUpdatedItems, int numberOfDeletedItems,
			String targetSourceType, int targetNumberOfAddedItems,
			int targetNumberOfUpdatedItems, int targetNumberOfDeletedItems);

	void registerSource(IMessageSyncAdapter source);
	
	void registerSourceIfAbsent(IMessageSyncAdapter source);

	List<ISyncSession> getAll();

}
