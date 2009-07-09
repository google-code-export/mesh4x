package org.mesh4j.ektoo.tasks;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Event;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.SchemaComparisonViewUI;
import org.mesh4j.ektoo.ui.component.RoundBorder;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;

public class SchemaComparisonViewTask extends SwingWorker<String, Void>{

	private static Log logger = LogFactory.getLog(SchemaComparisonViewTask.class);
	private EktooFrame ui;
	private IErrorListener errorListener;
	

	public SchemaComparisonViewTask(EktooFrame ui,IErrorListener errorListener){
		this.ui = ui;
		this.errorListener = errorListener;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		processSchemaComparison();
		return null;
	}
	
	@Override
	public void done(){
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	 
	
	private void processSchemaComparison(){
		
	try{
		ui.getSourceItem().getCurrentController().setCurrentEvent(Event.schema_view_event);
		ISyncAdapter sourceAdapter = ui.getSourceItem().getCurrentController().createAdapter();
		if(sourceAdapter == null){
			MessageDialog.showErrorMessage(ui, 
					EktooUITranslator.getMessageSchemaComparisonViewErrorInSourceAdapterCreation());
		}
		List<IRDFSchema> sourceSchemaList = ui.getSourceItem().getCurrentController().fetchSchema(sourceAdapter);
		if(sourceSchemaList == null){
			MessageDialog.showErrorMessage(ui, 
					EktooUITranslator.getMessageSchemaViewErrorSchemaNotFoundInSource());
		}
		ui.getTargetItem().getCurrentController().setCurrentEvent(Event.schema_view_event);
		ISyncAdapter targetAdapter = ui.getTargetItem().getCurrentController().createAdapter();
		if(targetAdapter == null){
			MessageDialog.showErrorMessage(ui, 
					EktooUITranslator.getMessageSchemaComparisonViewErrorInTargetAdapterCreation());
		}
		List<IRDFSchema> targetSchemaList = ui.getTargetItem().getCurrentController().fetchSchema(targetAdapter);
		if(targetSchemaList == null){
			MessageDialog.showErrorMessage(ui, 
					EktooUITranslator.getMessageSchemaViewErrorSchemaNotFoundInTarget());
		}
		
		JPanel schemaComparisonPanel = new JPanel(new GridLayout(sourceSchemaList.size(),1));
		boolean isCompatibleEntityFound = false;
		for(IRDFSchema sourceSchema : sourceSchemaList){
			String sourceEntity = sourceSchema.getOntologyClassName();
			for(IRDFSchema targetSchema : targetSchemaList){
				String targetEntity = targetSchema.getOntologyClassName();
				if(sourceEntity.equals(targetEntity)){
					SchemaComparisonViewUI comparisonViewUI = new SchemaComparisonViewUI(sourceSchema,targetSchema);
					comparisonViewUI.setBorder(BorderFactory.createTitledBorder( new RoundBorder(Color.LIGHT_GRAY), 
							sourceEntity));
					schemaComparisonPanel.add(comparisonViewUI);
					isCompatibleEntityFound = true;
					break;
				}
			}
		}
		
		if(isCompatibleEntityFound){
			showSchemaComparisonInPopUP(schemaComparisonPanel);
		} else {
			MessageDialog.showWarningMessage(this.ui, EktooUITranslator.getWarningMessageForUnEqualSourceTargetEntityName());
		}
		} catch(Exception ec){
			logger.error(ec);
			errorListener.notifyError(EktooUITranslator.getErrorOnShowingSchemaComparison());
		}
	}
	
	private void showSchemaComparisonInPopUP(JPanel schemaComparisonUI){
		this.ui.showViewInPopup(EktooUITranslator.getTitleOfSchemaViewComparisonPopUp(),schemaComparisonUI);
	}
}
