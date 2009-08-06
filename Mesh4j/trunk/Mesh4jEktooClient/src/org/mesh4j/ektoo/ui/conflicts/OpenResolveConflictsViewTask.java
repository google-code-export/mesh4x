package org.mesh4j.ektoo.ui.conflicts;

import java.awt.Cursor;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class OpenResolveConflictsViewTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(OpenResolveConflictsViewTask.class);
	
	// MODEL VARIABLES
	private EktooFrame ui;
	private AbstractUIController controller;
	
	// BUSINESS METHODS
	public OpenResolveConflictsViewTask(EktooFrame ui, AbstractUIController controller){
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
			
			IRDFSchema rdfSchema = null;
			if(schemas != null && schemas.size() == 1){
				rdfSchema = schemas.get(0);
			}
			
			showSchemaInPopup(new ConflictsUI(this.ui, adapter, rdfSchema));
			
		} catch(Throwable t){
			LOGGER.error(t.getMessage(), t);
			MessageDialog.showErrorMessage(ui, t.getLocalizedMessage());
		}
		return null;
	}
	
	@Override
	public void done(){
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void showSchemaInPopup(ConflictsUI ui){
		this.ui.showViewInPopup(EktooUITranslator.getConflictWindowTitle(), ui, ui.getWidth(), ui.getHeight(), false);
	}
	
}
