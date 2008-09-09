package org.sms.exchanger.message.repository;

import java.util.List;

public interface IMessageRepository {

	void open();
	
	void close();

	List<Message> getAllMessagesToSend();
	
	boolean addMessage(Message message);

	boolean deleteMessage(Message message);

}
