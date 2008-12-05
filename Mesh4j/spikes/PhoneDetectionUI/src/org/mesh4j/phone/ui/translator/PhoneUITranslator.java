package org.mesh4j.phone.ui.translator;

import java.util.Date;

import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.mesh4j.sync.translator.MessageTranslator;


public class PhoneUITranslator {

	public static String getLabelTitle() {
		return MessageTranslator.translate("PHONE_TITLE");
	}

	public static String getToolTipConsoleView() {
		return MessageTranslator.translate("PHONE_TOOLTIP_CONSOLE_VIEW");
	}

	public static String getLabelDevice() {
		return MessageTranslator.translate("PHONE_LABEL_DEVICE");
	}

	public static String getToolTipDevice() {
		return MessageTranslator.translate("PHONE_TOOLTIP_DEVICE");
	}

	public static String getToolTipAutoDetect() {
		return MessageTranslator.translate("PHONE_TOOLTIP_MODEM_DICOVERY");
	}

	public static String getToolTipStopAutoDetect() {
		return MessageTranslator.translate("PHONE_TOOLTIP_MODEM_DICOVERY_STOP");
	}

	
	public static String getToolTipReadMessages() {
		return MessageTranslator.translate("PHONE_TOOLTIP_READ_MESSAGES");
	}

	public static String getLabelReadMessages() {
		return MessageTranslator.translate("PHONE_LABEL_READ_MESSAGES");
	}

	public static String getLabelPhoneNumber() {
		return MessageTranslator.translate("PHONE_LABEL_PHONE_NUMBER");
	}

	public static String getLabelMessage() {
		return MessageTranslator.translate("PHONE_LABEL_MESSAGE_TO_SEND");
	}
	
	public static String getToolTipPhoneNumber() {
		return MessageTranslator.translate("PHONE_TOOLTIP_PHONE_NUMBER");
	}

	public static String getToolTipMessage() {
		return MessageTranslator.translate("PHONE_TOOLTIP_MESSAGE_TO_SEND");
	}

	public static String getToolTipSendMessage() {
		return MessageTranslator.translate("PHONE_TOOLTIP_SEND_MESSAGE");
	}
	
	public static String getLabelSendMessage() {
		return MessageTranslator.translate("PHONE_LABEL_SEND_MESSAGE");
	}

	public static String getToolTipCleanConsole() {
		return MessageTranslator.translate("PHONE_TOOLTIP_CLEAN_CONSOLE_VIEW");
	}
	
	public static String getLabelCleanConsole() {
		return MessageTranslator.translate("PHONE_LABEL_CLEAN_CONSOLE_VIEW");
	}

	public static String getMessageCheckingModem(String portName, int baudRate) {
		return MessageTranslator.translate("PHONE_MESSAGE_CHECKING_MODEM", portName, baudRate);
	}

	public static String getMessageCheckingPort(String portName, int baudRate) {
		return MessageTranslator.translate("PHONE_MESSAGE_CHECKING_PORT", portName, baudRate);
	}

	public static String getMessageAvailableModem(Modem modem) {
		return MessageTranslator.translate("PHONE_MESSAGE_AVAILABLE_MODEM", modem.toString());
	}

	public static String getLabelAvailable() {
		return MessageTranslator.translate("PHONE_MESSAGE_AVAILABLE");
	}

	public static String getLabelNoAvailable() {
		return MessageTranslator.translate("PHONE_MESSAGE_NO_AVAILABLE");
	}

	
	public static String getMessageNotifySendMessage(String endpointId, String message) {
		return MessageTranslator.translate("PHONE_MESSAGE_NOTIFY_SEND_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessage(String endpointId, String message) {
		return MessageTranslator.translate("PHONE_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessageError(String endpointId, String message) {
		return MessageTranslator.translate("PHONE_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getMessageNotifySendMessageError(String endpointId, String message) {
		return MessageTranslator.translate("PHONE_MESSAGE_NOTIFY_RECEIVE_MSG", endpointId, message);
	}

	public static String getMessageNotifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		return MessageTranslator.translate("PHONE_MESSAGE_NOTIFY_RECEIVE_MSG_WAS_NOT_PROCESSED", endpoint, message, date.toString());
	}
	
	public static String getLabelModemDiscovery() {
		return MessageTranslator.translate("PHONE_LABEL_MODEM_DISCOVERY");
	}

	public static String getLabelStopModemDiscovery() {
		return MessageTranslator.translate("PHONE_LABEL_STOP_MODEM_DISCOVERY");
	}
	
	public static String getMessageBeginModemDiscovery() {
		return MessageTranslator.translate("PHONE_MESSAGE_BEGIN_MODEM_DISCOVERY");
	}

	public static String getMessageEndModemDiscovery(int availableModems) {
		if(availableModems == 0){
			return MessageTranslator.translate("PHONE_MESSAGE_END_MODEM_DISCOVERY_NO_AVAILABLE");
		} else {
			return MessageTranslator.translate("PHONE_MESSAGE_END_MODEM_DISCOVERY_AVAILABLE", availableModems);
		}
	}

	public static String getMessageBeginReadMessages() {
		return MessageTranslator.translate("PHONE_MESSAGE_BEGIN_READ_MESSAGES");
	}

	public static String getMessageEndReadMessages() {
		return MessageTranslator.translate("PHONE_MESSAGE_END_READ_MESSAGES");
	}

	public static String getLabelSaveDefaults() {
		return MessageTranslator.translate("PHONE_LABEL_SAVE_DEFAULTS");
	}

	public static String getToolTipSaveDefaults() {
		return MessageTranslator.translate("PHONE_TOOLTIP_SAVE_DEFAULTS");
	}

	public static String getMessageSendWasAddedToOutboxQueue(String smsNumber, String message) {
		return MessageTranslator.translate("PHONE_MESSAGE_SEND_MSG_WAS_ADDED_OUTBOX", smsNumber, message);
	}
}
