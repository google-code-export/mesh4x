package org.mesh4j.sync.ui.translator;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageSyncProtocol;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.message.protocol.ACKEndSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.ACKMergeMessageProcessor;
import org.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.CancelSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.EndSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.EqualStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.GetForMergeMessageProcessor;
import org.mesh4j.sync.message.protocol.LastVersionStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.MergeMessageProcessor;
import org.mesh4j.sync.message.protocol.MergeWithACKMessageProcessor;
import org.mesh4j.sync.message.protocol.NoChangesMessageProcessor;
import org.mesh4j.sync.security.LoggedInIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;


public class MeshUITranslator {

	public static String getTitle() {
		return MessageTranslator.translate("MESH_TITLE");
	}
	
	public static String getTitleEmulationMode(String emulationEndpointId) {
		return MessageTranslator.translate("MESH_TITLE_EMULATION", emulationEndpointId, LoggedInIdentityProvider.getUserName());
	}

	public static String getGroupCommunications() {
		return MessageTranslator.translate("MESH_GROUP_COMMUNICATIONS");
	}

	public static String getToolTipPhoneNumber() {
		return MessageTranslator.translate("MESH_TOOLTIP_PHONE_NUMBER");
	}

	public static String getLabelSMSDevice() {
		return MessageTranslator.translate("MESH_LABEL_SMS_DEVICE");
	}

	public static String getLabelPhoneNumber() {
		return MessageTranslator.translate("MESH_LABEL_PHONE_NUMBER");
	}

	public static String getLabelDataSource() {
		return MessageTranslator.translate("MESH_LABEL_DATA_SOURCE");
	}

	public static String getLabelTable() {
		return MessageTranslator.translate("MESH_LABEL_TABLE");
	}

	public static String getToolTipDataSource() {
		return MessageTranslator.translate("MESH_TOOLTIP_DATA_SOURCE");
	}

	public static String getToolTipFileChooser() {
		return MessageTranslator.translate("MESH_TOOLTIP_FILE_CHOOSER");
	}

	public static String getLabelFileChooser() {
		return MessageTranslator.translate("MESH_LABEL_FILE_CHOOSER");
	}

	public static String getSynchronize() {
		return MessageTranslator.translate("MESH_LABEL_SYNCHRONIZE");
	}

	public static String getCancel() {
		return MessageTranslator.translate("MESH_LABEL_CANCEL");
	}

