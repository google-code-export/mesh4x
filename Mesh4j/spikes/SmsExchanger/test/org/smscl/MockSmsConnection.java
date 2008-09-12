package org.smscl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sms.exchanger.connection.IMessageNotification;
import org.sms.exchanger.connection.ISmsConnection;
import org.sms.exchanger.message.repository.Message;

public class MockSmsConnection implements ISmsConnection {

	private IMessageNotification messageNotification;
	
	public MockSmsConnection(IMessageNotification messageNotification) {
		super();
		this.messageNotification = messageNotification;
	}

	@Override
	public void connect() throws Exception {
		System.out.println("Connection: connect...");
	}

	@Override
	public void disconnect() throws Exception {
		System.out.println("Connection: disconnect...");

	}

	@Override
	public List<Message> getUnreadMessages() throws Exception {
		ArrayList<Message> result = new ArrayList<Message>();
		result.add(new Message("123", "1234567890", "Hellow!!!", new Date()));
		return result;
	}

	@Override
	public void sendMessage(Message message) throws Exception {
		System.out.println("Connection: sent message: " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		this.messageNotification.notifySentMessage(message);
	}

	@Override
	public List<Message> getAllMessages() throws Exception {
		ArrayList<Message> result = new ArrayList<Message>();
		result.add(new Message("123", "1234567890", "Hellow!!!", new Date()));
		return result;
	}

	@Override
	public List<Message> getReadMessages() throws Exception {
		ArrayList<Message> result = new ArrayList<Message>();
		result.add(new Message("123", "1234567890", "Hellow!!!", new Date()));
		return result;
	}

	@Override
	public String newMessageID() {
		return String.valueOf(System.nanoTime());
	}

}
