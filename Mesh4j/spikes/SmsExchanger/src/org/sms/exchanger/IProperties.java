package org.sms.exchanger;

public interface IProperties {

	public final static String WATCHER_PERIOD = "watcher.period";
	public final static String WATCHER_PERIOD_DEFAULT_VALUE = "30000";
	
	public final static String READ_MODE = "read.mode";
	
	public static final String REPOSITORY_FACTORY = "repository.factory";
	public static final String CONNECTION_FACTORY = "connection.factory";
	
	public static final String SMS_PORT = "port";
	public static final String SMS_BAUD_RATE = "baud.rate";
	public static final String SMS_BAUD_RATE_DEFAULT_VALUE = "115200";
	
	public static final String SMS_MESSAGE_DESTINATION_PORT = "msg.dest.port";
	public static final String SMS_MESSAGE_DESTINATION_PORT_VALUE = "-1";
	public static final String SMS_MESSAGE_SOURCE_PORT = "msg.src.port";
	public static final String SMS_MESSAGE_SOURCE_PORT_VALUE = "-1";	
	
	public static final String INBOX_DIR = "inbox.directory";
	public static final String OUTBOX_DIR = "outbox.directory";
}
