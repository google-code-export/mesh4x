package org.mesh4j.sync.utils;

import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.IProgressMonitor;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.MeshUI;
import org.mesh4j.sync.ui.translator.MeshUITranslator;
import org.smslib.helper.CommPortIdentifier;

import com.swtdesigner.SwingResourceManager;

public class ConsoleNotification implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware, IProgressMonitor {
	
	private final static Log Logger = LogFactory.getLog(ConsoleNotification.class);

	// MODEL VARIABLES
	private JTextArea consoleView;
	private boolean stop;
	private JLabel imageStatus;
	private MeshUI ui;
	
	// BUSINESS METHODS
	public ConsoleNotification(JTextArea consoleView, JLabel imageStatus, MeshUI ui) {
		super();
		this.consoleView = consoleView;
		this.imageStatus = imageStatus;
		this.ui = ui;
	}
	
	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		this.log("\t"+MeshUITranslator.getMessageNotifyReceiveMessage(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.setErrorImageStatus();
		this.log("\t"+MeshUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+MeshUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		this.setErrorImageStatus();
		this.log("\t"+MeshUITranslator.getMessageNotifySendMessageError(endpointId, message));		
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
	}
	
	// IMessageSyncAware methods

	@Override
	public void beginSync(ISyncSession syncSession) {
		this.beginSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId());
	}
	
	public void beginSync(String target, String sourceId) {
		this.setInProcessImageStatus();
		this.log(MeshUITranslator.getLabelStart());
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId(), conflicts);
	}
	
	public void endSync(String target, String sourceId, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			this.setEndSyncImageStatus();
			this.log(MeshUITranslator.getLabelSuccess());
		} else {
			this.setErrorImageStatus();
			this.log(MeshUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
		}
		this.ui.setEndSync();
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		this.setErrorImageStatus();
		this.log(MeshUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId()));		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		this.log(MeshUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), syncSession.getSourceId()));
		this.setEndSyncImageStatus();
		this.ui.setEndSync();
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(ISyncSession syncSession) {
		this.setErrorImageStatus();
		this.log(MeshUITranslator.getMessageCancelSyncErrorSessionNotOpen(syncSession.getTarget(), syncSession.getSourceId()));		
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		this.log(MeshUITranslator.getMessageInvalidMessageProtocol(message));
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		this.log(MeshUITranslator.getMessageErrorInvalidProtocolMessageOrder(message));
	}

	@Override
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		this.log(MeshUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		this.log(MeshUITranslator.getMessageErrorSessionCreation(message, sourceId));
	}
	
	// IProgressMonitor methods
	
	@Override
	public void checkingModem(CommPortIdentifier port, int baudRateAvailable) {
		log("\t"+MeshUITranslator.getMessageCheckingModem(port.getName(), baudRateAvailable));
	}

	@Override
	public void checkingPortInfo(CommPortIdentifier port, int baudRateAvailable) {
		log("\t"+MeshUITranslator.getMessageCheckingPort(port.getName(), baudRateAvailable));
		
	}

	@Override
	public void notifyAvailableModem(CommPortIdentifier port, int baudRateAvailable, Modem modem) {
		logAppendEndLine(MeshUITranslator.getMessageAvailableModem(modem));		
	}

	@Override
	public void notifyAvailablePortInfo(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(MeshUITranslator.getLabelAvailable());		
	}

	@Override
	public void notifyNonAvailableModem(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(MeshUITranslator.getLabelNoAvailable());
	}

	@Override
	public void notifyNonAvailablePortInfo(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(MeshUITranslator.getLabelNoAvailable());
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
		this.imageStatus.setIcon(SwingResourceManager.getIcon(MeshUI.class, "/error.gif"));		
	}
	
	public void setInProcessImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(MeshUI.class, "/inprocess.gif"));		
	}
	
	public void setEndSyncImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(MeshUI.class, "/endsync.png"));		
	}
	
	public void setReadyImageStatus() {
		this.imageStatus.setIcon(SwingResourceManager.getIcon(MeshUI.class, "/endsync.png"));	
	}
}
