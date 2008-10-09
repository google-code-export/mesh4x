package org.mesh4j.sync.message;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.model.Item;


public interface ISyncSession {
	
	String getSessionId();
	int getVersion();
	
	String getSourceId();
	IEndpoint getTarget();
	
	Item get(String syncId);
	void add(Item item);
	void update(Item item);
	void delete(String syncID, String by, Date when);
	
	boolean hasChanged(String syncID);
	boolean hasConflict(String syncId);
	void addConflict(String syncID);
	void addConflict(Item conflicItem);
	
	List<Item> getAll();

	void beginSync();
	void beginSync(Date sinceDate, int version);
	void endSync(Date sinceDate);
	void cancelSync();
	
	Date getLastSyncDate();
	boolean isOpen();
	boolean isCompleteSync();
	List<Item> getSnapshot();
	
	void waitForAck(String syncId);
	void notifyAck(String syncId);

	Date createSyncDate();

	boolean isFullProtocol();

	List<Item> getCurrentSnapshot();

	List<String> getAllPendingACKs();

	List<String> getConflictsSyncIDs();

	boolean isCancelled();

}
