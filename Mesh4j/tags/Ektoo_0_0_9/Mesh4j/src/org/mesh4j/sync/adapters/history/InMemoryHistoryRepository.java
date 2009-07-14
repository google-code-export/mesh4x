package org.mesh4j.sync.adapters.history;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryRepository implements IHistoryRepository {

	private HashMap<String, List<HistoryChange>> historyChanges = new HashMap<String, List<HistoryChange>>();

	@Override
	public void addHistoryChange(HistoryChange historyChange) {
		List<HistoryChange> changes = this.historyChanges.get(historyChange.getSyncId());
		if(changes == null){
			changes = new ArrayList<HistoryChange>();
			this.historyChanges.put(historyChange.getSyncId(), changes);
		}
		changes.add(historyChange);
	}

	@Override
	public List<HistoryChange> getHistories(String syncId) {
		return this.historyChanges.get(syncId);
	}
}
