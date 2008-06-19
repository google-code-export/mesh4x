package com.mesh4j.sync.message.channel.sms;

import java.util.StringTokenizer;


public class MessageFormatter {

	private final static String ELEMENT_SEPARATOR = "&";
	
	public static String getMessageType(String message){
		return message.substring(0, 1);
	}
	
	public static String getSessionId(String message) {
		String data =  message.substring(1, message.length());
		StringTokenizer st = new StringTokenizer(data, ELEMENT_SEPARATOR);
		return st.nextToken();
	}

	public static String getData(String message) {
		String data =  message.substring(1, message.length());
		StringTokenizer st = new StringTokenizer(data, ELEMENT_SEPARATOR);
		st.nextToken();			// skip session id
		if(st.hasMoreTokens()){
			return st.nextToken();
		} else {
			return "";
		}
	}
	
	public static String createMessage(String messageType, String sessionId, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(messageType);
		sb.append(sessionId);
		if(data != null && !data.isEmpty()){
			sb.append(ELEMENT_SEPARATOR);
			sb.append(data);
		}
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
