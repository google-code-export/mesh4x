package org.mesh4j.sync.adapters.history;

import java.util.List;

public interface IHistoryRepository  {

	void addHistoryChange(HistoryChange historyChange);

	List<HistoryChange> getHistories(String syncId);

}
