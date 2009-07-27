package org.mesh4j.sync.message.channel.sms.core;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsSender;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.validations.Guard;


public class SmsSender implements ISmsSender{

	// MODEL 
	private ISmsConnection smsConnection;
	private ISmsSenderRepository repository;
	
	// BUSINESS METHODS
	public SmsSender(ISmsConnection smsConnection, ISmsSenderRepository repository) {
		Guard.argumentNotNull(smsConnection, "smsConnection");
		this.smsConnection = smsConnection;
		this.repository = repository;
	}
	
	public SmsSender(ISmsConnection smsConnection) {
		this(smsConnection, null);
	}

	public void send(SmsMessageBatch batch) {
		Guard.argumentNotNull(batch, "batch");
		send(batch.getMessages(), batch.getEndpoint());
		this.repository.save(batch);
	}

	public void send(SmsMessageBatch batch, Vector<SmsMessage> smsMessages, SmsEndpoint endpoint) {
		Guard.argumentNotNull(smsMessages, "smsMessages");
		send(smsMessages, endpoint);
		this.repository.save(batch);
	}
	
	private void send(Vector<SmsMessage> smsMessages, SmsEndpoint endpoint) {
		for (SmsMessage smsMessage : smsMessages) {
			Guard.argumentNotNull(smsMessage, "smsMessage");
			Guard.argumentNotNullOrEmptyString(smsMessage.getText(), "smsMessage.text");
			Guard.argumentNotNull(endpoint, "endpoint");
		}
		
		Vector<String> msgTexts = new Vector<String>();
		for (SmsMessage smsMessage : smsMessages) {
			smsMessage.setLastModificationDate(new Date());
			msgTexts.addElement(smsMessage.getText());
//System.out.println("Sender: send message: " + smsMessage.getText());
		}
		this.smsConnection.send(msgTexts, endpoint);
	}
	
	public void receiveACK(String batchId) {
		this.repository.receiveACK(batchId);
	}

	public void purgeBatches(String sessionId, int sessionVersion) {
		this.repository.removeAll(sessionId, sessionVersion);
	}

	public SmsMessageBatch getBatch(String batchID) {
		return this.repository.get(batchID);
	}

	public Vector<SmsMessageBatch> getPendingACKOutcommingBatches() {
		return this.repository.getPendingACKOutcommingBatches();
	}
}
