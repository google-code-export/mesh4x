package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;
import java.awt.Desktop;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.ui.LogFrame;
import org.mesh4j.sync.ui.translator.KmlUITranslator;

public class OpenURLTask extends SwingWorker<Void, Void> {
	 
	// MODEL VARIABLEs
	private String url;
	private IErrorListener errorListener;
	private JFrame frame;
	
	// BUSINESS METHODS
	public OpenURLTask(JFrame frame,IErrorListener errorListener, String url){
		super();
		this.url = url;
		this.errorListener = errorListener;
		this.frame = frame;
	}
	
	@Override
    public Void doInBackground() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if(HttpSyncAdapterFactory.isValidURL(url)){
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					try{
						desktop.browse(new URL(url).toURI());
					} catch(Exception e){
						errorListener.notifyError(KmlUITranslator.getErrorOpenBrowser());
						LogFrame.Logger.error(e.getMessage(), e);
					}
				} else {
					errorListener.notifyError(KmlUITranslator.getMessageOpenBrowserActionNotSupported());
				}
			}else {
				errorListener.notifyError(KmlUITranslator.getMessageOpenBrowserActionNotSupported());
			}
		} else {
			errorListener.notifyError(KmlUITranslator.getErrorInvalidURL(url));
		}
		return null;
    }
	
	@Override
    public void done() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
