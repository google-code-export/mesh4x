package org.mesh4j.sync.message.channel.sms.batch;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.core.MessageFormatter;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.HashMap;


public class SmsMessageBatch {

	// MODEL VARIABLES
	private String id = "";
	private String sessionId = "";
	private String protocolHeader = "";
	private int expectedMessageCount = 0;
	private HashMap messages = new HashMap();
	private String payload;
	private SmsEndpoint endpoint;
	private boolean discarded = false;
	private boolean waitingForAck = true;

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
		return IdGenerator.INSTANCE.newID().substring(0, 5);
	}

	public SmsMessageBatch reconstitutePayload() {
		String tempPayload = "";

		for (int i = 0; i < this.messages.size(); i++) {
			String msg = ((SmsMessage)this.messages.get(new Integer(i))).getText();
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

		SmsMessage msg = null;
		for (int i = 0; i < this.messages.values().length; i++) {
			msg = (SmsMessage)this.messages.values()[i];
			if (min == null || msg.getLastModificationDate().getTime() < min.getTime()) {
				min = msg.getLastModificationDate();
			}
		}

		return min;
	}

	public Date getDateTimeLastMessage() {
		Date max = null;

		SmsMessage msg = null;
		for (int i = 0; i < this.messages.values().length; i++) {
			msg = (SmsMessage)this.messages.values()[i];
				
			if (max == null || msg.getLastModificationDate().getTime() > max.getTime()) {
				max = msg.getLastModificationDate();
			}
		}
		return max;
	}

	public String getId() {
		return id;
	}

	public void addMessage(int sequence, SmsMessage message) {
		this.messages.put(new Integer(sequence), message);
	}

	public SmsMessage getMessage(int sequence) {
		return (SmsMessage)this.messages.get(new Integer(sequence));
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

	public Vector<SmsMessage> getMessages() {
		Vector<SmsMessage> result = new Vector<SmsMessage>();
		
		SmsMessage msg = null;
		for (int i = 0; i < this.messages.values().length; i++) {
			msg = (SmsMessage)this.messages.values()[i];
			result.addElement(msg);
		}
		return result;
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
	
	public boolean isDiscarded() {
		return discarded;
	}

	public void setDiscarded(boolean discarded) {
		this.discarded = discarded;
	}
	
	public boolean isWaitingForACK(){
		return this.waitingForAck;
	}

	public void setACKWasReceived() {
		this.waitingForAck = false;
	}
	
	public void setWaitForACK() {
		this.waitingForAck = true;
	}

	public void setNotWaitForACK() {
		this.waitingForAck = false;
	}
}
