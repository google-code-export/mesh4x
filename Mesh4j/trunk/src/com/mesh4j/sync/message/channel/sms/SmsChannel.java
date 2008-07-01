package com.mesh4j.sync.message.channel.sms;

import java.util.Date;
import java.util.StringTokenizer;

import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageReceiver;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.Guard;

public class SmsChannel implements IChannel, IBatchReceiver {
	
	// MODEL VARIABLES
	private SmsSender sender;
	private SmsReceiver receiver;

	private MessageBatchFactory batchFactory;
	private IMessageEncoding messageEncoding;
	
	private IMessageReceiver messageReceiver;

	// METHODs
	public SmsChannel(ISmsConnection smsConnection, int senderDelay, int receiverDelay) {
		Guard.argumentNotNull(smsConnection, "smsConnection");
		
		this.messageEncoding = smsConnection.getMessageEncoding();
		this.batchFactory = new MessageBatchFactory(smsConnection.getMaxMessageLenght());
		
		this.sender = new SmsSender(smsConnection);
		this.receiver = new SmsReceiver(this);
		smsConnection.registerSmsMessageReceiver(this.receiver);
		
		TimerScheduler.INSTANCE.schedule(new ResendBatchWithoutACKScheduleTask(this.sender, senderDelay), senderDelay);
		TimerScheduler.INSTANCE.schedule( new AskLossMessagesScheduleTask(this, this.receiver, receiverDelay), receiverDelay);
	}

	@Override
	public void receive(SmsMessageBatch batch){
		IMessage message = createMessage(batch);
		if(this.isRetry(message)){
			this.sendRetry(message);
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
		SmsMessageBatch batch = createBatch(message);		
		this.sender.send(batch, message.isAckRequired());
	}

	private SmsMessageBatch createBatch(IMessage message) {
		String msg = MessageFormatter.createMessage(message.getMessageType(), message.getSessionId(), message.getData());

		String encodedData = this.messageEncoding.encode(msg);		
		String header = message.getProtocol();
		String ackBatchId = (message.getOrigin() == null || message.getOrigin().length() == 0) ? "00000" : message.getOrigin();
		SmsMessageBatch batch = this.batchFactory.createMessageBatch((SmsEndpoint)message.getEndpoint(), header, ackBatchId, encodedData);
		return batch;
	}
	
	private Message createMessage(SmsMessageBatch batch) {
		batch.reconstitutePayload();

		String encodedData = batch.getPayload();
		String decodedData = this.messageEncoding.decode(encodedData);
		
		Message message = new Message(
			batch.getProtocolHeader(),
			MessageFormatter.getMessageType(decodedData),
			MessageFormatter.getSessionId(decodedData),
			MessageFormatter.getData(decodedData),
			batch.getEndpoint()
		);
		message.setOrigin(batch.getId());
		return message;
	}

	@Override
	public void receiveACK(String batchId) {
		this.sender.receiveACK(batchId);		
	}

	public boolean hasPendingMessages() {
		return this.sender.getOngoingBatchesCount() > 0 ||
			this.receiver.getOngoingBatchesCount() > 0;
	}

	protected void sendAskRetry(SmsMessageBatch batch) {
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
		Message message = new Message("R", "R", IdGenerator.newID(), sb.toString(), batch.getEndpoint());
		message.setAckIsRequired(false);
		
		this.send(message);
	}

	private boolean isRetry(IMessage message){
		return message != null  && "R".equals(message.getProtocol()) && "R".equals(message.getMessageType());
	}

	private void sendRetry(IMessage message) {
		StringTokenizer st = new StringTokenizer(message.getData(), "|");
		String batchID = st.nextToken();
		
		SmsMessageBatch batch = this.sender.getOngoingBatch(batchID);
		if(batch != null){
			while(st.hasMoreTokens()){
				int seq = Integer.valueOf(st.nextToken());
				SmsMessage smsMessage = batch.getMessage(seq);
				this.sender.send(smsMessage, batch.getEndpoint());
			}
		}
	}
}
