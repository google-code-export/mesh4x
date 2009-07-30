package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.List;
import java.util.TreeSet;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.MapConfigurationUI;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.mappings.Mapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class MappingsViewTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(MappingsViewTask.class);
	private EktooFrame ui;
	private CloudUIController controller;
	
	public MappingsViewTask(EktooFrame ui, CloudUIController controller){
		this.ui = ui;
		this.controller = controller;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			SyncItemUI itemUI = ui.getSourceItem();
			ISyncAdapter adapter = itemUI.createAdapter();
			List<IRDFSchema> schemas = itemUI.fetchSchema(adapter);
			if(schemas != null){
				TreeSet<String> propertyNames = new TreeSet<String>();
				
				IRDFSchema rdfSchema = schemas.get(0);
				int size = rdfSchema.getPropertyCount();
				for (int i = 0; i < size; i++) {
					propertyNames.add(rdfSchema.getPropertyName(i));
				}
				
				Mapping mapping = controller.getMappings();
				showSchemaInPopup(new MapConfigurationUI(this.ui, this.controller, rdfSchema.getOntologyClassName(), propertyNames, mapping));
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
