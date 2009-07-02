package org.mesh4j.sync.message.channel.sms.core;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageReceiver;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.ISmsRetiesNotification;
import org.mesh4j.sync.message.channel.sms.ISmsSender;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.StringTokenizer;


public class SmsChannel implements ISmsChannel, IMessageSyncAware {
	
	// MODEL VARIABLES
	private ISmsSender sender;
	private ISmsReceiver receiver;
	private MessageBatchFactory batchFactory;
	private IMessageEncoding messageEncoding;	
	private IMessageReceiver messageReceiver;	
	private ISmsRetiesNotification retriesNotification;
	
	// METHODs
	public SmsChannel(ISmsSender sender, ISmsReceiver receiver, IMessageEncoding messageEncoding, int maxMessageLenght, ISmsRetiesNotification retriesNotification) {

		Guard.argumentNotNull(sender, "sender");
		Guard.argumentNotNull(receiver, "receiver");
		Guard.argumentNotNull(messageEncoding, "messageEncoding");
		Guard.argumentNotNull(retriesNotification, "retriesNotification");
		
		this.messageEncoding = messageEncoding;
		this.batchFactory = new MessageBatchFactory(maxMessageLenght - MessageFormatter.getBatchHeaderLenght());
		this.retriesNotification = retriesNotification;
		this.sender = sender;
		this.receiver = receiver;
		receiver.setBatchReceiver(this);
	}


