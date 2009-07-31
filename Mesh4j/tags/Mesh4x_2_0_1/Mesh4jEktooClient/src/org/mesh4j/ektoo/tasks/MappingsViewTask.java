package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.controller.AbstractUIController;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.MapConfigurationUI;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class MappingsViewTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(MappingsViewTask.class);
	private EktooFrame ui;
	private AbstractUIController controller;
	private boolean useSourceRDF = false;
	
	public MappingsViewTask(EktooFrame ui, AbstractUIController controller, boolean useSourceRDF){
		this.ui = ui;
		this.controller = controller;
		this.useSourceRDF = useSourceRDF;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			List<IRDFSchema> schemas = null;
			Mapping mapping = null;
			
			controller.setCurrentEvent(Event.mappings_view_event);
			
			String comments = "";
			ISyncAdapter syncAdapter = controller.createAdapter();
			if(syncAdapter instanceof HttpSyncAdapter){
				String url = HttpSyncAdapter.makeKmlURL((((HttpSyncAdapter)syncAdapter).getURL()));
				comments = EktooUITranslator.getMessageURLFroMapAvailable(url);
			}
			
			ISyncAdapter adapter = syncAdapter;
			
			schemas = controller.fetchSchema(adapter);
			mapping = controller.getMapping();
			
			if(schemas == null){
				if(useSourceRDF){
					AbstractUIController sourceController = ui.getSourceItem().getCurrentController();
					if(sourceController != controller){
						sourceController.setCurrentEvent(Event.mappings_view_event);
						
						syncAdapter = null;
						adapter = sourceController.createAdapter();
						schemas = sourceController.fetchSchema(adapter);
						
						if(mapping == null){
							mapping = sourceController.getMapping();
						}
					}
				}
			} 
			
			
			if(schemas != null){
				IRDFSchema rdfSchema = schemas.get(0);
				showSchemaInPopup(new MapConfigurationUI(this.ui, this.controller, rdfSchema, mapping, syncAdapter, comments));
			}
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
	

	private void showSchemaInPopup(MapConfigurationUI ui){
		this.ui.showViewInPopup(EktooUITranslator.getMapConfigurationWindowTitle(), ui, ui.getWidth(), ui.getHeight(), false);
	}
	
}
