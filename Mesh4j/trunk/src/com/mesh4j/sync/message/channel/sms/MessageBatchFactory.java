package com.mesh4j.sync.message.channel.sms;


public class MessageBatchFactory {

	// CONSTANTS
	private int maxMessageLength = 140; // Default max value 140

	// BUSINESS METHODS
	public MessageBatchFactory() {
		super();
	}

	public MessageBatchFactory(int maxMesgLength) {
		super();
		this.maxMessageLength = maxMesgLength;
	}

	public SmsMessageBatch createMessageBatch(SmsEndpoint endopoint, String protocolHeader, String ackBatchId, String payload) {
		SmsMessageBatch newBatch = new SmsMessageBatch(endopoint);
		
		int payloadLength = payload.length();
		int expected = (payloadLength / maxMessageLength)
			+ (((payloadLength % maxMessageLength) == 0) ? 0 : 1);

		int i = 0;

		int msgSequence = 0;
		while (i < payloadLength) {
			String newMessagetext = payload.substring(i,
					((i + maxMessageLength) > payloadLength) ? payloadLength
							: (maxMessageLength + i));

			newMessagetext = MessageFormatter.createBatchMessage(protocolHeader, newBatch.getId(), ackBatchId, expected, msgSequence, newMessagetext);
			SmsMessage newMessage = new SmsMessage(newMessagetext);

			// add to collection
			newBatch.addMessage(msgSequence, newMessage);

			i = i + maxMessageLength;
			msgSequence++;
		}

		newBatch.setPayload(payload);
		newBatch.setExpectedMessageCount(newBatch.getMessagesCount());
		newBatch.setProtocolHeader(protocolHeader);
		return newBatch;
	}
}
