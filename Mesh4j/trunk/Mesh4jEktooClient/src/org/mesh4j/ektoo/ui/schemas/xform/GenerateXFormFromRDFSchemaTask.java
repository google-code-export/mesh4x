package org.mesh4j.ektoo.ui.schemas.xform;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.xform.SchemaToXFormTranslator;

public class GenerateXFormFromRDFSchemaTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(GenerateXFormFromRDFSchemaTask.class);
	
	// MODEL VARIABLES
	private XFormEditorUI ui;
	private IRDFSchema rdfSchema;
	
	// BUSINESS METHODS
	public GenerateXFormFromRDFSchemaTask(XFormEditorUI ui, IRDFSchema rdfSchema){
		this.ui = ui;
		this.rdfSchema = rdfSchema;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{			
			
			String xformXML = SchemaToXFormTranslator.translate(this.rdfSchema);
			
			ui.refresh(xformXML);
			ui.notifyXFormGenerationDone();
		} catch(Throwable t){
			ui.notifyXFormGenerationFailed();
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
