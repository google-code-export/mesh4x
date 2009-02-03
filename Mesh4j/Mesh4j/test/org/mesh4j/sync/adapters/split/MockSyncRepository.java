package org.mesh4j.sync.adapters.split;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;

public class MockSyncRepository implements ISyncRepository {

	// MODEL VARIABLES
	private HashMap<String, SyncInfo> syncInfos = new HashMap<String, SyncInfo>();
	
	// BUSINESS METHODS
	
	@Override
	public SyncInfo get(String syncId) {
		return this.syncInfos.get(syncId);
	}

	@Override
	public List<SyncInfo> getAll(String entityName) {
		return new ArrayList<SyncInfo>(this.syncInfos.values());
	}

	@Override
	public String newSyncID(IContent content) {
		return IdGenerator.INSTANCE.newID();
	}

	@Override
	public void save(SyncInfo syncInfo) {
		this.syncInfos.put(syncInfo.getSyncId(), syncInfo);
	}

}
