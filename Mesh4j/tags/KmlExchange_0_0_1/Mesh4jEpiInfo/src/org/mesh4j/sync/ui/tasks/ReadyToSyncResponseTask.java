package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class ReadyToSyncResponseTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private MeshCompactUI ui;
	private String endpoint;
	private String dataSourceAlias;
	private boolean isDataSourceAvailable;
	
	// BUSINESS METHODS
	public ReadyToSyncResponseTask(MeshCompactUI ui, String endpoint, String dataSourceAlias, boolean isDataSourceAvailable){
		super();
		this.ui = ui;
		this.endpoint = endpoint;
		this.dataSourceAlias = dataSourceAlias;
		this.isDataSourceAvailable = isDataSourceAvailable;
	}
	
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		String message = ReadyToSyncTask.makeAnswer(dataSourceAlias, isDataSourceAvailable);
		SyncEngineUtil.sendSms(ui.getSyncEngine(), endpoint, message);
		return null;
    }

	@Override
    public void done() {
		super.done();
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
