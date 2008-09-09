package org.sms.exchanger.connection.factory;

import org.sms.exchanger.connection.IMessageNotification;
import org.sms.exchanger.connection.ISmsConnection;
import org.sms.exchanger.properties.PropertiesProvider;

public interface ISmsConnectionFactory {

	ISmsConnection createConnection(PropertiesProvider prop, IMessageNotification messageNotification);

}
