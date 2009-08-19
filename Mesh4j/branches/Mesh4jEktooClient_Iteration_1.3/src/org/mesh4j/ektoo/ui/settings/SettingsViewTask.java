package org.mesh4j.ektoo.ui.settings;

import java.awt.Cursor;

import javax.swing.JComponent;
import javax.swing.SwingWorker;

import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.SettingsContainer;
import org.mesh4j.ektoo.ui.settings.prop.IPropertyManager;

public class SettingsViewTask extends SwingWorker<String, Void>{

	private EktooFrame parent;
	
	
	public SettingsViewTask(EktooFrame parent){
		this.parent = parent;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			SettingsController controller = new SettingsController();
			SettingsContainer container = new SettingsContainer(controller,parent);
			showSettingsInPopUP(container);
		} catch (Exception ec){
			
		}
		return null;
	}

	@Override
	public void done(){
		parent.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void showSettingsInPopUP(JComponent component){
		parent.showViewInPopup("Settings",component,400,500,true,true);
	}
}
