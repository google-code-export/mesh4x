package org.sms.exchanger.message.repository;

import java.util.List;

public interface IMessageRepository {

	void open();
	
	void close();

	List<Message> getIncommingMessages();
	boolean addIncommingMessage(Message message);
	boolean deleteIncommingMessage(Message message);

	List<Message> getOutcommingMessages();
	boolean addOutcommingMessage(Message message);
	boolean deleteOutcommingMessage(Message message);
}
