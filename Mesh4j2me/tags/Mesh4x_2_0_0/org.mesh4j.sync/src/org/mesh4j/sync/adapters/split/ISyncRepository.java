package org.mesh4j.sync.adapters.split;

import java.util.Vector;

import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.model.IContent;


public interface ISyncRepository {

	void save(SyncInfo syncInfo);

	SyncInfo get(String syncId);

	Vector<SyncInfo> getAll(String entityName);

	String newSyncID(IContent content);

	void deleteAll();
}