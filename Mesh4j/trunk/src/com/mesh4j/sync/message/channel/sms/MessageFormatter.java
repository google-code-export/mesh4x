package com.mesh4j.sync.message.channel.sms;

import org.apache.commons.lang.StringUtils;

public class MessageFormatter {

	public static String getMessageType(String message){
		return message.substring(0, 1);
	}
	
	public static String getDataSetId(String message) {
		return message.substring(1, 6);
	}

	public static String getData(String message) {
		if(message.length() >= 6){
			return message.substring(6, message.length());
		} else {
			return null;
		}
	}
	
	public static String createMessage(String messageType, String dataSetId, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(messageType);
		sb.append(StringUtils.leftPad(dataSetId, 5, "0"));
		sb.append(data);
		return sb.toString();
	}

	public static String getProtocol(String message){
		return message.substring(0, 1);
	}
	
	public static String getMessage(String messageText) {
		return messageText.substring(1, messageText.length());
	}
	
	public static int getBatchHeaderLenght(){
		return 8;
	}	
	
	public static String createBatchMessage(String protocolHeader, String batchId, int expected, int sequence, String messagetext) {
		String expectedString = String.valueOf(expected);
		String sequenceString = String.valueOf(sequence);
		return protocolHeader + batchId + expectedString + sequenceString + messagetext;
	}
	
	public static String getBatchProtocolHeader(String messageText) {
		return messageText.substring(0, 1);
	}
	
	public static String getBatchId(String messageText) {
		return messageText.substring(1, 6);
	}

	public static int getBatchExpectedMessageCount(String messageText) {
		return new Integer(messageText.substring(6, 7));
	}

	public static  int getBatchMessageSequenceNumber(String messageText) {
		return new Integer(messageText.substring(7, 8));
	}
}
