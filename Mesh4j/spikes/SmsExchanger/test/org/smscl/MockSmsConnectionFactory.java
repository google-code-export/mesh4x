package org.smscl;

import org.sms.exchanger.connection.IMessageNotification;
import org.sms.exchanger.connection.ISmsConnection;
import org.sms.exchanger.connection.factory.ISmsConnectionFactory;
import org.sms.exchanger.properties.PropertiesProvider;

public class MockSmsConnectionFactory implements ISmsConnectionFactory {

	@Override
	public ISmsConnection createConnection(PropertiesProvider prop, IMessageNotification messageNotification) {
		return new MockSmsConnection(messageNotification);
	}

}
