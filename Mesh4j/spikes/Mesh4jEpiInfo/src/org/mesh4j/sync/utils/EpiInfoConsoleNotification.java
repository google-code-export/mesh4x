package org.mesh4j.sync.utils;

import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.epiinfo.ui.EpiinfoUI;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.IProgressMonitor;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;
import org.smslib.helper.CommPortIdentifier;

import com.swtdesigner.SwingResourceManager;

public class EpiInfoConsoleNotification implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware, IProgressMonitor {
	
	private final static Log Logger = LogFactory.getLog(EpiInfoConsoleNotification.class);

	// MODEL VARIABLES
	private JTextArea consoleView;
	private boolean stop;
	private JLabel imageStatus;
	private EpiinfoUI epiinfoUI;
	
	// BUSINESS METHODS
	public EpiInfoConsoleNotification(JTextArea consoleView, JLabel imageStatus, EpiinfoUI ui) {
		super();
		this.consoleView = consoleView;
		this.imageStatus = imageStatus;
		this.epiinfoUI = ui;
	}
	
	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.setErrorImageStatus();
		this.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessage(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		this.setErrorImageStatus();
		this.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessageError(endpointId, message));		
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
	}
	
	// IMessageSyncAware methods

	@Override
	public void beginSync(ISyncSession syncSession) {
		this.beginSync(syncSession.getTarget().getEndpointId(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()));
	}
	
	public void beginSync(String target, String mdbFileName, String mdbTable) {
		this.setInProcessImageStatus();
		this.log(EpiInfoUITranslator.getLabelStart());
//		this.logStatus(EpiInfoUITranslator.getStatusBeginSync(target, mdbFileName, mdbTable, new Date()));
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()), conflicts);
	}
	
	public void endSync(String target, String mdbFileName, String mdbTable, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			this.setEndSyncImageStatus();
			this.log(EpiInfoUITranslator.getLabelSuccess());
//			this.logStatus(EpiInfoUITranslator.getStatusEndSync(target, mdbFileName, mdbTable, new Date()));
		} else {
			this.setErrorImageStatus();
			this.log(EpiInfoUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
//			this.logStatus(EpiInfoUITranslator.getStatusEndSyncWithConflicts(target, mdbFileName, mdbTable, new Date(), conflicts.size()));
		}
		this.epiinfoUI.setEndSync();
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		this.setErrorImageStatus();
		this.log(EpiInfoUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), EpiInfoUITranslator.getSourceId(syncSession.getSourceId())));		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		this.log(EpiInfoUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), EpiInfoUITranslator.getSourceId(syncSession.getSourceId())));
//		this.logStatus(EpiInfoUITranslator.getStatusCancelSync(syncSession.getTarget(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()), new Date()));
		this.setEndSyncImageStatus();
		this.epiinfoUI.setEndSync();
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		this.setErrorImageStatus();
		this.log(EpiInfoUITranslator.getMessageCancelSyncErrorSessionNotOpen(endpoint, sourceId));		
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		this.log(EpiInfoUITranslator.getMessageInvalidMessageProtocol(message));
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		this.log(EpiInfoUITranslator.getMessageErrorInvalidProtocolMessageOrder(message));
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		this.log(EpiInfoUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		this.log(EpiInfoUITranslator.getMessageErrorSessionCreation(message, sourceId));
	}
	
	// IProgressMonitor methods
	
	@Override
	public void checkingModem(CommPortIdentifier port, int baudRateAvailable) {
		log("\t"+EpiInfoUITranslator.getMessageCheckingModem(port.getName(), baudRateAvailable));
	}

	@Override
	public void checkingPortInfo(CommPortIdentifier port, int baudRateAvailable) {
		log("\t"+EpiInfoUITranslator.getMessageCheckingPort(port.getName(), baudRateAvailable));
		
	}

	@Override
	public void notifyAvailableModem(CommPortIdentifier port, int baudRateAvailable, Modem modem) {
		logAppendEndLine(EpiInfoUITranslator.getMessageAvailableModem(modem));		
	}

	@Override
	public void notifyAvailablePortInfo(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(EpiInfoUITranslator.getLabelAvailable());		
	}

	@Override
	public void notifyNonAvailableModem(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(EpiInfoUITranslator.getLabelNoAvailable());
	}

	@Override
	public void notifyNonAvailablePortInfo(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(EpiInfoUITranslator.getLabelNoAvailable());
	}
	
	// Console view methods
	public void log(String text) {
		this.consoleView.setText(text + "\n"+ this.consoleView.getText());
		if(Logger.isInfoEnabled()){
			Logger.info(text);
		}
	}

	public void logError(Throwable t, String errorMessage) {
		this.consoleView.setText(errorMessage + "\n"+ this.consoleView.getText());
		Logger.error(t.getMessage(), t);
	}
	
	public void logAppendEndLine(String text) {
		String line, allWithOutLine;
		
		String consoleText = this.consoleView.getText();
		int lfIndex = consoleText.indexOf("\n");
		if(lfIndex == -1){
			line = consoleText;
			allWithOutLine = "";
		} else {
			line = consoleText.substring(0, lfIndex);
			allWithOutLine = consoleText.substring(lfIndex, consoleText.length());
		}
		
		this.consoleView.setText(line+ text + allWithOutLine);
		if(Logger.isInfoEnabled()){
			Logger.info(text);
		}
	}

	public void stopDiscovery(){
		this.stop = true;
	}
	
	public void startDiscovery(){
		this.stop = false;
	}
	
	public boolean isStopped(){
		return stop;
	}
	
	
	public void setErrorImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/error.gif"));		
	}
	
	public void setInProcessImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/inprocess.gif"));		
	}
	
	public void setEndSyncImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/endsync.png"));		
	}
	
	public void setReadyImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(EpiinfoUI.class, "/endsync.png"));	
	}
}
