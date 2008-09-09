package org.smscl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.message.repository.Message;

public class MockMessageManager implements IMessageRepository {

	@Override
	public boolean addMessage(Message message) {
		System.out.println("MessageManager: add message " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		return true;
	}

	@Override
	public void close() {
		System.out.println("MessageManager: close");

	}

	@Override
	public List<Message> getAllMessagesToSend() {
		ArrayList<Message> result = new ArrayList<Message>();
		result.add(new Message("2", "22223442244", "By !!!", new Date()));
		return result;
	}

	@Override
	public boolean deleteMessage(Message message) {
		System.out.println("MessageManager: delete message " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		return true;
	}

	@Override
	public void open() {
		System.out.println("MessageManager: open");

	}

}
