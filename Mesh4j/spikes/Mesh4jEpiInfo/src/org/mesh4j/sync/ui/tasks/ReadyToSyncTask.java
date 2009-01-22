package org.mesh4j.sync.ui.tasks;

import java.awt.Cursor;

import javax.swing.SwingWorker;

import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;
import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.ui.translator.EpiInfoCompactUITranslator;
import org.mesh4j.sync.utils.SyncEngineUtil;

public class ReadyToSyncTask extends SwingWorker<Void, Void> {

	// MODEL VARIABLEs
	private EpiinfoCompactUI ui;
	
	// BUSINESS METHODS
	public ReadyToSyncTask(EpiinfoCompactUI ui){
		super();
		this.ui = ui;
	}
	
    public Void doInBackground() {
		ui.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		EndpointMapping endpoint = (EndpointMapping)ui.getComboBoxEndpoint().getSelectedItem();
		DataSourceMapping dataSource = (DataSourceMapping)ui.getComboBoxMappingDataSource().getSelectedItem();
	
		ui.notifyStartReadyToSync(endpoint, dataSource);
		
		String message = makeQuestion(dataSource);
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
			return EpiInfoCompactUITranslator.getAnswerReadyToSync(dataSourceAlias);
		} else {
			return EpiInfoCompactUITranslator.getAnswerNotReadyToSync(dataSourceAlias);
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
	
	public static String makeQuestion(DataSourceMapping dataSourceMapping) {
		StringBuffer sb = new StringBuffer();
		sb.append(EpiInfoCompactUITranslator.getQuestionForReadyToSync());
		sb.append(" ");
		sb.append(dataSourceMapping.getAlias());
		sb.append("(");
		sb.append(EpiInfoCompactUITranslator.getLabelMSAccessMDB());
		sb.append(dataSourceMapping.getMDBName());
		sb.append(" ");
		sb.append(EpiInfoCompactUITranslator.getLabelMSAccessTableName());
		sb.append(dataSourceMapping.getTableName());
		sb.append(")");
		sb.append(EpiInfoCompactUITranslator.getQuestionEndSymbol());
		
		return sb.toString();
	}

	public static boolean isQuestion(String message) {
		return message.startsWith( EpiInfoCompactUITranslator.getQuestionForReadyToSync() ) &&
			message.endsWith(EpiInfoCompactUITranslator.getQuestionEndSymbol());
	}
	
	public static String getDataSourceAlias(String message) {
		int start = EpiInfoCompactUITranslator.getQuestionForReadyToSync().length() +1 ;
		int end = message.indexOf("(");
		return message.substring(start, end);
	}
}
