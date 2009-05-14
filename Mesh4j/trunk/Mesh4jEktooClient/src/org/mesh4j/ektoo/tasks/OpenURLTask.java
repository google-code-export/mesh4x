package org.mesh4j.ektoo.tasks;

import java.awt.Cursor;
import java.awt.Desktop;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;

public class OpenURLTask extends SwingWorker<Void, Void> 
{
  private final static Log LOGGER = LogFactory.getLog(OpenURLTask.class);	 
  
	// MODEL VARIABLEs
	private String url;
	private IErrorListener errorListener;
	private JFrame ui;
	
	// BUSINESS METHODS
	public OpenURLTask(JFrame ui, IErrorListener errorListener, String url)
	{
		super();
		this.url = url;
		this.errorListener = errorListener;
    this.ui = ui;
	}
	
	@Override
    public Void doInBackground() 
	{
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		if(HttpSyncAdapterFactory.isValidURL(url)){
			if (Desktop.isDesktopSupported()) {
				Desktop desktop = Desktop.getDesktop();
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					try{
						desktop.browse(new URL(url).toURI());
					} catch(Exception e){
						errorListener.notifyError(EktooUITranslator.getErrorOpenBrowser());
						LOGGER.error(e.getMessage(), e);
					}
				} else {
					errorListener.notifyError(EktooUITranslator.getMessageOpenBrowserActionNotSupported());
				}
			}else {
				errorListener.notifyError(EktooUITranslator.getMessageOpenBrowserActionNotSupported());
			}
		} else {
			errorListener.notifyError(EktooUITranslator.getErrorInvalidURL());
		}
		return null;
    }
	
	@Override
  public void done() 
	{
	  ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
  }
}
