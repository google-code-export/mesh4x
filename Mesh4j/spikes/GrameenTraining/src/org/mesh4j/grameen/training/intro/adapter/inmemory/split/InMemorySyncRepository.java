package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.List;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.model.IContent;

public class InMemorySyncRepository implements ISyncRepository , ISyncAware{

	@Override
	public SyncInfo get(String syncId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SyncInfo> getAll(String entityName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String newSyncID(IContent content) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(SyncInfo syncInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beginSync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSync() {
		// TODO Auto-generated method stub
		
	}

}
