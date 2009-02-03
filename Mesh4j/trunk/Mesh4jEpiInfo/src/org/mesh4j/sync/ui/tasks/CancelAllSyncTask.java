package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.ui.MeshCompactUI;

public class CancelAllSyncTask extends SwingWorker<Void, Void> {
	 
	private final static Log LOGGER = LogFactory.getLog(CancelAllSyncTask.class);
	
	// MODEL VARIABLEs
	private MeshCompactUI ui;
	
	// BUSINESS METHODS
	public CancelAllSyncTask(MeshCompactUI ui){
		super();
		this.ui = ui;
	}
	
	@Override
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		ui.fullDisableAllButtons();
		
		List<ISyncSession> syncSessions = ui.getSyncEngine().getAllSyncSessions();
		for (ISyncSession syncSession : syncSessions) {
			if(syncSession.isOpen()){
				
				try{
					ui.getSyncEngine().cancelSync(syncSession.getSourceId(), syncSession.getTarget());
				} catch(Throwable e){
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return null;
    }

	@Override
    public void done() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		ui.fullEnableAllButtons();
    }
}
