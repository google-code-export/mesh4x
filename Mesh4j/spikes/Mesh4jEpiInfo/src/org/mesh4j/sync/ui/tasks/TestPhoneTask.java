package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.mesh4j.sync.epiinfo.ui.ExampleUI;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class TestPhoneTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private ExampleUI ui;
	
	// BUSINESS METHODS
	public TestPhoneTask(ExampleUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		ui.setStartTestForPhoneCompatibility();
		
		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();
		
		String message = "Test phone compatibility <" + IdGenerator.INSTANCE.newID() + ">";
		ui.getConsoleNotification().addAwaitedMessage(message);
		SyncEngineUtil.sendSms(ui.getSyncEngine(), endpoint.getEndpoint(), message);
		return null;
    }

	@Override
    public void done() {
		super.done();
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