	public static String getMessageNotifySendMessage(String endpointId, String message) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_NOTIFY_SEND_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessage(String endpointId, String message) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessageError(String endpointId, String message) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_NOTIFY_RECEIVE_MSG_ERROR", endpointId, message);
	}

	public static String getMessageNotifySendMessageError(String endpointId, String message) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_NOTIFY_SEND_MSG_ERROR", endpointId, message);
	}

	public static String getLabelMessageToSend() {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_LABEL_MESSAGE_TO_SEND");
	}

	public static String getLabelSendMessage() {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_LABEL_SEND");

	}

	public static String getMessageErrorBeginSync(String endpointId, String sourceId) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_ERROR_BEGIN_SYNC", endpointId, sourceId);
	}

	public static String getMessageCancelSync(String sessionId, String endpointId, String sourceId) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_CANCEL_SYNC", sessionId, endpointId, sourceId);
	}

	public static String getMessageCancelSyncErrorSessionNotOpen(IEndpoint endpoint, String sourceId) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_ERROR_CANCEL_SYNC_SESSION_NOT_OPEN", endpoint.getEndpointId(), sourceId);
	}
	
	public static String getMessageInvalidMessageProtocol(IMessageSyncProtocol syncProtocol, IMessage message) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_ERROR_INVALID_PROTOCOL", message.getProtocol(), translateMessageType(syncProtocol, message) + " session: " + message.getSessionId() + " endpoint: " + message.getEndpoint().getEndpointId());
	}
	
	public static String getMessageErrorInvalidProtocolMessageOrder(IMessageSyncProtocol syncProtocol, IMessage message) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_ERROR_INVALID_PROTOCOL_MESSAGE_ORDER", translateMessageType(syncProtocol, message), message.getSessionId(), message.getEndpoint().getEndpointId());	
	}

	public static String getMessageErrorSessionCreation(IMessageSyncProtocol syncProtocol, IMessage message, String sourceId) {
		return MessageTranslator.translate("MESH_SMS_MESSAGE_ERROR_SESSION_CREATION", translateMessageType(syncProtocol, message), message.getSessionId(), message.getEndpoint().getEndpointId(), sourceId);
	}
	
	private static String translateMessageType(IMessageSyncProtocol syncProtocol, IMessage message){
		String messageType = message.getMessageType();
		if(BeginSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return  MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_BEGIN_SYNC");
		}
		if(NoChangesMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_NO_CHANGES");
		}
		if(LastVersionStatusMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_LAST_VERSION");
		}
		if(GetForMergeMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			String syncId = GetForMergeMessageProcessor.getSyncID(message.getData());
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_GET_FOR_MERGE", syncId);
		}
		if(MergeMessageProcessor.MESSAGE_TYPE.equals(messageType)){			
			MergeMessageProcessor messageProcessor = (MergeMessageProcessor)syncProtocol.getMessageProcessor(MergeMessageProcessor.MESSAGE_TYPE);
			String syncId = messageProcessor.getSyncID(message.getData());
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_MERGE", syncId);
		}
		if(MergeWithACKMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			MergeWithACKMessageProcessor messageProcessor = (MergeWithACKMessageProcessor)syncProtocol.getMessageProcessor(MergeWithACKMessageProcessor.MESSAGE_TYPE);
			String syncId = messageProcessor.getSyncID(message.getData());
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_MERGE_WITH_ACK", syncId);
		}
		if(EndSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_END_SYNC");
		}
		if(ACKEndSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_ACK_END_SYNC");
		}
		if(ACKMergeMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			ACKMergeMessageProcessor messageProcessor = (ACKMergeMessageProcessor)syncProtocol.getMessageProcessor(ACKMergeMessageProcessor.MESSAGE_TYPE);
			String syncId = messageProcessor.getSyncID(message.getData());

			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_ACK_MERGE", syncId);
		}
		if(CancelSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_CANCEL", message.getSessionId());
		}
		if(EqualStatusMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("MESH_SMS_MESSAGE_TYPE_EQUAL_STATUS");
		}
		return messageType;
	}

	public static String getMessageProcessed(IMessageSyncProtocol syncProtocol, IMessage message, List<IMessage> response) {
		StringBuffer sb = new StringBuffer();
		if(response.size() > 0){
			IMessage responseMessage;
			
			if(response.size() == 1){
				responseMessage = response.get(0);
				sb.append("\n   ");
				sb.append(MessageTranslator.translate("MESH_SMS_MESSAGE_RESPONSE", 1, translateMessageType(syncProtocol, responseMessage)));
			} else {
				for (int i = 0; i < response.size(); i++) {
					responseMessage = response.get(i);
					sb.append("\n   ");
					sb.append(MessageTranslator.translate("MESH_SMS_MESSAGE_RESPONSE", i, translateMessageType(syncProtocol, responseMessage)));
				}
			}
		}
		return MessageTranslator.translate("MESH_SMS_MESSAGE_PROCESSED", 
				translateMessageType(syncProtocol, message), 
				message.getOrigin(),
				message.getSessionId(),
				message.getEndpoint().getEndpointId()) + sb.toString();
	}

	public static String getLabelCancelSyn() {
		return MessageTranslator.translate("MESH_SMS_LABEL_CANCEL_SYNCHRONIZE");
	}

	public static String getLabelStart() {
		return MessageTranslator.translate("MESH_SYNC_LABEL_START");
	}

	public static String getLabelFailed() {
		return MessageTranslator.translate("MESH_SYNC_LABEL_FAILED");
	}

	public static String getLabelSuccess() {
		return MessageTranslator.translate("MESH_SYNC_LABEL_SUCCESS");
	}

	public static String getLabelSyncEndWithConflicts(int conflicts) {
		return MessageTranslator.translate("MESH_SYNC_COMPLETED_WITH_CONFLICTS", conflicts);
	}

	public static String getLabelCleanConsole() {
		return MessageTranslator.translate("MESH_SMS_LABEL_CLEAN_CONSOLE");
	}

	public static String getStatusBeginSync(String target, String dataSource, String tableName, Date date) {
		return MessageTranslator.translate("MESH_STATUS_BEGIN_SYNC", target, dataSource, tableName, date);
	}

	public static String getStatusEndSync(String target, String dataSource, String tableName, Date date) {
		return MessageTranslator.translate("MESH_STATUS_END_SYNC", target, dataSource, tableName, date);
	}

	public static String getStatusEndSyncWithConflicts(String target, String dataSource, String tableName, Date date, int conflicts) {
		return MessageTranslator.translate("MESH_STATUS_END_SYNC_WITH_CONFLICTS", target, dataSource, tableName, date, conflicts);
	}
	
	public static String getStatusCancelSync(IEndpoint target, String dataSource, String tableName, Date date) {
		return MessageTranslator.translate("MESH_STATUS_CANCEL_SYNC", target.getEndpointId(), dataSource, tableName, date);
	}

	public static String getLabelDataSourceFileExtensions() {
		return MessageTranslator.translate("MESH_LABEL_DATA_SOURCE_FILE_EXTENSIONS");
	}

	public static String getLabelDemo() {
		return MessageTranslator.translate("MESH_LABEL_DEMO");
	}

	public static String getLabelModemDiscovery() {
		return MessageTranslator.translate("MESH_LABEL_MODEM_DISCOVERY");
	}

	public static String getLabelURL() {
		return MessageTranslator.translate("MESH_LABEL_URL");
	}

	public static String getLabelChannelSMS() {
		return MessageTranslator.translate("MESH_LABEL_CHANNEL_SMS");
	}

	public static String getLabelChannelWEB() {
		return MessageTranslator.translate("MESH_LABEL_CHANNEL_WEB");
	}

	public static String getLabelAddDataSource() {
		return MessageTranslator.translate("MESH_LABEL_ADD_DATA_SOURCE");
	}

	public static String getLabelDeviceConnectionFailed(String device) {
		return MessageTranslator.translate("MESH_LABEL_DEVICE_CONNECTION_FAILED", device);
	}

	public static String getLabelSaveDefaults() {
		return MessageTranslator.translate("MESH_LABEL_SAVE_DEFAULTS");
	}

	public static String getLabelKML() {
		return MessageTranslator.translate("MESH_LABEL_KML_LOCAL");
	}
	
	public static String getLabelKMLWEB() {
		return MessageTranslator.translate("MESH_LABEL_KML_WEB");
	}

	public static String getLabelDownloadSchema() {
		return MessageTranslator.translate("MESH_LABEL_DOWNLOAD_SCHEMA");
	}

	public static String getLabelDownloadMappings() {
		return MessageTranslator.translate("MESH_LABEL_DOWNLOAD_MAPPINGS");
	}

	public static String getMessageCheckingModem(String portName, int baudRate) {
		return MessageTranslator.translate("MESH_MESSAGE_CHECKING_MODEM", portName, baudRate);
	}

	public static String getMessageCheckingPort(String portName, int baudRate) {
		return MessageTranslator.translate("MESH_MESSAGE_CHECKING_PORT", portName, baudRate);
	}

	public static String getMessageAvailableModem(Modem modem) {
		return MessageTranslator.translate("MESH_MESSAGE_AVAILABLE_MODEM", modem.toString());
	}

	public static String getLabelAvailable() {
		return MessageTranslator.translate("MESH_MESSAGE_AVAILABLE");
	}

	public static String getLabelNoAvailable() {
		return MessageTranslator.translate("MESH_MESSAGE_NO_AVAILABLE");
	}
	
	public static String getLabelStopModemDiscovery() {
		return MessageTranslator.translate("MESH_LABEL_STOP_MODEM_DISCOVERY");
	}
	
	public static String getMessageBeginModemDiscovery() {
		return MessageTranslator.translate("MESH_MESSAGE_BEGIN_MODEM_DISCOVERY");
	}

	public static String getMessageEndModemDiscovery(int availableModems) {
		if(availableModems == 0){
			return MessageTranslator.translate("MESH_MESSAGE_END_MODEM_DISCOVERY_NO_AVAILABLE");
		} else {
			return MessageTranslator.translate("MESH_MESSAGE_END_MODEM_DISCOVERY_AVAILABLE", availableModems);
		}
	}
	
	public static String getToolTipAutoDetect() {
		return MessageTranslator.translate("MESH_TOOLTIP_MODEM_DICOVERY");
	}

	public static String getToolTipStopAutoDetect() {
		return MessageTranslator.translate("MESH_TOOLTIP_MODEM_DICOVERY_STOP");
	}

	public static String getLabelKMLFailed() {
		return MessageTranslator.translate("MESH_LABEL_KML_FAILED");
	}

	public static String getLabelDownloadSchemaFailed() {
		return MessageTranslator.translate("MESH_LABEL_DONWLOAD_SCHEMA_FAILED");
	}

	public static String getErrorInvalidURL() {
		return MessageTranslator.translate("MESH_ERROR_INVALID_URL");
	}

	public static String getErrorInvalidMSAccessTable() {
		return MessageTranslator.translate("MESH_ERROR_INVALID_MS_ACCESS");
	}

	public static String getLabelShowConsole() {
		return MessageTranslator.translate("MESH_LABEL_SHOW_CONSOLE");
	}

	public static String getToolTipShowConsole() {
		return MessageTranslator.translate("MESH_TOOLTIP_SHOW_CONSOLE");
	}
	
	public static String getLabelHideConsole() {
		return MessageTranslator.translate("MESH_LABEL_HIDE_CONSOLE");
	}

	public static String getToolTipHideConsole() {
		return MessageTranslator.translate("MESH_TOOLTIP_HIDE_CONSOLE");
	}

	public static String getLabelTabDataExchange() {
		return MessageTranslator.translate("MESH_LABEL_TAB_DATA_EXCHANGE");
	}

	public static String getLabelTabMap() {
		return MessageTranslator.translate("MESH_LABEL_TAB_MAP_EXCHANGE");
	}

	public static String getLabelTabSettings() {
		return MessageTranslator.translate("MESH_LABEL_TAB_SETTINGS");
	}

	public static String getErrorKMLMappingsNotFound() {
		return MessageTranslator.translate("MESH_ERROR_KML_MAPPINGS_NOT_FOUND");
	}
	
	public static String getErrorKMLSchemaNotFound() {
		return MessageTranslator.translate("MESH_ERROR_KML_SCHEMA_NOT_FOUND");
	}

	public static String getLabelDownloadMappingsFailed() {
		return MessageTranslator.translate("MESH_LABEL_DONWLOAD_MAPPINGS_FAILED");
	}

}
