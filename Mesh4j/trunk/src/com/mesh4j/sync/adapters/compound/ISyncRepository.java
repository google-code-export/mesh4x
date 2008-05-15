package com.mesh4j.sync.adapters.compound;

import java.util.List;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.model.IContent;

public interface ISyncRepository {

	void save(SyncInfo syncInfo);

	SyncInfo get(String syncId);

	List<SyncInfo> getAll(String entityName);

	String newSyncID(IContent content);
}