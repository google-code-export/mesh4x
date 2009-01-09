package org.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageReceiver;
import org.mesh4j.sync.message.IMessageSyncAware;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.ISmsSender;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.core.Message;
import org.mesh4j.sync.message.encoding.IMessageEncoding;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.validations.Guard;


public class SmsChannel implements ISmsChannel, IMessageSyncAware {
	
	// MODEL VARIABLES
	private ISmsSender sender;
	private ISmsReceiver receiver;
	private MessageBatchFactory batchFactory;
	private IMessageEncoding messageEncoding;	
	private IMessageReceiver messageReceiver;
	private ISmsConnection smsConnection;

	// METHODs
	public SmsChannel(ISmsConnection smsConnection, ISmsSender sender, ISmsReceiver receiver, IMessageEncoding messageEncoding, int maxMessageLenght) {

		Guard.argumentNotNull(sender, "sender");
		Guard.argumentNotNull(receiver, "receiver");
		Guard.argumentNotNull(messageEncoding, "messageEncoding");
		Guard.argumentNotNull(smsConnection, "smsConnection");
		
		int max = maxMessageLenght - MessageFormatter.getBatchHeaderLenght();
		if(max < 0){
			Guard.throwsArgumentException("maxMessageLenght");	
		}
		
		this.smsConnection = smsConnection;
		this.messageEncoding = messageEncoding;
		this.batchFactory = new MessageBatchFactory(max);
		
		this.sender = sender;
		this.receiver = receiver;
		receiver.setBatchReceiver(this);
	}

	@Override
	public void receive(SmsMessageBatch batch){
		Guard.argumentNotNull(batch, "batch");
		
		IMessage message = createMessage(batch);
		if(this.isRetry(message)){
			this.resendSmsMessages(message);
		}else{
			this.messageReceiver.receiveMessage(message);
		}
	}
	
	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	@Override
	public void send(IMessage message) {
		Guard.argumentNotNull(message, "message");
		
		SmsMessageBatch batch = createBatch(message);		
		this.send(batch, message.isAckRequired());
	}

	public SmsMessageBatch createBatch(IMessage message) {
		String msg = MessageFormatter.createMessage(message.getMessageType(), message.getSessionVersion(), message.getData());

		String encodedData = this.messageEncoding.encode(msg);		
		String header = message.getProtocol();
		String ackBatchId = (message.getOrigin() == null || message.getOrigin().length() == 0) ? "00000" : message.getOrigin();
		SmsMessageBatch batch = this.batchFactory.createMessageBatch(message.getSessionId(), (SmsEndpoint)message.getEndpoint(), header, ackBatchId, encodedData);
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

	@Override
	public void receiveACK(String batchId) {
		if(batchId != null && batchId.length() != 0){
			this.sender.receiveACK(batchId);
		}
	}

	@Override
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
		
		this.send(message);
	}

	public boolean isRetry(IMessage message){
		return message != null  && "R".equals(message.getProtocol()) && "R".equals(message.getMessageType());
	}

	private void resendSmsMessages(IMessage message) {
		StringTokenizer st = new StringTokenizer(message.getData(), "|");
		String batchID = st.nextToken();
		
		SmsMessageBatch batch = this.sender.getOngoingBatch(batchID);
		if(batch != null){
			ArrayList<SmsMessage> messagesToResend = new ArrayList<SmsMessage>();
			while(st.hasMoreTokens()){
				int seq = Integer.valueOf(st.nextToken());
				SmsMessage smsMessage = batch.getMessage(seq);
				messagesToResend.add(smsMessage);
			}
			this.sender.send(messagesToResend, batch.getEndpoint());
		}
	}

	@Override
	public List<SmsMessageBatch> getIncommingBatches() {
		return this.receiver.getOngoingBatches();
	}

	@Override
	public List<SmsMessageBatch> getOutcommingBatches() {
		return this.sender.getOngoingBatches();
	}

	@Override
	public void resend(SmsMessageBatch outcommingBatch) {
		this.sender.send(outcommingBatch, false);
	}

	@Override
	public void send(SmsMessageBatch batch, boolean ackIsRequired) {
		this.sender.send(batch, ackIsRequired);		
	}
	
	public void send(SmsMessage message, SmsEndpoint endpoint){
		this.sender.send(message, endpoint);
	}
	
	public void send(List<SmsMessage> messages, SmsEndpoint endpoint){
		this.sender.send(messages, endpoint);
	}
	
	// IMEssageSyncAdapter protocol
	@Override
	public void beginSync(ISyncSession syncSession) {
		// nothing to do		
	}
	
	@Override
	public void endSync(ISyncSession syncSession, List<Item> conflicts) {
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

	public void notifyMessageProcessed(IMessage message, List<IMessage> response) {
		// nothing to do		
	}

	public void notifySessionCreationError(IMessage message, String sourceId) {
		// nothing to do		
	}

	public void notifyCancelSync(ISyncSession syncSession) {
		// nothing to do		
	}

	public void notifyCancelSyncErrorSyncSessionNotOpen(String sourceId, IEndpoint endpoint) {
		// nothing to do		
	}

	@Override
	public void startUp() {
		this.sender.startUp();
	}
	
	@Override
	public void shutdown() {
		this.sender.shutdown();
	}

	public ISmsConnection getSmsConnection() {
		return this.smsConnection;
	}
}
