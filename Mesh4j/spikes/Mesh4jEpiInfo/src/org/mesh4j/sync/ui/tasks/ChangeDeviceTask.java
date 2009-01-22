package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class ChangeDeviceTask extends SwingWorker<Void, Void> {
	
	private final static Log Logger = LogFactory.getLog(ChangeDeviceTask.class);

	 
	// MODEL VARIABLEs
	private MeshCompactUI ui;
	
	// BUSINESS METHODS
	public ChangeDeviceTask(MeshCompactUI ui){
		super();
		this.ui = ui;
	}
	
	@Override
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		ui.fullDisableAllButtons();
		ui.shutdownSyncEngine();
		
		try{
			SyncEngineUtil.initializeSmsConnection(ui.getSyncEngine(), ui.getPropertiesProvider());
			ui.startUpSyncEngine();
		} catch(Throwable e){
			ui.notifyStartUpError();
			Logger.error(e.getMessage(), e);
		}
		return null;
    }

	@Override
    public void done() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
