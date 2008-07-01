package com.mesh4j.sync.message.channel.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mesh4j.sync.message.IChannel;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.IMessageReceiver;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.message.encoding.IMessageEncoding;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class SmsChannel implements IChannel, ISmsMessageReceiver {

	private final static Log LOGGER = LogFactory.getLog(SmsChannel.class);
	
	// MODEL VARIABLES
	private MessageBatchFactory batchFactory;
	private SmsReceiver receiver;
	private ISmsConnection smsConnection;
	private IMessageEncoding messageEncoding;
	private IMessageReceiver messageReceiver;

	// METHODs
	public SmsChannel(ISmsConnection smsConnection, IMessageEncoding messageEncoding) {
		Guard.argumentNotNull(smsConnection, "smsConnection");
		Guard.argumentNotNull(messageEncoding, "messageEncoding");
		
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
		try{
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
		} catch (MeshException e) {
			LOGGER.error(e.getMessage(), e);
		} catch(IllegalArgumentException iae){
			LOGGER.error(iae.getMessage(), iae);
		} catch(NullPointerException npe){
			LOGGER.error(npe.getMessage(), npe);
		}
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
