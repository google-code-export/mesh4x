package com.mesh4j.sync.ui.translator;

import com.mesh4j.sync.translator.MessageTranslator;

public class Mesh4jSmsUITranslator {

//		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_NOTIFY_SEND");

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

	public static String getMessageNotifySend(String endpointIdFrom,
			String endpointIdTo, String message) {
		return MessageTranslator.translate("Mesh4jUI_SMS_MESSAGE_NOTIFY_SEND", endpointIdFrom, endpointIdTo, message);
	}

}
