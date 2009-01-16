package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;

public class StartUpTask extends SwingWorker<Void, Void> {
	
	private final static Log Logger = LogFactory.getLog(StartUpTask.class);
	 
	// MODEL VARIABLEs
	private EpiinfoCompactUI ui;
	
	// BUSINESS METHODS
	public StartUpTask(EpiinfoCompactUI ui){
		super();
		this.ui = ui;
	}
	
	@Override
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try{
			if(ui.getSyncEngine() == null){
				ui.notifyStartUpError();
			} else {
				ui.getSyncEngine().getChannel().startUp();	
				ui.notifyStartUpOk();
			}
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

