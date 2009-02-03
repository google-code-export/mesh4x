package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.ui.MeshCompactUI;

public class ShutdownTask extends SwingWorker<Void, Void> {
	
	private final static Log Logger = LogFactory.getLog(ShutdownTask.class);
	 
	// MODEL VARIABLEs
	private MeshCompactUI ui;
	
	// BUSINESS METHODS
	public ShutdownTask(MeshCompactUI ui){
		super();
		this.ui = ui;
	}
	
	@Override
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		try{
			ui.getSyncEngine().getChannel().shutdown();
		} catch(Throwable e){
			Logger.error(e.getMessage(), e);
		}
		return null;
    }

	@Override
    public void done() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}

