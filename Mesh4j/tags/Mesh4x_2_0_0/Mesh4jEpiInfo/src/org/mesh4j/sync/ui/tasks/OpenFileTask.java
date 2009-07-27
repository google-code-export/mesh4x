package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.mesh4j.sync.ui.LogFrame;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;

public class OpenFileTask extends SwingWorker<Void, Void> {
	 
	// MODEL VARIABLEs
	private String fileName;
	private IErrorListener errorListener;
	private JFrame frame;
	
	// BUSINESS METHODS
	public OpenFileTask(JFrame frame, IErrorListener errorListener, String fileName){
		super();
		this.fileName = fileName;
		this.errorListener = errorListener;
		this.frame = frame;
	}
	
	@Override
    public Void doInBackground() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		try{
			File file = new File(fileName);
			if(file.exists()){
				openFile(this.errorListener, file);
			} else {
				errorListener.notifyError(MeshCompactUITranslator.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
			}
		}catch(Throwable e){
			LogFrame.Logger.error(e.getMessage(), e);
		}
		return null;
    }
	
	public static void openFile(IErrorListener errorListener, String fileName) throws IOException {
		openFile(errorListener, new File(fileName));
	}
	
	public static void openFile(IErrorListener errorListener, File file) throws IOException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.OPEN)) {
				if(file.exists()){
					desktop.open(file.getCanonicalFile());
				} else {
					errorListener.notifyError(MeshCompactUITranslator.getErrorImpossibleToOpenFileBecauseFileDoesNotExists());
				}
			} else {
				errorListener.notifyError(MeshCompactUITranslator.getErrorOpenFileActionNotSupported());
			}
		} else {
			errorListener.notifyError(MeshCompactUITranslator.getErrorOpenFileActionNotSupported());	
		}
	}
	
	@Override
    public void done() {
		this.frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
