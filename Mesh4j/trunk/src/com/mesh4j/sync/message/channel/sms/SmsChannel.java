package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageReceiver;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;
import com.mesh4j.sync.validations.Guard;

public class SmsChannel implements IChannel, IBatchReceiver {
	
	// MODEL VARIABLES
	private SmsSender sender;
	private SmsReceiver receiver;

	private MessageBatchFactory batchFactory;
	private IMessageEncoding messageEncoding;
	
	private IMessageReceiver messageReceiver;
	private AskLossMessagesScheduleTask retryTask;

	// METHODs
	public SmsChannel(ISmsConnection smsConnection, int senderDelay, int receiverDelay) {
		Guard.argumentNotNull(smsConnection, "smsConnection");
		
		this.messageEncoding = smsConnection.getMessageEncoding();
		this.batchFactory = new MessageBatchFactory(smsConnection.getMaxMessageLenght());
		
		this.sender = new SmsSender(smsConnection);
		this.receiver = new SmsReceiver(this);
		smsConnection.registerSmsMessageReceiver(this.receiver);
		
		TimerScheduler.INSTANCE.schedule(new ResendBatchWithoutACKScheduleTask(this.sender, senderDelay), senderDelay);
		
		this.retryTask = new AskLossMessagesScheduleTask(this, this.receiver, this.sender, receiverDelay);
		TimerScheduler.INSTANCE.schedule(this.retryTask, receiverDelay);
	}

	@Override
	public void receive(SmsMessageBatch batch){
		IMessage message = createMessage(batch);
		if(this.retryTask.isRetry(message)){
			this.retryTask.sendRetry(message);
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
}
