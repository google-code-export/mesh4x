package com.mesh4j.sync.message.protocol;

import org.apache.commons.lang.StringUtils;

public class MessageFormatter {

	public static String getData(String message) {
		if(message.length() >= 8){
			return message.substring(8, message.length());
		} else {
			return null;
		}
	}
	
	public static String getDataSetId(String message) {
		return message.substring(3, 8);
	}
	
	public static String getMessageType(String message){
		return message.substring(2, 3);
	}
	
	public static String getProtocol(String message){
		return message.substring(0, 1);
	}
	
	public static String getVersion(String message){
		return message.substring(1, 2);
	}
		
	public static String createMessageHeader(String dataSetId, String messageType) {
		StringBuffer sb = new StringBuffer();
		sb.append(MessageSyncProtocol.PREFIX);
		sb.append(MessageSyncProtocol.VERSION);
		sb.append(messageType);
		sb.append(StringUtils.leftPad(dataSetId, 5, "0"));
		return sb.toString();
	}

	public static int getHeaderLenght() {
		return 8;
	}

	public static String createMessage(String dataSetId, String messageType,
			String data) {
		StringBuffer sb = new StringBuffer();
		sb.append(MessageSyncProtocol.PREFIX);
		sb.append(MessageSyncProtocol.VERSION);
		sb.append(messageType);
		sb.append(StringUtils.leftPad(dataSetId, 5, "0"));
		sb.append(data);
		return sb.toString();
	}
}
