package org.mesh4j.ektoo.ui.settings;

import java.awt.Cursor;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.ektoo.Ektoo;
import org.mesh4j.ektoo.ui.EktooFrame;
import org.mesh4j.ektoo.ui.SettingsContainer;
import org.mesh4j.ektoo.ui.component.messagedialog.MessageDialog;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.translator.MessageProvider;

public class SettingsViewTask extends SwingWorker<String, Void>{

	private final static Log LOGGER = LogFactory.getLog(SettingsViewTask.class);
	private EktooFrame ui;
	
	
	public SettingsViewTask(EktooFrame ui){
		this.ui = ui;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try{
			SettingsController controller = new SettingsController(ui);
			SettingsContainer container = new SettingsContainer(controller,ui);
			showSettingsInPopUP(container);
		} catch (Exception ec){
			LOGGER.error(ec);
			MessageDialog.showErrorMessage(ui, 
					EktooUITranslator.getErrorSettingsLoading());
		}
		return null;
	}

	@Override
	public void done(){
		ui.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void showSettingsInPopUP(JComponent component){
		ui.showViewInPopup(MessageProvider.translate("TITLTE_SETTINGS"),
				component,400,500,false,true);
	}
}
