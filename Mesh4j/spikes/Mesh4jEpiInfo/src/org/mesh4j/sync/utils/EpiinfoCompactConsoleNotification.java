package org.mesh4j.sync.utils;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.msaccess.IMsAccessSourceIdResolver;
import org.mesh4j.sync.epiinfo.ui.EpiinfoCompactUI;
import org.mesh4j.sync.epiinfo.ui.LogFrame;
import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.protocol.ACKMergeMessageProcessor;
import org.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.EqualStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.LastVersionStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.NoChangesMessageProcessor;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.ui.translator.EpiInfoUITranslator;

public class EpiinfoCompactConsoleNotification implements ISmsConnectionInboundOutboundNotification, IMessageSyncAware {
	
	private final static Log Logger = LogFactory.getLog(EpiinfoCompactConsoleNotification.class);

	// MODEL VARIABLES
	private LogFrame consoleView;
	private EpiinfoCompactUI ui;
	private IFilter<String> messageFilter;
	private IMsAccessSourceIdResolver sourceIdResolver;
	
	// BUSINESS METHODS
	public EpiinfoCompactConsoleNotification(LogFrame consoleView, EpiinfoCompactUI ui, IFilter<String> messageFilter, IMsAccessSourceIdResolver sourceIdResolver) {
		super();
		this.consoleView = consoleView;
		this.ui = ui;
		this.messageFilter = messageFilter;
	}
	
	// ISmsConnectionInboundOutboundNotification methods
	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
		ui.increaseSmsIn();
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		ui.setErrorImageStatus();
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifyReceiveMessage(endpointId, message));
		ui.increaseSmsIn();
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessage(endpointId, message));
		ui.increaseSmsOut();
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		ui.setErrorImageStatus();
		consoleView.log("\t"+EpiInfoUITranslator.getMessageNotifySendMessageError(endpointId, message));
		ui.increaseSmsOut();
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		if(this.messageFilter.applies(message)){
			ui.notifyReceiveMessage(endpoint, message, date);
		} else {
			Logger.info("SMS - Received message was not processed, endpoint: " + endpoint + " message: " + message + " date: " + date.toString());
		}
	}

	
	// IMessageSyncAware methods

	@Override
	public void beginSync(ISyncSession syncSession) {
		ui.notifyBeginSync(syncSession.getSourceId(), syncSession.shouldSendChanges(), syncSession.shouldReceiveChanges());
		consoleView.log(EpiInfoUITranslator.getLabelStart());
	}

	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
		endSync(syncSession.getTarget().getEndpointId(), syncSession.getSourceId(), conflicts);
	}
	
	public void endSync(String target, String sourceId, List<Item> conflicts) {
		if(conflicts.isEmpty()){
			consoleView.log(EpiInfoUITranslator.getLabelSuccess());
			this.ui.notifyEndSync(false);
		} else {
			consoleView.log(EpiInfoUITranslator.getLabelSyncEndWithConflicts(conflicts.size()));
			this.ui.notifyEndSync(true);
		}
	}

	@Override
	public void beginSyncWithError(ISyncSession syncSession) {
		ui.setErrorImageStatus();
		consoleView.log(EpiInfoUITranslator.getMessageErrorBeginSync(syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId())));		
	}

	@Override
	public void notifyCancelSync(ISyncSession syncSession) {
		consoleView.log(EpiInfoUITranslator.getMessageCancelSync(syncSession.getSessionId(), syncSession.getTarget().getEndpointId(), sourceIdResolver.getSourceName(syncSession.getSourceId())));
		this.ui.notifyEndSync(false);
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
	public void notifyMessageProcessed(ISyncSession syncSession, IMessage message, List<IMessage> response) {
		consoleView.log(EpiInfoUITranslator.getMessageProcessed(message, response));
		
		ui.updateLocalStatus(
			syncSession.getNumberOfAddedItems(), 
			syncSession.getNumberOfUpdatedItems(),
			syncSession.getNumberOfDeletedItems());
		
		if(BeginSyncMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
			String dataSourceType = BeginSyncMessageProcessor.getSourceType(message.getData());
			ui.updateRemoteDataSource(dataSourceType);
			ui.increaseSmsIn();
		}

		if(EqualStatusMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
			String dataSourceType = message.getData();
			ui.updateRemoteDataSource(dataSourceType);
		}
		
		if(NoChangesMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
			String dataSourceType = message.getData();
			ui.updateRemoteDataSource(dataSourceType);
		}
		
		if(LastVersionStatusMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
			String dataSourceType = LastVersionStatusMessageProcessor.getSourceType(message.getData());
			ui.updateRemoteDataSource(dataSourceType);
		}
		
		if(ACKMergeMessageProcessor.MESSAGE_TYPE.equals(message.getMessageType())){
			ui.updateRemoteStatus(
					ACKMergeMessageProcessor.getNumberOfAddedItems(message.getData()), 
					ACKMergeMessageProcessor.getNumberOfUpdatedItems(message.getData()),
					ACKMergeMessageProcessor.getNumberOfDeletedItems(message.getData()));			
		}
	}

	@Override
	public void notifySessionCreationError(IMessage message, String sourceId) {
		consoleView.log(EpiInfoUITranslator.getMessageErrorSessionCreation(message, sourceIdResolver.getSourceName(sourceId)));
	}	
}
