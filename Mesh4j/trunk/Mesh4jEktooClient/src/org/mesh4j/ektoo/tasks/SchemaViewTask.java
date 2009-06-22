package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.SchemaViewUI;
import org.mesh4j.ektoo.ui.SyncItemUI;
import org.mesh4j.ektoo.ui.component.PopupDialog;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class SchemaViewTask extends SwingWorker<String, Void>{

	private static Log Logger = LogFactory.getLog(SchemaViewTask.class);
	private EktooFrame ui;
	private SyncItemUI syncItemUI;
	private IErrorListener errorListener;
	
	public SchemaViewTask(EktooFrame ui,SyncItemUI syncItemUI,IErrorListener errorListener){
		this.ui = ui;
		this.syncItemUI = syncItemUI;
		this.errorListener = errorListener;
	}
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		HashMap<IRDFSchema , String> shcemaList = syncItemUI.fetchSchema(syncItemUI.createAdapter());
		for(Map.Entry<IRDFSchema, String>  entry :  shcemaList.entrySet()){
			showSchemaInPopup(entry.getKey());
		}
		return null;
	}
	@Override
	public void done(){
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void showSchemaInPopup(IRDFSchema schema){
		SchemaViewUI schemaViewUI = new SchemaViewUI(schema);
		PopupDialog dialog = new PopupDialog(this.ui,"schema");
		dialog.add(schemaViewUI);
		dialog.setSize(ui.getWidth() - 100, ui.getHeight()/2);
		dialog.setVisible(true);
	}
}
