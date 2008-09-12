package org.sms.exchanger.connection;

import java.util.List;

import org.sms.exchanger.message.repository.Message;

public interface ISmsConnection {

	void connect() throws Exception;

	void disconnect() throws Exception;

	void sendMessage(Message message) throws Exception;
	
	List<Message> getReadMessages() throws Exception;
	
	List<Message> getUnreadMessages() throws Exception;

	List<Message> getAllMessages() throws Exception;
	
	String newMessageID();

}
