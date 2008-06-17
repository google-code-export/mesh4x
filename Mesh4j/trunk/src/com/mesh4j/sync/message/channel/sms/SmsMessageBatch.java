package com.mesh4j.sync.message.channel.sms;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import com.mesh4j.sync.utils.IdGenerator;

public class SmsMessageBatch {

	// MODEL VARIABLES
	private String id = "";
	private String protocolHeader = "";
	private int expectedMessageCount = 0;
	private HashMap<Integer, SmsMessage> messages = new HashMap<Integer, SmsMessage>();
	private String payload;

	// BUSINESS METHODS

	public SmsMessageBatch() {
		super();
		this.id = this.generateNewId();
	}

	public SmsMessageBatch(String protocolHeader, String messageBatchId, int expectedMessageCount) {
		this.protocolHeader = protocolHeader;
		this.id = messageBatchId;
		this.expectedMessageCount = expectedMessageCount;
	}

	private String generateNewId() {
		return IdGenerator.newID().substring(0, 5);
	}

	public SmsMessageBatch reconstitutePayload() {
		String tempPayload = "";

		for (int i = 0; i < this.messages.size(); i++) {
			String msg = this.messages.get(i).getText();
			tempPayload = tempPayload + msg.substring(MessageFormatter.getBatchHeaderLenght(), msg.length());
		}
		this.payload = tempPayload;
		return this;
	}

	public boolean isComplete() {
		boolean complete = (this.messages.size() == this.expectedMessageCount);
		return complete;
	}

	public Date getDateTimeFirstMessageReceived() {
		Date min = null;

		for (SmsMessage msg : this.messages.values()) {
			if (min == null || msg.getReceived().before(min)) {
				min = msg.getReceived();
			}
		}

		return min;
	}

	public Date getDateTimeLastMessageReceived() {
		Date max = null;

		for (SmsMessage msg : this.messages.values()) {
			if (max == null || msg.getReceived().after(max)) {
				max = msg.getReceived();
			}
		}
		return max;
	}

	public String getId() {
		return id;
	}

	public void addMessage(int sequence, SmsMessage message) {
		this.messages.put(sequence, message);
	}

	public SmsMessage getMessage(int sequence) {
		return this.messages.get(sequence);
	}

	public SmsMessageBatch setPayload(String payload) {
		this.payload = payload;
		return this;
	}

	public SmsMessageBatch setProtocolHeader(String protocolHeader) {
		this.protocolHeader = protocolHeader;
		return this;
	}

	
	public int getMessagesCount() {
		return this.messages.size();
	}

	public SmsMessageBatch setExpectedMessageCount(int expectedMessageCount) {
		this.expectedMessageCount = expectedMessageCount;
		return this;
	}

	public String getPayload() {
		return payload;
	}

	public Collection<SmsMessage> getMessages() {
		return this.messages.values();
	}

	public int getExpectedMessageCount() {
		return this.expectedMessageCount;
	}

	public String getProtocolHeader() {
		return this.protocolHeader;
	}
}
