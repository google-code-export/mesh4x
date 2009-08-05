package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.sync.ISupportMerge;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;

public class ResolveConflictsTask extends SwingWorker<String, Void> {

	private final static Log LOGGER = LogFactory.getLog(ResolveConflictsTask.class);

	// MODEL VARIABLEs
	private EktooFrame ui;
	private ISyncAdapter adapter;
	private HashMap<String, Item> currentWinners;
	private List<Item> chooseWinners;

	// BUSINESS METHODS
	public ResolveConflictsTask(EktooFrame ui, ISyncAdapter adapter, HashMap<String, Item> currentWinners, List<Item> chooseWinners) {
		super();
		this.ui = ui;
		this.adapter = adapter;
		this.currentWinners = currentWinners;
		this.chooseWinners = chooseWinners;
	}

	@Override
	public String doInBackground() {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		ArrayList<Item> items = new ArrayList<Item>();
		
		if(adapter instanceof ISyncAware){
			((ISyncAware) adapter).beginSync();
		}
		
		for (Item winner : chooseWinners) {
			try{
				Item current = this.currentWinners.get(winner.getSyncId());
				if(winner.equals(current)){
					Item resultItem = MergeBehavior.resolveConflicts(winner, winner.getLastUpdate().getBy(), new Date(), winner.isDeleted());
					items.add(resultItem);
				} else {
					Sync sync = current.getSync().clone();
					IContent content = winner.getContent().clone();
					
					Item item = new Item(content, sync);
					
					Item resultItem = MergeBehavior.resolveConflicts(item, winner.getLastUpdate().getBy(), new Date(), winner.isDeleted());
					items.add(resultItem);
				}
			}catch (Throwable e) {
				LOGGER.error(e.getMessage(), e);
			}
		}

		if(adapter instanceof ISupportMerge){
			((ISupportMerge) adapter).merge(items);
		}else{
			for (Item item : items) {
				adapter.update(item);
			}
		}
		
		if(adapter instanceof ISyncAware){
			((ISyncAware) adapter).endSync();
		}
		return null;
	}

	@Override
	public void done() {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		ui.closePopupViewWindow();
	}

}
