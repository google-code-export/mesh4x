package org.mesh4j.sync.message.channel.sms.batch;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.MessageFormatter;
import org.mesh4j.sync.utils.IdGenerator;
import org.mesh4j.sync.validations.Guard;


public class SmsMessageBatch {

	// MODEL VARIABLES
	private String id = "";
	private String sessionId = "";
	private String protocolHeader = "";
	private int expectedMessageCount = 0;
	private HashMap<Integer, SmsMessage> messages = new HashMap<Integer, SmsMessage>();
	private String payload;
	private SmsEndpoint endpoint;

	// BUSINESS METHODS

	public SmsMessageBatch(String sessionId, SmsEndpoint endpoint) {
		
		Guard.argumentNotNull(sessionId, "sessionId");
		Guard.argumentNotNull(endpoint, "endpoint");
		
		this.sessionId = sessionId;
		this.id = this.generateNewId();
		this.endpoint = endpoint;
	}

	public SmsMessageBatch(String sessionId, SmsEndpoint endpoint, String protocolHeader, String messageBatchId, int expectedMessageCount) {
		Guard.argumentNotNull(sessionId, "sessionId");
		Guard.argumentNotNull(endpoint, "endpoint");
		Guard.argumentNotNull(protocolHeader, "protocolHeader");
		Guard.argumentNotNull(messageBatchId, "messageBatchId");
		
		this.sessionId = sessionId;
		this.protocolHeader = protocolHeader;
		this.id = messageBatchId;
		this.expectedMessageCount = expectedMessageCount;
		this.endpoint = endpoint;
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

	public Date getDateTimeFirstMessage() {
		Date min = null;

		for (SmsMessage msg : this.messages.values()) {
			if (min == null || msg.getLastModificationDate().before(min)) {
				min = msg.getLastModificationDate();
			}
		}

		return min;
	}

	public Date getDateTimeLastMessage() {
		Date max = null;

		for (SmsMessage msg : this.messages.values()) {
			if (max == null || msg.getLastModificationDate().after(max)) {
				max = msg.getLastModificationDate();
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

	public List<SmsMessage> getMessages() {
		return new ArrayList<SmsMessage>(this.messages.values());
	}

	public int getExpectedMessageCount() {
		return this.expectedMessageCount;
	}

	public String getProtocolHeader() {
		return this.protocolHeader;
	}

	public SmsEndpoint getEndpoint() {
		return endpoint;
	}

	public String getSessionId() {
		return sessionId;
	}
}
