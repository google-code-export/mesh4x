package org.mesh4j.ektoo.ui.settings;

import java.awt.Cursor;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.SettingsContainer;

public class SettingsViewTask extends SwingWorker<String, Void>{

	private EktooFrame parent;
	
	
	public SettingsViewTask(EktooFrame parent){
		this.parent = parent;
//		parent.getTargetItem().getCurrentController().get
	}
	
	@Override
	protected String doInBackground() throws Exception {
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			SettingsController controller = new SettingsController(parent);
			SettingsContainer container = new SettingsContainer(controller,parent);
			showSettingsInPopUP(container);
		} catch (Exception ec){
			//TODO exception handling
		}
		return null;
	}

	@Override
	public void done(){
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void showSettingsInPopUP(JComponent component){
		parent.showViewInPopup("Settings",component,400,500,true,false);
	}
}
