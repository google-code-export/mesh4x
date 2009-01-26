package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class TestPhoneTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private MeshCompactUI ui;
	
	// BUSINESS METHODS
	public TestPhoneTask(MeshCompactUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
		String id = IdGenerator.INSTANCE.newID();
		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();
		
		ui.getProcessCustomMessages().notifyStartTestForPhoneCompatibility(endpoint, id);
				
		String message = makeAnswer(id);

		this.sendSms(endpoint.getEndpoint(), message);
		return null;
    }

	@Override
    public void done() {
		super.done();
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

	public static boolean isQuestion(String message) {
		return message.startsWith(MeshCompactUITranslator.getQuestionTestPhoneCompatibility());
	}
	
	public static String makeAnswer(String id) {
		return MeshCompactUITranslator.getQuestionTestPhoneCompatibility() + " <" + id + ">";
	}
	
	protected void sendSms(String endpointId, String message){
		SyncEngineUtil.sendSms(ui.getSyncEngine(), endpointId , message);
	}
}
