package org.mesh4j.sync.adapters.history;

import org.mesh4j.sync.model.History;

public class HistoryChange {

	// MODEL VARIABLE
	private String syncId;
	private History syncHistory;
	private String payload;
	private HistoryType historyType;

	// BUSINESS METHODS
	public HistoryChange(String syncId, History history, String payload, HistoryType historyType) {
		this.syncId = syncId;
		this.syncHistory = history;
		this.payload = payload;
		this.historyType = historyType;
	}

	public String getSyncId() {
		return this.syncId;
	}

	public HistoryType getHistoryType() {
		return this.historyType;
	}

	public String getPayload() {
		return this.payload;
	}

	public History getSyncHistory() {
		return this.syncHistory;
	}
}