package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.mappings.MSAccessDataSourceMapping;
import org.mesh4j.sync.ui.MeshCompactUI;
import org.mesh4j.sync.ui.translator.MeshCompactUITranslator;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class ReadyToSyncTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private MeshCompactUI ui;
	
	// BUSINESS METHODS
	public ReadyToSyncTask(MeshCompactUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();
		MSAccessDataSourceMapping dataSource = (MSAccessDataSourceMapping)ui.getComboBoxMappingDataSource().getSelectedItem();
	
		ui.getProcessCustomMessages().notifyStartReadyToSync(endpoint, dataSource);
		
		String message = makeQuestion(dataSource, ui.getPropertiesProvider().getLoggedUserName());
		SyncEngineUtil.sendSms(ui.getSyncEngine(), endpoint.getEndpoint(), message);
		return null;
    }

	@Override
    public void done() {
		super.done();
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

	public static String makeAnswer(String dataSourceAlias, boolean isDataSourceAvailable) {
		if(isDataSourceAvailable){
			return MeshCompactUITranslator.getAnswerReadyToSync(dataSourceAlias);
		} else {
			return MeshCompactUITranslator.getAnswerNotReadyToSync(dataSourceAlias);
		}
	}
	
	public static boolean isAnswer(String message) {
		return message.startsWith(makeAnswer("", true)) || message.startsWith(makeAnswer("", false));
	}
	
	public static boolean isAnswerOk(String message, String dataSourceAlias) {
		return makeAnswer(dataSourceAlias, true).equals(message);
	}
	
	public static boolean isAnswerNotOk(String message, String dataSourceAlias) {
		return makeAnswer(dataSourceAlias, false).equals(message);
	}
	
	public static String makeQuestion(MSAccessDataSourceMapping dataSourceMapping, String userName) {
		StringBuffer sb = new StringBuffer();
		sb.append(MeshCompactUITranslator.getQuestionForReadyToSync());
		sb.append(" ");
		sb.append(dataSourceMapping.getAlias());
		sb.append("(");
		sb.append(MeshCompactUITranslator.getLabelMSAccessMDB());
		sb.append(dataSourceMapping.getMDBName());
		sb.append(" ");
		sb.append(MeshCompactUITranslator.getLabelMSAccessTableName());
		sb.append(dataSourceMapping.getTableName());
		sb.append("). ");
		sb.append(userName);
		sb.append(MeshCompactUITranslator.getQuestionEndSymbol());
		
		return sb.toString();
	}

	public static boolean isQuestion(String message) {
		return message.startsWith( MeshCompactUITranslator.getQuestionForReadyToSync() ) &&
			message.endsWith(MeshCompactUITranslator.getQuestionEndSymbol());
	}
	
	public static String getDataSourceAlias(String message) {
		int start = MeshCompactUITranslator.getQuestionForReadyToSync().length() +1 ;
		int end = message.indexOf("(");
		return message.substring(start, end);
	}

	public static String getDataSourceDescription(String message) {
		int start = MeshCompactUITranslator.getQuestionForReadyToSync().length() +1 ;
		int end = message.indexOf(")");
		return message.substring(start, end+1);
	}

	public static String getQuestionUserName(String message) {
		int start = message.indexOf("). ") + 3;
		int end = message.indexOf(MeshCompactUITranslator.getQuestionEndSymbol());
		return message.substring(start, end);
	}
}
