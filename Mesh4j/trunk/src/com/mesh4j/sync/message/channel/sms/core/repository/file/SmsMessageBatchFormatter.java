package com.mesh4j.sync.message.channel.sms.core.repository.file;

import java.util.Date;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import com.mesh4j.sync.utils.DateHelper;
import com.mesh4j.sync.validations.Guard;

public class SmsMessageBatchFormatter {

	public final static String ELEMENT_ROOT = "SmsChannel";
	public final static String ELEMENT_OUTCOMMING = "Outcomming";
	public static final String ELEMENT_OUTCOMMING_COMPLETED = "OutcommingCompleted";
	public final static String ELEMENT_INCOMMING_COMPLETED = "IncommingCompleted";
	public final static String ELEMENT_INCOMMING_ONGOING = "IncommingOngoing";
	public final static String ELEMENT_INCOMMING_DISCARDED = "IncommingDiscarded";
	
	private final static String ELEMENT_BATCH = "Batch";
	private final static String ELEMENT_MESSAGE = "BatchMessage";
	private final static String ATTRIBUTE_ID = "batchId";
	private final static String ATTRIBUTE_SESSION_ID = "sessionId";
	private final static String ATTRIBUTE_PROTOCOL = "protocol";
	private final static String ATTRIBUTE_EXPECTED_MESSAGE_COUNT = "messages";
	private final static String ATTRIBUTE_ENDPOINT = "sms";
	private final static String ATTRIBUTE_SEQUENCE = "sequence";
	private final static String ATTRIBUTE_DATE = "lastModDate";
	
	public static Element createBatchElement(SmsMessageBatch batch){
		Guard.argumentNotNull(batch, "batch");
		
		Element batchElement = DocumentHelper.createElement(ELEMENT_BATCH);
		batchElement.addAttribute(ATTRIBUTE_ID, batch.getId());
		batchElement.addAttribute(ATTRIBUTE_PROTOCOL, batch.getProtocolHeader());
		batchElement.addAttribute(ATTRIBUTE_EXPECTED_MESSAGE_COUNT, String.valueOf(batch.getExpectedMessageCount()));
		batchElement.addAttribute(ATTRIBUTE_ENDPOINT, batch.getEndpoint().getEndpointId());
		batchElement.addAttribute(ATTRIBUTE_SESSION_ID, batch.getSessionId());
		
		for (int i = 0; i < batch.getExpectedMessageCount(); i++) {
			SmsMessage msg = batch.getMessage(i);
			if(msg != null){
				Element messageElement = createMessageElement(i, batch.getMessage(i));
				batchElement.add(messageElement);
			}
		}
		return batchElement;
	}

	private static Element createMessageElement(int sequence, SmsMessage message) {
		Element msgElement = DocumentHelper.createElement(ELEMENT_MESSAGE);
		msgElement.addAttribute(ATTRIBUTE_SEQUENCE, String.valueOf(sequence));
		msgElement.addAttribute(ATTRIBUTE_DATE, DateHelper.formatRFC822(message.getLastModificationDate()));
		msgElement.setText(message.getText());
		return msgElement;
	}
	
	@SuppressWarnings("unchecked")
	public static SmsMessageBatch createBatch(Element batchElement){
		Guard.argumentNotNull(batchElement, "elementBatch");
		
		String id = batchElement.attributeValue(ATTRIBUTE_ID);
		String protocol = batchElement.attributeValue(ATTRIBUTE_PROTOCOL);
		int expectedMsgCount = Integer.parseInt(batchElement.attributeValue(ATTRIBUTE_EXPECTED_MESSAGE_COUNT));
		String endpoint = batchElement.attributeValue(ATTRIBUTE_ENDPOINT);
		String sessionId = batchElement.attributeValue(ATTRIBUTE_SESSION_ID);
		
		SmsMessageBatch batch = new SmsMessageBatch(sessionId, new SmsEndpoint(endpoint), protocol, id, expectedMsgCount);
		
		List<Element> messageElements = batchElement.elements(ELEMENT_MESSAGE);
		for (Element msgElement : messageElements) {
			addBatchMessage(batch, msgElement);
		}
		return batch;
	}

	private static void addBatchMessage(SmsMessageBatch batch, Element msgElement) {
		int sequence = Integer.parseInt(msgElement.attributeValue(ATTRIBUTE_SEQUENCE));
		Date date = DateHelper.parseRFC822(msgElement.attributeValue(ATTRIBUTE_DATE));
		String msgText = msgElement.getText();
		batch.addMessage(sequence, new SmsMessage(msgText, date));
	}
}
