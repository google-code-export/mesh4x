package org.smscl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.message.repository.Message;

public class MockMessageManager implements IMessageRepository {

	@Override
	public void close() {
		System.out.println("MessageManager: close");

	}

	@Override
	public void open() {
		System.out.println("MessageManager: open");

	}

	@Override
	public boolean addIncommingMessage(Message message) {
		System.out.println("MessageManager: add message " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		return true;
	}

	@Override
	public boolean addOutcommingMessage(Message message) {
		System.out.println("MessageManager: add message " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		return true;
	}

	@Override
	public boolean deleteIncommingMessage(Message message) {
		System.out.println("MessageManager: delete message " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		return true;
	}

	@Override
	public boolean deleteOutcommingMessage(Message message) {
		System.out.println("MessageManager: delete message " + message.getID() + " - " + message.getNumber() + " _ " + message.getText());
		return true;
	}

	@Override
	public List<Message> getIncommingMessages() {
		ArrayList<Message> result = new ArrayList<Message>();
		result.add(new Message("2", "22223442244", "By !!!", new Date()));
		return result;
	}

	@Override
	public List<Message> getOutcommingMessages() {
		ArrayList<Message> result = new ArrayList<Message>();
		result.add(new Message("2", "22223442244", "By !!!", new Date()));
		return result;
	}

}
