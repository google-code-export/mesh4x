package org.mesh4j.sync.message.channel.sms.core;

import de.enough.polish.util.StringTokenizer;

public class MessageFormatter {

	private final static String ELEMENT_SEPARATOR = "&";
	
	public static String getMessageType(String message){
		return message.substring(0, 1);
	}
	
	public static int getSessionVersion(String message) {
		String data =  message.substring(1, message.length());
		StringTokenizer st = new StringTokenizer(data, ELEMENT_SEPARATOR);
		String sessionVersion = st.nextToken();
		int version = Integer.valueOf(sessionVersion);
		return version;
	}

	public static String getData(String message) {
		String data =  message.substring(1, message.length());
		StringTokenizer st = new StringTokenizer(data, ELEMENT_SEPARATOR);
		String sessionVersion = st.nextToken();
		if(st.hasMoreTokens()){
			return data.substring(sessionVersion.length() + 1, data.length());
		} else {
			return "";
		}
	}
	
	public static String createMessage(String messageType, int version, String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(messageType);
		sb.append(String.valueOf(version));
		if(data != null && data.length() > 0){
			sb.append(ELEMENT_SEPARATOR);
			sb.append(data);
		}
		return sb.toString();
	}

	public static int getBatchHeaderLenght(){
		return 51;
	}	
	
	public static String createBatchMessage(String sessionId, String protocolHeader, String batchId, String ackBatchId, int expected, int sequence, String messagetext) {
		String expectedString = String.valueOf(expected);
		if(expected <10){
			expectedString = "0" + expectedString; 
		}
		String sequenceString = String.valueOf(sequence);
		if(sequence <10){
			sequenceString = "0" + sequenceString;
		}
		return protocolHeader + batchId + expectedString + sequenceString + ackBatchId + sessionId + messagetext;
	}
	
	public static String getBatchProtocolHeader(String messageText) {
		return messageText.substring(0, 1);
	}
	
	public static String getBatchId(String messageText) {
		return messageText.substring(1, 6);
	}

	public static int getBatchExpectedMessageCount(String messageText) {
		return Integer.valueOf(messageText.substring(6, 8));
	}

	public static int getBatchMessageSequenceNumber(String messageText) {
		return Integer.valueOf(messageText.substring(8, 10));
	}

	public static String getBatchACK(String messageText) {
		return messageText.substring(10, 15);
	}

	public static String getBatchSessionId(String messageText) {
		return messageText.substring(15, 51);
	}
}
