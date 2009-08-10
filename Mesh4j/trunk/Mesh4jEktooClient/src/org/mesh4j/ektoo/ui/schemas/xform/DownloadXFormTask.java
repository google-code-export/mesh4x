package org.mesh4j.ektoo.ui.schemas.xform;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;

public class DownloadXFormTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(DownloadXFormTask.class);
	
	// MODEL VARIABLES
	private XFormEditorUI ui;
	private String serverURL;
	private String meshGroup;
	private String dataSet;
	
	
	// BUSINESS METHODS
	public DownloadXFormTask(XFormEditorUI ui, String serverUrl, String meshGroup, String dataSet){
		this.ui = ui;
		this.serverURL = serverUrl;
		this.meshGroup = meshGroup;
		this.dataSet = dataSet;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{			
			
			String xformXML = HttpSyncAdapter.getXForm(serverURL, meshGroup, dataSet);
			
			ui.refresh(xformXML);
			ui.notifyDownloadDone();
		} catch(Throwable t){
			ui.notifyDownloadFailed();
			LOGGER.error(t.getMessage(), t);
			
			MessageDialog.showErrorMessage(ui.getEktooFrame(), t.getMessage());
		}
		return null;
	}
	
	@Override
	public void done(){
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
		
}
