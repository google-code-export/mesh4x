package org.mesh4j.ektoo.ui.schemas.xform;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.security.LoggedInIdentityProvider;

public class UploadXFormTask extends SwingWorker<String, Void> {

	private static final Log LOGGER = LogFactory.getLog(UploadXFormTask.class);
	
	// MODEL VARIABLEs
	private XFormEditorUI ui;
	private CloudModel model;
	private IRDFSchema rdfSchema; 
	private String xformXML;

	// BUSINESS METHODS
	public UploadXFormTask(XFormEditorUI ui, CloudModel model, IRDFSchema rdfSchema, String xformXML) {
		super();
		this.model = model;
		this.ui = ui;
		this.rdfSchema = rdfSchema;
		this.xformXML = xformXML;
	}

	@Override
	public String doInBackground() {			
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try{
			HttpSyncAdapter.uploadMeshDefinition(
				model.getBaseUri(), 
				model.getMeshName()+"/"+model.getDatasetName(), 
				RssSyndicationFormat.INSTANCE.getName(), 
				model.getDatasetName(), 
				rdfSchema, 
				model.getMapping(), 
				LoggedInIdentityProvider.getUserName(),
				xformXML);
			ui.notifyUploadDone();
		} catch(Throwable t){
			ui.notifyUploadFailed();
			LOGGER.error(t.getMessage(), t);
			
			MessageDialog.showErrorMessage(ui.getEktooFrame(), t.getMessage());
		}
		return null;
	}

	@Override
	public void done() {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

}
