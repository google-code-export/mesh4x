package org.mesh4j.sync.ui.translator;

import java.util.List;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.protocol.ACKEndSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.ACKMergeMessageProcessor;
import org.mesh4j.sync.message.protocol.BeginSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.CancelSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.EndSyncMessageProcessor;
import org.mesh4j.sync.message.protocol.GetForMergeMessageProcessor;
import org.mesh4j.sync.message.protocol.ItemEncoding;
import org.mesh4j.sync.message.protocol.LastVersionStatusMessageProcessor;
import org.mesh4j.sync.message.protocol.MergeMessageProcessor;
import org.mesh4j.sync.message.protocol.MergeWithACKMessageProcessor;
import org.mesh4j.sync.message.protocol.NoChangesMessageProcessor;
import org.mesh4j.sync.translator.MessageTranslator;

public class Mesh4jSmsUITranslator {

	public static String getTitle() {
		return MessageTranslator.translate("Mesh4jUI_SMS_TITLE");
	}

	public static String getLabelKMLFile() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_KML_FILE");
	}

	public static String getLabelPhone() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_PHONE");
	}

	public static String getLabelPhoneDestination() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_PHONE_DESTINATION");
	}

	public static String getLabelCompressMessage() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_COMPRESS_MESSAGE");
	}

	public static String getLabelSynchronize() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SYNCHRONIZE");
	}

	public static String getMessageErrorKMLFile() {
		return MessageTranslator.translate("Mesh4jUI_SMS_ERROR_KML_FILE");
	}

	public static String getMessageErrorPhoneNumberDestination() {
		return MessageTranslator.translate("Mesh4jUI_SMS_ERROR_PHONE_NUBER_DESTINATION");
	}

	public static String getLabelStart() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_START");
	}

	public static String getLabelFailed() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_FAILED");
	}

	public static String getLabelSuccess() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SUCCESS");
	}

	public static String getLabelSimulate() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SIMULATE");
	}

	public static String getLabelDemo() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_DEMO");
	}

	public static String getMessageNotifySendMessage(String endpointId,
			String message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_NOTIFY_SEND_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessage(String endpointId,
			String message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessageError(String endpointId,
			String message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getMessageNotifySendMessageError(String endpointId,
			String message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getLabelMessageToSend() {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_LABEL_MESSAGE_TO_SEND");
	}

	public static String getLabelSendMessage() {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_LABEL_SEND");

	}

	public static String getMessageErrorBeginSync(String endpointId, String sourceId) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_ERROR_BEGIN_SYNC", endpointId, sourceId);
	}

	public static String getMessageCancelSync(String sessionId, String endpointId, String sourceId) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_CANCEL_SYNC", sessionId, endpointId, sourceId);
	}

	public static String getMessageCancelSyncErrorSessionNotOpen(IEndpoint endpoint, String sourceId) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_ERROR_CANCEL_SYNC_SESSION_NOT_OPEN", endpoint, sourceId);
	}
	
	public static String getMessageInvalidMessageProtocol(IMessage message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_ERROR_INVALID_PROTOCOL", message.getProtocol(), translateMessageType(message) + " session: " + message.getSessionId() + " endpoint: " + message.getEndpoint().getEndpointId());
	}
	
	public static String getMessageErrorInvalidProtocolMessageOrder(IMessage message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_ERROR_INVALID_PROTOCOL_MESSAGE_ORDER", translateMessageType(message), message.getSessionId(), message.getEndpoint().getEndpointId());	
	}

	public static String getMessageErrorSessionCreation(IMessage message, String sourceId) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_ERROR_SESSION_CREATION", translateMessageType(message), message.getSessionId(), message.getEndpoint().getEndpointId(), sourceId);
	}
	
	
	private static String translateMessageType(IMessage message){
		String messageType = message.getMessageType();
		if(BeginSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return "Begin sync";
		}
		if(NoChangesMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return "No Changes";
		}
		if(LastVersionStatusMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return "Last Items Versions";
		}
		if(GetForMergeMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			String syncId = GetForMergeMessageProcessor.getSyncID(message.getData());
			return "Get For Merge: " + syncId;
		}
		if(MergeMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			String syncId = ItemEncoding.getSyncID(message.getData());
			return "Merge: " + syncId;
		}
		if(MergeWithACKMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			String itemData = message.getData().substring(1, message.getData().length());
			String syncId = ItemEncoding.getSyncID(itemData);
			return "Merge with ACK: " + syncId;
		}
		if(EndSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return "End sync";
		}
		if(ACKEndSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return "ACK of End Sync";
		}
		if(ACKMergeMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			String itemData = message.getData().substring(1, message.getData().length());
			String syncId = ItemEncoding.getSyncID(itemData);
			return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_TYPE_ACK_MERGE", syncId);
		}
		if(CancelSyncMessageProcessor.MESSAGE_TYPE.equals(messageType)){
			return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_TYPE_CANCEL", message.getSessionId());
		}
		return messageType;
	}

	public static String getMessageProcessed(IMessage message, List<IMessage> response) {
		StringBuffer sb = new StringBuffer();
		if(response.size() > 0){
			IMessage responseMessage;
			
			if(response.size() == 1){
				responseMessage = response.get(0);
				sb.append("\n   ");
				sb.append(MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_RESPONSE", 1, translateMessageType(responseMessage)));
			} else {
				for (int i = 0; i < response.size(); i++) {
					responseMessage = response.get(i);
					sb.append("\n   ");
					sb.append(MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_RESPONSE", i, translateMessageType(responseMessage)));
				}
			}
		}
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_PROCESSED", 
				translateMessageType(message), 
				message.getOrigin(),
				message.getSessionId(),
				message.getEndpoint().getEndpointId()) + sb.toString();
	}

	public static String getLabelCancelSyn() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_CANCEL_SYNCHRONIZE");
	}

	public static String getLabelSynGroup() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SYNC_GROUP");
	}

	public static String getLabelSimulationGroup() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SIMULATION_GROUP");
	}

	public static String getLabelSMSGroup() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SMS_GROUP");
	}

	public static String getLabelForceReceiveMessages() {
		return MessageTranslator.translate("Mesh4jUI_SMS_LABEL_SMS_FORCE_READ_MESSAGES");
	}

	public static String getMessageEnterTextMessage() {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_ENTER_TEXT");
	}

}