package org.mesh4j.sync.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.epiinfo.ui.ExampleUI;
import org.mesh4j.sync.epiinfo.ui.LogFrame;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;

public class ExampleConsoleNotification implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware {
	
	private final static Log Logger = LogFactory.getLog(ExampleConsoleNotification.class);

	// MODEL VARIABLES
	private LogFrame consoleView;
	private ExampleUI ui;
	private ArrayList<String> awaitedMessages = new ArrayList<String>();
	
	// BUSINESS METHODS
	public ExampleConsoleNotification(LogFrame consoleView, ExampleUI ui) {
		super();
		this.consoleView = consoleView;
		this.ui = ui;
	}
	
	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		ui.setErrorImageStatus();
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessage(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		ui.setErrorImageStatus();
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessageError(endpointId, message));		
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		// TODO (JMT) I must check endpoint?
		if(this.waitingForMessage(message)){
			this.awaitedMessages = new ArrayList<String>();
			ui.setPhoneIsCompatible();
		} else {
			Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
		}
	}
	
	private boolean waitingForMessage(String message) {
		return this.awaitedMessages.contains(message);
	}
	
	public void addAwaitedMessage(String message) {
		this.awaitedMessages.add(message);		
	}
	
	// IMessageSyncAware methods

	@Override
	public void beginSync(ISyncSession syncSession) {
		this.beginSync(syncSession.getTarget().getEndpointId(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()));
	}
	
	public void beginSync(String target, String mdbFileName, String mdbTable) {
		ui.setBeginSync();
		consoleView.log(EpiInfoUITranslator.getLabelStart());
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), MsAccessSyncAdapterFactory.getFileName(syncSession.getSourceId()), MsAccessSyncAdapterFactory.getTableName(syncSession.getSourceId()), conflicts);
	}
	
	public void endSync(String target, String mdbFileName, String mdbTable, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			consoleView.log(EpiInfoUITranslator.getLabelSuccess());
			this.ui.setEndSync(false);
		} else {
			consoleView.log(EpiInfoUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
			this.ui.setEndSync(true);
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		ui.setErrorImageStatus();
		consoleView.log(EpiInfoUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), EpiInfoUITranslator.getSourceId(syncSession.getSourceId())));		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		consoleView.log(EpiInfoUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), EpiInfoUITranslator.getSourceId(syncSession.getSourceId())));
		this.ui.setEndSync(false);
	}

	@Override
	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		ui.setErrorImageStatus();
		consoleView.log(EpiInfoUITranslator.getMessageCancelSyncErrorSessionNotOpen(endpoint, sourceId));		
	}

	@Override
	public void notifyInvalidMessageProtocol(IMessage message) {
		consoleView.log(EpiInfoUITranslator.getMessageInvalidMessageProtocol(message));
	}

	@Override
	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		consoleView.log(EpiInfoUITranslator.getMessageErrorInvalidProtocolMessageOrder(message));
	}

	@Override
	public void notifyMessageProcessed(IMessage message, List<IMessage> response) {
		consoleView.log(EpiInfoUITranslator.getMessageProcessed(message, response));
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		consoleView.log(EpiInfoUITranslator.getMessageErrorSessionCreation(message, sourceId));
	}	
}
