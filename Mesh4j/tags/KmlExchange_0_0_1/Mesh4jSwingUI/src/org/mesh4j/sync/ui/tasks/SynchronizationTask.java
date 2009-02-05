package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;
import java.util.List;

import javax.swing.SwingWorker;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.SyncEngine;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.ui.KmlUI;
import org.mesh4j.sync.ui.LogFrame;
import org.mesh4j.sync.ui.translator.KmlUITranslator;

public class SynchronizationTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLE
	private KmlUI ui;
	
	// BUSINESS METHODS
	
	public SynchronizationTask(KmlUI ui){
		super();
		this.ui = ui;
	}
	
	@Override
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	ui.disableAllButtons();
    	
		String endpoint1 = ui.getEndpoint1();
		String endpoint2 = ui.getEndpoint2();
		boolean ok = validateEndpoints(endpoint1, endpoint2);
		if(ok){
			ui.getLog().log(KmlUITranslator.getMessageSyncStart());
			ui.getLog().log("\t"+ KmlUITranslator.getLabelEndpoint1() + endpoint1);
			ui.getLog().log("\t"+ KmlUITranslator.getLabelEndpoint2() + endpoint2);
			ui.setStatusInProcess(KmlUITranslator.getMessageSyncStart());
			
			String resultMessage = "";
    		try{
    			IIdentityProvider identityProvider = ui.getPropertiesProvider().getIdentityProvider();
    			ISyncAdapter sourceRepo = ui.getSyncAdapterFactory().createSyncAdapter("endpoint1", endpoint1, identityProvider);
    			ISyncAdapter targetRepo = ui.getSyncAdapterFactory().createSyncAdapter("endpoint2", endpoint2, identityProvider);

    			SyncEngine syncEngine = new SyncEngine(sourceRepo, targetRepo);
    			List<Item> conflicts = syncEngine.synchronize();
    			if(conflicts.isEmpty()){
    				resultMessage = KmlUITranslator.getMessageSyncSuccessfully();
    				ui.setStatusOk(resultMessage);
    				
    			} else {
    				resultMessage = KmlUITranslator.getMessageSyncCompletedWithConflicts(conflicts.size());
    				ui.setStatusError(resultMessage);
    			}
    		} catch (Throwable e) {
    			LogFrame.Logger.error(e.getMessage(), e);
    			resultMessage = KmlUITranslator.getMessageSyncFailed();
    			ui.setStatusError(resultMessage);
    		}
    		ui.getLog().log(KmlUITranslator.getMessageSyncCompleted(resultMessage));
			
		}
        return null;
    }

	private boolean validateEndpoints(String endpoint1, String endpoint2) {
		boolean okEndpoint1 = validate(endpoint1, "Endpoint1");
		if(!okEndpoint1){
			return false;
		}
		
		boolean okEndpoint2 = validate(endpoint2, "Endpoint2");		
		if(!okEndpoint2){
			return false;
		}		
		
		if(endpoint1.equals(endpoint2)){
			String error = KmlUITranslator.getErrorSameEndpoints();
			ui.getLog().log(error);
			ui.setStatusError(error);
			return false;
		}
		
		return true;
	}
	
	private boolean validate(String endpointValue, String endpointHeader){
		if(endpointValue ==  null || endpointValue.trim().length() == 0){
			String error = KmlUITranslator.getErrorEndpoint(endpointHeader);
			ui.getLog().log(error);
			ui.setStatusError(error);
			return false;
		}
		
		if(HttpSyncAdapterFactory.isURL(endpointValue)){
			return validateURL(endpointValue, endpointHeader);
		} else {
			return validateKmlFile(endpointValue, endpointHeader);
		}
	}
	
	private boolean validateURL(String url, String endpointHeader){
		if(HttpSyncAdapterFactory.isMalformedURL(url)){
			String error = KmlUITranslator.getErrorInvalidURL(endpointHeader);
			ui.getLog().log(error);
			ui.setStatusError(error);
			return false;
		}
		
		if(!HttpSyncAdapterFactory.isValidURL(url)){
			String error = KmlUITranslator.getErrorURLConnectionFailed(endpointHeader);
			ui.getLog().log(error);
			ui.setStatusError(error);

			return false;
		}
		return true;
	}

	
	private boolean validateKmlFile(String fileName, String endpointHeader){
		if(!(fileName != null && fileName.trim().length() > 5 && KMLDOMLoaderFactory.isKML(fileName))){
			String error = KmlUITranslator.getErrorKMLType(endpointHeader);
			ui.getLog().log(error);
			ui.setStatusError(error);
			return false;
		}
		return true;
	}
	
	@Override
    public void done() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        ui.enableAllButtons();
    }

}
