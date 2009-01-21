package org.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mesh4j.sync.message.channel.sms.ISmsBatchReceiver;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public class MockSmsReceiver implements ISmsReceiver {

	private List<SmsMessageBatch> batches = new ArrayList<SmsMessageBatch>();
	private List<SmsMessage> messages = new ArrayList<SmsMessage>();
	
	@Override
	public void receiveSms(SmsEndpoint endpoint, String smsMessageText, Date date) {
		this.messages.add(new SmsMessage(smsMessageText, date));
	}

	public List<SmsMessage> getReceivedMessages() {
		return messages;
	}

	@Override
	public List<SmsMessageBatch> getOngoingBatches() {
		return batches;
	}

	@Override
	public int getOngoingBatchesCount() {
		return batches.size();
	}
	
	public void addBatch(SmsMessageBatch batch){
		batches.add(batch);
	}

	@Override
	public void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver) {
		
	}

	@Override
	public List<SmsMessageBatch> getCompletedBatches() {
		return null;
	}

	@Override
	public List<DiscardedBatchRecord> getDiscardedBatches() {
		return null;
	}

	@Override
	public void purgeBatches(String sessionId, int sessionVersion) {
		// nothing to do		
	}

	@Override
	public List<SmsMessageBatch> getCompletedBatches(String sessionId,
			int version) {
		return null;
	}

	@Override
	public List<SmsMessageBatch> getOngoingBatches(String sessionId, int version) {
		return null;
	}
}
