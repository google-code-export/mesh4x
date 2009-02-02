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
				
		String message = makeAnswer(id, ui.getPropertiesProvider().getLoggedUserName());

		this.sendSms(endpoint.getEndpoint(), message);
		return null;
    }

	@Override
    public void done() {
		super.done();
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

	public static boolean isQuestion(String message, String currentUserName) {
		String userName = getUserName(message);
		return isTestPhoneMessage(message) && !currentUserName.equals(userName);
	}
	
	public static String getUserName(String message) {
		int end = message.indexOf(" <");
		int start = message.substring(0, end).lastIndexOf(" ") + 1;
		return message.substring(start, end);
	}

	public static String makeAnswer(String id, String currentUserName) {
		return MeshCompactUITranslator.getQuestionTestPhoneCompatibility() 
			+ " " 
			+ currentUserName 
			+ " <" + id + "> ";
	}
	
	protected void sendSms(String endpointId, String message){
		SyncEngineUtil.sendSms(ui.getSyncEngine(), endpointId , message);
	}

	public static boolean isTestPhoneMessage(String message) {
		return message.startsWith(MeshCompactUITranslator.getQuestionTestPhoneCompatibility());
	}
}
