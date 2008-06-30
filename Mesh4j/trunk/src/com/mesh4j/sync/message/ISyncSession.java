package com.mesh4j.sync.message;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.model.Item;

public interface ISyncSession {
	
	String getSessionId();
	
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
	void beginSync(Date sinceDate);
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

}
