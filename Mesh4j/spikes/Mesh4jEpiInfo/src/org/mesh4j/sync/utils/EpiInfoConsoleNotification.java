package org.mesh4j.sync.utils;

import java.util.Date;
import java.util.List;

import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;

public class EpiInfoConsoleNotification implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware {
	
	private final static Log Logger = LogFactory.getLog(EpiInfoConsoleNotification.class);

	// MODEL VARIABLES
	private JTextArea consoleView;
	private JTextArea consoleStatus;
	
	// BUSINESS METHODS
	public EpiInfoConsoleNotification(JTextArea consoleView, JTextArea consoleStatus) {
		super();
		this.consoleView = consoleView;
		this.consoleStatus = consoleStatus;
	}
	
	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessage(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
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
		this.log(EpiInfoUITranslator.getLabelStart());
		this.logStatus(EpiInfoUITranslator.getStatusBeginSync(target, mdbFileName, mdbTable, new Date()));
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()), conflicts);
	}
	
	public void endSync(String target, String mdbFileName, String mdbTable, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			this.log(EpiInfoUITranslator.getLabelSuccess());
			this.logStatus(EpiInfoUITranslator.getStatusEndSync(target, mdbFileName, mdbTable, new Date()));
		} else {
			this.log(EpiInfoUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
			this.logStatus(EpiInfoUITranslator.getStatusEndSyncWithConflicts(target, mdbFileName, mdbTable, new Date(), conflicts.size()));
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		this.log(EpiInfoUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), EpiInfoUITranslator.getSourceId(syncSession.getSourceId())));		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		this.log(EpiInfoUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), EpiInfoUITranslator.getSourceId(syncSession.getSourceId())));
		this.logStatus(EpiInfoUITranslator.getStatusCancelSync(syncSession.getTarget(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()), new Date()));
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
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
	public void notifyMessageProcessed(IMessage message, List<IMessage> response) {
		this.log(EpiInfoUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		this.log(EpiInfoUITranslator.getMessageErrorSessionCreation(message, sourceId));
	}

	public void logStatus(String text) {
		this.consoleStatus.setText(text + "\n"+ this.consoleStatus.getText());
	}
	
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

}
