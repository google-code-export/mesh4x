package com.mesh4j.sync.adapters.compound;

import java.util.List;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.SyncInfo;

public interface ISyncRepository {

	void save(SyncInfo syncInfo);

	SyncInfo get(String syncId);

	List<SyncInfo> getAll(String entityName);

	String newSyncID(EntityContent content);
}