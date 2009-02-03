package org.mesh4j.sync.ui.tasks;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import org.mesh4j.sync.ui.LogFrame;

public class OpenFileTask extends SwingWorker<Void, Void> {
	 
	// MODEL VARIABLEs
	private String fileName;
	
	// BUSINESS METHODS
	public OpenFileTask(String fileName){
		super();
		this.fileName = fileName;
	}
	
	@Override
    public Void doInBackground() {
		try{
			File file = new File(fileName);
			if(file.exists()){
				openFile(file);
			}
		}catch(Throwable e){
			LogFrame.Logger.error(e.getMessage(), e);
		}
		return null;
    }
	
	public static void openFile(String fileName) throws IOException {
		openFile(new File(fileName));
	}
	
	public static void openFile(File file) throws IOException {
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.OPEN)) {
				desktop.open(file.getCanonicalFile());
			}
		}
	}
}
