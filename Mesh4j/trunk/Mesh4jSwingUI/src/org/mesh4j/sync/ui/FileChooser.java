package org.mesh4j.sync.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.mesh4j.sync.ui.translator.KmlUITranslator;

public class FileChooser {

	
	public static String openFileDialogKML(Component frame, String fileName){
		String fileNameSelected = openFileDialog(frame, fileName, new FileNameExtensionFilter(KmlUITranslator.getLabelKMLExtensions(), "kml", "kmz"));
		return fileNameSelected;
	}
	
	public static String openFileDialog(Component frame, String fileName, FileNameExtensionFilter filter){
		JFileChooser chooser = new JFileChooser();
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(filter);
		
		if(fileName != null && fileName.trim().length() > 0){
			File file = new File(fileName);
			chooser.setSelectedFile(file);
		}
		
		int returnVal = chooser.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getAbsolutePath();
		} else{
			return null;
		}
	}
}
