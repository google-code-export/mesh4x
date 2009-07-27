package org.mesh4j.sync.message;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.IFilter;
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
	
	Vector<Item> getAll();
	Vector<Item> getAll(IFilter<Item> filter);	
	Vector<Item> getSnapshot();
	Item getSnapshotItem(String syncId);
	
	void beginSync();
	void beginSync(Date sinceDate, int version);
	void endSync(Date sinceDate);
	void cancelSync();
	
	Date getLastSyncDate();
	boolean isOpen();
	boolean isCompleteSync();
	
	void waitForAck(String syncId);
	void notifyAck(String syncId);

	Date createSyncDate();

	boolean isFullProtocol();

	boolean isCancelled();

}
