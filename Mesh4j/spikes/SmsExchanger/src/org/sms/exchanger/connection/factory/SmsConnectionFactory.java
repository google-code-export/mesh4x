package org.sms.exchanger.connection.factory;

import org.sms.exchanger.IProperties;
import org.sms.exchanger.connection.IMessageNotification;
import org.sms.exchanger.connection.ISmsConnection;
import org.sms.exchanger.connection.SmsConnection;
import org.sms.exchanger.properties.PropertiesProvider;

public class SmsConnectionFactory implements ISmsConnectionFactory {

	@Override
	public ISmsConnection createConnection(PropertiesProvider prop, IMessageNotification messageNotification) {
		String portName = prop.getString(IProperties.SMS_PORT);
		if(portName == null || portName.length() == 0){
			throw new IllegalArgumentException("portName");
		}
		int baudRate = prop.getInt(IProperties.SMS_BAUD_RATE, IProperties.SMS_BAUD_RATE_DEFAULT_VALUE);
		int dstPort = prop.getInt(IProperties.SMS_MESSAGE_DESTINATION_PORT, IProperties.SMS_MESSAGE_DESTINATION_PORT_VALUE);
		int srcPort = prop.getInt(IProperties.SMS_MESSAGE_SOURCE_PORT, IProperties.SMS_MESSAGE_SOURCE_PORT_VALUE);
		return new SmsConnection(portName, baudRate, srcPort, dstPort, messageNotification);
	}

}
