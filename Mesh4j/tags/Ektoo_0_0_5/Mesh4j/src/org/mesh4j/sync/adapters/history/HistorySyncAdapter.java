package org.mesh4j.sync.adapters.history;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

public class HistorySyncAdapter implements ISyncAdapter, ISyncAware {

	// MODEL VARIABLES
	private ISyncAdapter adapter;
	private IHistoryRepository historyRepository;
	
	// BUSINESS METHODS
	
	public HistorySyncAdapter(ISyncAdapter syncAdapter, IHistoryRepository historyRepository) {
		Guard.argumentNotNull(syncAdapter, "syncAdapter");
		Guard.argumentNotNull(historyRepository, "historyRepository");
		this.adapter = syncAdapter;
		this.historyRepository = historyRepository;
	}

	@Override
	public void add(Item item) {
		this.adapter.add(item);
		
		HistoryChange historyChange = new HistoryChange(item.getSyncId(), item.getLastUpdate(), item.getContent().getPayload().asXML(), HistoryType.ADD);
		this.historyRepository.addHistoryChange(historyChange);
	}

	@Override
	public void delete(String id) {
		Item item = this.get(id);
		if(item != null){
			HistoryChange historyChange = new HistoryChange(item.getSyncId(), item.getLastUpdate(), item.getContent().getPayload().asXML(), HistoryType.DELETE);
			this.historyRepository.addHistoryChange(historyChange);
		}
		this.adapter.delete(id);
	}

	@Override
	public Item get(String id) {
		return this.adapter.get(id);
	}

	@Override
	public List<Item> getAll() {
		return this.adapter.getAll();
	}

	@Override
	public List<Item> getAll(IFilter<Item> filter) {
		return this.adapter.getAll(filter);
	}

	@Override
	public List<Item> getAllSince(Date since) {
		return this.adapter.getAllSince(since);
	}

	@Override
	public List<Item> getAllSince(Date since, IFilter<Item> filter) {
		return this.adapter.getAllSince(since, filter);
	}

	@Override
	public List<Item> getConflicts() {
		return this.adapter.getConflicts();
	}

	@Override
	public String getFriendlyName() {
		return this.adapter.getFriendlyName();
	}

	@Override
	public void update(Item item) {
		this.adapter.update(item);
		
		HistoryChange historyChange = new HistoryChange(item.getSyncId(), item.getLastUpdate(), item.getContent().getPayload().asXML(), HistoryType.UPDATE);
		this.historyRepository.addHistoryChange(historyChange);

	}

	@Override
	public void update(Item item, boolean resolveConflicts) {
		this.adapter.update(item, resolveConflicts);
		
		HistoryChange historyChange = new HistoryChange(item.getSyncId(), item.getLastUpdate(), item.getContent().getPayload().asXML(), HistoryType.UPDATE);
		this.historyRepository.addHistoryChange(historyChange);
	}

	@Override
	public void beginSync() {
		if(this.adapter instanceof ISyncAware){
			((ISyncAware)this.adapter).beginSync();
		}
	}

	@Override
	public void endSync() {
		if(this.adapter instanceof ISyncAware){
			((ISyncAware)this.adapter).endSync();
		}		
	}
	
	public List<HistoryChange> getHistories(String syncId) {
		return this.historyRepository.getHistories(syncId);
	}
}
