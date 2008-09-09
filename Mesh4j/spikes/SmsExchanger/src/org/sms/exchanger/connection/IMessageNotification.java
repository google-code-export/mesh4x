package org.sms.exchanger.connection;

import org.sms.exchanger.message.repository.Message;

public interface IMessageNotification {

	boolean notifySentMessage(Message message);
	
	boolean notifyReceiveMessage(Message message);

	void notifyStartUpGateway();

}
