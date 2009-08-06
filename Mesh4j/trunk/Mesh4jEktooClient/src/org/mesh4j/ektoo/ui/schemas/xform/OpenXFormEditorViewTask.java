package org.mesh4j.ektoo.ui.schemas.xform;

import java.awt.Cursor;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.model.CloudModel;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class OpenXFormEditorViewTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(OpenXFormEditorViewTask.class);
	
	// MODEL VARIABLES
	private EktooFrame ui;
	private CloudUIController controller;
	
	// BUSINESS METHODS
	public OpenXFormEditorViewTask(EktooFrame ui, CloudUIController controller){
		this.ui = ui;
		this.controller = controller;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			List<IRDFSchema> schemas = null;
			
			controller.setCurrentEvent(Event.mappings_view_event);
		
			ISyncAdapter adapter = controller.createAdapter();
						
			schemas = controller.fetchSchema(adapter);
			controller.getMapping(); // fecth and initialize mappings in model
			
			IRDFSchema rdfSchema = null;
			if(schemas != null && schemas.size() == 1){
				rdfSchema = schemas.get(0);
			}
			
			CloudModel model = (CloudModel)controller.getModel();
			
			String xformXML = HttpSyncAdapter.getXForm(model.getUri());
			
			showSchemaInPopup(new XFormEditorUI(this.ui, model, rdfSchema, xformXML));
			
		} catch(Throwable t){
			LOGGER.error(t.getMessage(), t);
			MessageDialog.showErrorMessage(ui, t.getMessage());
		}
		return null;
	}
	
	@Override
	public void done(){
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void showSchemaInPopup(XFormEditorUI ui){
		this.ui.showViewInPopup(EktooUITranslator.getXFormEditorTitle(), ui, ui.getWidth(), ui.getHeight(), false);
	}
	
}
