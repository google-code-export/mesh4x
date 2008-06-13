package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessageReceiver;
import com.mesh4j.sync.message.encoding.IMessageEncoding;

public class SmsChannel implements IChannel, IMessageReceiver {

	// MODEL VARIABLES
	private MessageBatchFactory batchFactory;
	private SmsReceiver receiver;
	private ISmsConnection smsConnection;
	private IMessageEncoding messageEncoding;
	private IMessageReceiver messageReceiver;
	
	// METHODs
	// TODO (JMT) MeshSMS: pipeline: msg-(endode)->msgEncoded-(batch)->msg1..msgn-(connection send sms)->ms1..msgn-(batch)->msgEncoded-(decode)->msg
	public SmsChannel(ISmsConnection smsConnection, IMessageEncoding messageEncoding) {
		super();
		this.smsConnection = smsConnection;
		
		this.messageEncoding = messageEncoding;
		
		this.receiver = new SmsReceiver(this);
		this.batchFactory = new MessageBatchFactory(this.smsConnection.getMaxMessageLenght());
		this.smsConnection.registerMessageReceiver(this.receiver);
	}

	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	public void receiveMessage(String message){
		String decodedMessage = this.messageEncoding.decode(message);
		this.messageReceiver.receiveMessage(decodedMessage);
	}
	
	@Override
	public void send(String message) {
		String encodedMessage = this.messageEncoding.encode(message);
		
		SmsMessageBatch batch = this.batchFactory.createMessageBatch(encodedMessage);
		for (SmsMessage msg : batch.getMessages()) {
			this.smsConnection.send(msg.getText());
		}
	}

}
