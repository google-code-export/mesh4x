package com.mesh4j.sync.message.channel.sms;

import java.util.ArrayList;
import java.util.List;

import com.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public class MockSmsSender implements ISmsSender {

	private List<SmsMessageBatch> batches = new ArrayList<SmsMessageBatch>();
	private List<SmsMessage> messages = new ArrayList<SmsMessage>();
	private List<String> acks = new ArrayList<String>();
	
	@Override
	public SmsMessageBatch getOngoingBatch(String batchID) {
		for (SmsMessageBatch batch : batches) {
			if(batch.getId().equals(batchID)){
				return batch;
			}
		}
		return null;
	}

	@Override
	public List<SmsMessageBatch> getOngoingBatches() {
		return batches;
	}

	@Override
	public int getOngoingBatchesCount() {
		return batches.size();
	}

	@Override
	public void receiveACK(String batchId) {
		acks.add(batchId);
	}

	@Override
	public void send(SmsMessageBatch batch, boolean ackRequired) {
		this.batches.add(batch);
	}

	@Override
	public void send(SmsMessage smsMessage, SmsEndpoint endpoint) {
		this.messages.add(smsMessage);
	}

	public List<SmsMessage> getMessages() {
		return messages;
	}
	
	public List<String> getACKs(){
		return acks;
	}

}
