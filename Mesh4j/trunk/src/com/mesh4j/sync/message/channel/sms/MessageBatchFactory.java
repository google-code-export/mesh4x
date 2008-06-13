package com.mesh4j.sync.message.channel.sms;

import org.apache.commons.lang.StringUtils;

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

	public SmsMessageBatch createMessageBatch(String payload) {
		SmsMessageBatch newBatch = new SmsMessageBatch();

		int payloadLength = payload.length();
		String expectedCount = getExpectedCountString(payloadLength);

		int i = 0;

		int msgSequence = 0;
		while (i < payloadLength) {
			String newMessagetext = payload.substring(i,
					((i + maxMessageLength) > payloadLength) ? payloadLength
							: (maxMessageLength + i));

			// append Id [5] & batch count [3] & position[3] //maybe only
			// some messages actually need 'count' - like only first?

			String numMessageString = getNumMessageString(msgSequence);

			newMessagetext = newBatch.getId() + expectedCount
					+ numMessageString + newMessagetext;
			SmsMessage newMessage = new SmsMessage(newMessagetext);

			// add to collection
			newBatch.addMessage(msgSequence, newMessage);

			i = i + maxMessageLength;
			msgSequence++;
		}

		newBatch.setPayload(payload);
		newBatch.setExpectedMessageCount(newBatch.getMessagesCount());
		return newBatch;
	}

	private String getNumMessageString(int numMessage) {
		// TODO (JMT) MeshSMS: improve encoding
		return StringUtils.leftPad(String.valueOf(numMessage), 3, '0');
	}

	private String getExpectedCountString(int payloadLength) {
		// TODO (JMT) MeshSMS: improve encoding
		int expected = (payloadLength / maxMessageLength)
				+ (((payloadLength % maxMessageLength) == 0) ? 0 : 1);

		return StringUtils.leftPad(String.valueOf(expected), 3, '0');
	}
}
