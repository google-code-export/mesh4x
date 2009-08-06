package org.mesh4j.ektoo.ui.conflicts;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.List;

import javax.swing.SwingWorker;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.merge.MergeBehavior;
import org.mesh4j.sync.model.Item;

public class ResolveConflictsTask extends SwingWorker<String, Void> {

	// MODEL VARIABLEs
	private ConflictsUI ui;
	private ISyncAdapter adapter;
	private HashMap<String, Item> currentWinners;
	private List<Item> chooseWinners;

	// BUSINESS METHODS
	public ResolveConflictsTask(ConflictsUI ui, ISyncAdapter adapter, HashMap<String, Item> currentWinners, List<Item> chooseWinners) {
		super();
		this.ui = ui;
		this.adapter = adapter;
		this.currentWinners = currentWinners;
		this.chooseWinners = chooseWinners;
	}

	@Override
	public String doInBackground() {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));		
		MergeBehavior.resolveConflicts(adapter, currentWinners, chooseWinners, ui);
		return null;
	}

	@Override
	public void done() {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
