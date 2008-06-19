package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageReceiver;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.message.encoding.IMessageEncoding;

public class SmsChannel implements IChannel, ISmsMessageReceiver {

	// MODEL VARIABLES
	private MessageBatchFactory batchFactory;
	private SmsReceiver receiver;
	private ISmsConnection smsConnection;
	private IMessageEncoding messageEncoding;
	private IMessageReceiver messageReceiver;

	// METHODs
	// TODO (JMT) MeshSms: pipeline? msg-(endode)->msgEncoded-(batch)->msg1..msgn-(connection send sms)->ms1..msgn-(batch)->msgEncoded-(decode)->msg
	public SmsChannel(ISmsConnection smsConnection, IMessageEncoding messageEncoding) {
		super();
		this.smsConnection = smsConnection;
		
		this.messageEncoding = messageEncoding;
		
		this.receiver = new SmsReceiver(this);
		this.batchFactory = new MessageBatchFactory(this.smsConnection.getMaxMessageLenght());
		this.smsConnection.registerSmsMessageReceiver(this.receiver);
	}

	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}

	@Override
	public void receiveSms(SmsEndpoint endpoint, String messageText){
		String encodedData = MessageFormatter.getMessage(messageText);
		String decodedData = this.messageEncoding.decode(encodedData);
		
		Message message = new Message(
			MessageFormatter.getProtocol(messageText),
			MessageFormatter.getMessageType(decodedData),
			MessageFormatter.getSessionId(decodedData),
			MessageFormatter.getData(decodedData),
			endpoint
		);
		this.messageReceiver.receiveMessage(message);
	}
	
	@Override
	public void send(IMessage message) {
		String msg = MessageFormatter.createMessage(message.getMessageType(), message.getSessionId(), message.getData());

		String encodedData = this.messageEncoding.encode(msg);		
		String header = message.getProtocol();
		SmsMessageBatch batch = this.batchFactory.createMessageBatch(header, encodedData);
		for (SmsMessage smsMessage : batch.getMessages()) {
			this.smsConnection.send((SmsEndpoint)message.getEndpoint(), smsMessage.getText());
		}
	}
}