	public void receive(SmsMessageBatch batch){
		Guard.argumentNotNull(batch, "batch");
		
		IMessage message = createMessage(batch);
		if(this.isRetry(message)){
			this.resendSmsMessages(message);
		}else{
			this.messageReceiver.receiveMessage(message);
		}
	}
	

	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}


	public void send(IMessage message) {
		Guard.argumentNotNull(message, "message");
		
		SmsMessageBatch batch = createBatch(message);		
		this.send(batch);
	}

	public SmsMessageBatch createBatch(IMessage message) {
		String msg = MessageFormatter.createMessage(message.getMessageType(), message.getSessionVersion(), message.getData());

		String encodedData = this.messageEncoding.encode(msg);		
		String header = message.getProtocol();
		String ackBatchId = (message.getOrigin() == null || message.getOrigin().length() == 0) ? "00000" : message.getOrigin();
		SmsMessageBatch batch = this.batchFactory.createMessageBatch(message.getSessionId(), (SmsEndpoint)message.getEndpoint(), header, ackBatchId, encodedData);
		if(message.isAckRequired()){
			batch.setWaitForACK();
		} else {
			batch.setNotWaitForACK();
		}
		return batch;
	}
	
	private Message createMessage(SmsMessageBatch batch) {
		batch.reconstitutePayload();

		String encodedData = batch.getPayload();
		String decodedData = this.messageEncoding.decode(encodedData);
		
		Message message = new Message(
			batch.getProtocolHeader(),
			MessageFormatter.getMessageType(decodedData),
			batch.getSessionId(),
			MessageFormatter.getSessionVersion(decodedData),
			MessageFormatter.getData(decodedData),
			batch.getEndpoint()
		);
		message.setOrigin(batch.getId());
		return message;
	}


	public void receiveACK(String batchId) {
		if(batchId != null && batchId.length() != 0){
			this.sender.receiveACK(batchId);
		}
	}


	public void sendAskForRetry(SmsMessageBatch batch) {
		
		Guard.argumentNotNull(batch, "batch");
		
		StringBuffer sb = new StringBuffer();
		sb.append(batch.getId());
		sb.append("|");
		
		for (int i = 0; i < batch.getExpectedMessageCount(); i++) {
			SmsMessage msg = batch.getMessage(i);
			if(msg == null){
				sb.append(i);
				sb.append("|");
			}else{
				msg.setLastModificationDate(new Date());
			}
		}
		Message message = new Message("R", "R", batch.getSessionId(), 0, sb.toString(), batch.getEndpoint());
		message.setAckIsRequired(false);
		
		this.retriesNotification.notifySendAskForRetryIncompleteReceivedBatch(batch);
		this.send(message);
	}

	public boolean isRetry(IMessage message){
		return message != null  && "R".equals(message.getProtocol()) && "R".equals(message.getMessageType());
	}

	private void resendSmsMessages(IMessage message) {
		StringTokenizer st = new StringTokenizer(message.getData(), "|");
		String batchID = st.nextToken();
		
		SmsMessageBatch batch = this.sender.getBatch(batchID);
		this.retriesNotification.notifyReSendIncompleteReceivedBatch(batch);
		if(batch != null){
			Vector<SmsMessage> messagesToResend = new Vector<SmsMessage>();
			while(st.hasMoreTokens()){
				int seq = Integer.valueOf(st.nextToken());
				SmsMessage smsMessage = batch.getMessage(seq);
				messagesToResend.addElement(smsMessage);
			}
			this.sender.send(batch, messagesToResend, batch.getEndpoint());
		}
	}


	public void resend(SmsMessageBatch outcommingBatch) {
		this.retriesNotification.notifyReSendBatch(outcommingBatch);
		this.sender.send(outcommingBatch);
	}


	public void send(SmsMessageBatch batch) {
		this.sender.send(batch);		
	}
	
	
	// IMessageSyncAware METHODS
	public void beginSync(ISyncSession syncSession) {
		// nothing to do		
	}	

	public void endSync(ISyncSession syncSession, Vector<Item> conflicts) {
		this.sender.purgeBatches(syncSession.getSessionId(), syncSession.getVersion());
		this.receiver.purgeBatches(syncSession.getSessionId(), syncSession.getVersion());
	}

	public void beginSyncWithError(ISyncSession syncSession) {
		// nothing to do		
	}

	public void notifyInvalidMessageProtocol(IMessage message) {
		// nothing to do		
	}

	public void notifyInvalidProtocolMessageOrder(IMessage message) {
		// nothing to do		
	}

	public void notifyMessageProcessed(IMessage message, Vector<IMessage> response) {
		// nothing to do		
	}

	public void notifyProblemWithSessionCreation(IMessage message) {
		// nothing to do		
	}

	public void notifyCancelSync(ISyncSession syncSession) {
		// nothing to do		
	}

	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		// nothing to do		
	}


	public Vector<SmsMessageBatch> getIncompleteIncommingBatches() {
		return this.receiver.getIncompleteIncommingBatches();
	}

	public Vector<SmsMessageBatch> getPendingACKOutcommingBatches() {
		return this.sender.getPendingACKOutcommingBatches();
	}

	public void resendPendingACKOutcommingBatches(long min) {
		boolean ok = false;
		for (SmsMessageBatch batch : this.getPendingACKOutcommingBatches()) {
			if(min == 0 || batch.getDateTimeLastMessage().getTime() < min){
				ok = true;
				try{
					this.resend(batch);
				} catch (RuntimeException e) {
					this.retriesNotification.notifyReSendBatchError(batch, e.getMessage());
				}
			}
		}
		if(min == 0 && !ok){
			this.retriesNotification.notifyNoPendingACKOutcommingBatches();
		}
	}

	public void sendAskForRetryIncompleteIncommingBatches(long min) {
		boolean ok = false;
		for (SmsMessageBatch batch : this.getIncompleteIncommingBatches()) {
			if(min == 0 || batch.getDateTimeLastMessage().getTime() < min){
				ok = true;
				try{
					this.sendAskForRetry(batch);
				} catch (RuntimeException e) {
					this.retriesNotification.notifySendAskForRetryError(batch, e.getMessage());
				}
			} 
		}
		if(min == 0 && !ok){
			this.retriesNotification.notifyNoIncompleteIncommingBatches();
		}
	}
}
