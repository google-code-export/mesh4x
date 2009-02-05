package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;
import java.io.File;

import javax.swing.SwingWorker;

import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.ui.KmlFrame;
import org.mesh4j.sync.ui.translator.KmlUITranslator;

public abstract class AbstractKmlTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLE
	private KmlFrame kmlFrame;
	private String fileName;
	
	// BUSINESS METHODS
	
	public AbstractKmlTask(KmlFrame kmlFrame, String fileName){
		super();
		this.kmlFrame = kmlFrame;
		this.fileName = fileName;
	}
	
	@Override
    public Void doInBackground() {
		kmlFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    	
		boolean ok = validateKmlFile(fileName, "KmlFile");
		if(ok){
			this.basicProcess();
		}
        return null;
    }

	protected abstract void basicProcess();

	protected boolean validateKmlFile(String fileName, String endpointHeader){
		if(!(fileName != null && fileName.trim().length() > 5 && KMLDOMLoaderFactory.isKML(fileName))){
			String error = KmlUITranslator.getErrorKMLType(endpointHeader);
			setStatusError(error);
			return false;
		}
			
		File file = new File(fileName);
		if(!file.exists()){
			String error = KmlUITranslator.getErrorFileDoesNotExist(endpointHeader);
			setStatusError(error);
			return false;
		}		
		return true;
	}
	
	protected void setStatusError(String error) {
		this.kmlFrame.setStatusError(error);
		log(error);
	}

	protected void setStatusOk(String msg) {
		this.kmlFrame.setStatusOk(msg);
		log(msg);
	}

	protected void setStatusInProcess(String msg) {
		this.kmlFrame.setStatusInProcess(msg);
		log(msg);
	}
	
	@Override
    public void done() {
		kmlFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
	
	protected String getFileName(){
		return this.fileName;
	}
	
	protected void log(String text){
		this.kmlFrame.getKmlUI().getLog().log(text);
	}
	
	protected IIdentityProvider getIdentityProvider(){
		return this.kmlFrame.getKmlUI().getPropertiesProvider().getIdentityProvider();
	}
}
