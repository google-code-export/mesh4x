package org.mesh4j.sync.message.channel.sms.core;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.channel.sms.ISmsBatchReceiver;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.validations.Guard;

public class SmsReceiver implements ISmsReceiver {

	private final static Object SEMAPHORE = new Object();

	// MODEL VARIABLES
	private ISmsBatchReceiver smsBatchReceiver;
	private ISmsReceiverRepository repository;

	// BUSINESS METHODS

	public SmsReceiver(ISmsReceiverRepository repository) {
		Guard.argumentNotNull(repository, "repository");
		this.repository = repository;
	}

	public SmsReceiver receive(String endpoint, SmsMessage message) {
		return receive(new SmsEndpoint(endpoint), message);
	}

	public SmsReceiver receive(SmsEndpoint endpoint, SmsMessage message) {
		SmsMessageBatch batch = null;
		boolean notifyACK = false;
		synchronized (SEMAPHORE) {

			// do we have the batch?
			String receivedMessageBatchId = MessageFormatter.getBatchId(message
					.getText());
			batch = this.repository.get(receivedMessageBatchId);

			if (batch == null) {
				String protocolHeader = MessageFormatter
						.getBatchProtocolHeader(message.getText());
				String sessionId = MessageFormatter.getBatchSessionId(message
						.getText());
				batch = new SmsMessageBatch(sessionId, endpoint,
						protocolHeader, receivedMessageBatchId,
						MessageFormatter.getBatchExpectedMessageCount(message
								.getText()));
				notifyACK = true;
//System.out.println("RECEIVER: create new batch");				
			} 
			
			if (batch.isComplete() || batch.isDiscarded()) {
//int sequence = MessageFormatter.getBatchMessageSequenceNumber(message.getText());
//System.out.println("RECEIVER: discard message because batch is completed or batch is discarded. message: " + sequence + " text: " + message.getText());
				return this;
			} else {
				int sequence = MessageFormatter
						.getBatchMessageSequenceNumber(message.getText());
				SmsMessage batchMsg = batch.getMessage(sequence);
				if (batchMsg == null) {
					batch.addMessage(sequence, message);
//System.out.println("RECEIVER: add new message: " + sequence + " text: " + message.getText());
				} else {
					// validate message
					if (!(message.getText().equals(batchMsg.getText()))) {
						batch.setDiscarded(true);
//System.out.println("RECEIVER: discarded batch because message text is not equals to old message: " + sequence + " text: " + message.getText());
					} else {
//System.out.println("RECEIVER: discarded message because message text is equals to old message: " + sequence + " text: " + message.getText());						
						return this;
					}
				}
			}

			if (batch.isComplete()) {
				batch.reconstitutePayload();
//System.out.println("RECEIVER: batch is completed reconstitutePayload: " + batch.getPayload());
			}
			this.repository.save(batch);
//System.out.println("RECEIVER: batch was saved");
			if (notifyACK) {
				this.notifyBatchACK(message);
			}

			if (batch != null && batch.isComplete()) {
				this.notifyBatchCompleted(batch);
			}
		}
		return this;
	}

	private void notifyBatchCompleted(SmsMessageBatch batch) {
		if (this.smsBatchReceiver != null) {
//System.out.println("RECEIVER: notify batch completed");
			this.smsBatchReceiver.receive(batch);
		}
	}

	private void notifyBatchACK(SmsMessage message) {
		if (this.smsBatchReceiver != null) {
			String ack = MessageFormatter.getBatchACK(message.getText());
//System.out.println("RECEIVER: notify batch ack");
			this.smsBatchReceiver.receiveACK(ack);
		}
	}

	public void receiveSms(SmsEndpoint endpoint, String message, Date date) {
		SmsMessage smsMessage = new SmsMessage(message, date);
		this.receive(endpoint, smsMessage);
	}

	public void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver) {
		this.smsBatchReceiver = smsBatchReceiver;
	}

	public void purgeBatches(String sessionId, int sessionVersion) {
		synchronized (SEMAPHORE) {
//System.out.println("RECEIVER: purgue batches");
			this.repository.removeAll(sessionId, sessionVersion);
		}
	}

	public Vector<SmsMessageBatch> getIncompleteIncommingBatches() {
		return this.repository.getIncompleteIncommingBatches();
	}
}
