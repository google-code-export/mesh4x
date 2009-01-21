package org.mesh4j.sync.message;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.model.Item;


public interface ISyncSession {
	
	String getSessionId();
	int getVersion();
	
	String getSourceId();
	String getSourceType();
	
	IEndpoint getTarget();
	
	Item get(String syncId);
	void add(Item item);
	void update(Item item);
	void delete(String syncID, String by, Date when);
	
	boolean hasConflict(String syncId);
	void addConflict(String syncID);
	void addConflict(Item conflicItem);
	
	List<Item> getAll();

	void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges);
	void beginSync(boolean fullProtocol, boolean shouldSendChanges, boolean shouldReceiveChanges, Date sinceDate, int version, String targetSourceType);
	void endSync(Date sinceDate);
	void cancelSync();
	
	Date getLastSyncDate();
	
	boolean isOpen();
	boolean isCompleteSync();
	boolean isCancelled();
	boolean isBroken();	
	
	List<Item> getSnapshot();
	
	void waitForAck(String syncId);
	void notifyAck(String syncId);

	Date createSyncDate();

	List<Item> getCurrentSnapshot();

	List<String> getAllPendingACKs();

	List<String> getConflictsSyncIDs();

	boolean isFullProtocol();
	boolean shouldSendChanges();
	boolean shouldReceiveChanges();
	
	int getNumberOfAddedItems();
	int getNumberOfUpdatedItems();
	int getNumberOfDeletedItems();

	String getTargetSourceType();
	void setTargetSorceType(String targetSourceType);
	
	int getTargetNumberOfAddedItems();
	void setTargetNumberOfAddedItems(int added);
	
	int getTargetNumberOfUpdatedItems();
	void setTargetNumberOfUpdatedItems(int updated);
	
	int getTargetNumberOfDeletedItems();
	void setTargetNumberOfDeletedItems(int deleted);
	
	void setBroken();
	
}
