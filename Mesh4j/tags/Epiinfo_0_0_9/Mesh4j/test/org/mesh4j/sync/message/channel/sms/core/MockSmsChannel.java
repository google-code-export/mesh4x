package org.mesh4j.sync.message.channel.sms.core;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageReceiver;
import org.mesh4j.sync.message.InOutStatistics;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.ISmsConnection;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public class MockSmsChannel implements ISmsChannel {

	private List<SmsMessageBatch> incomming = new ArrayList<SmsMessageBatch>();
	private List<SmsMessageBatch> outcomming = new ArrayList<SmsMessageBatch>();
	private List<SmsMessageBatch> retries = new ArrayList<SmsMessageBatch>();
	private List<SmsMessageBatch> resend = new ArrayList<SmsMessageBatch>();
	private List<String> acks = new ArrayList<String>();
	
	@Override
	public void send(SmsMessageBatch batch, boolean ackIsRequired) {
		if(ackIsRequired){
			this.outcomming.add(batch);
		}
	}
	
	@Override
	public void receive(SmsMessageBatch batch) {
		this.incomming.add(batch);
	}

	@Override
	public void receiveACK(String batchId) {
		this.acks.add(batchId);
	}

	public List<String> getBatchACKs() {
		return acks;
	}

	@Override
	public List<SmsMessageBatch> getIncommingBatches() {
		return incomming;
	}

	@Override
	public List<SmsMessageBatch> getOutcommingBatches() {
		return outcomming;
	}


	@Override
	public void resend(SmsMessageBatch outcommingBatch) {
		this.resend.add(outcommingBatch);		
	}

	@Override
	public void sendAskForRetry(SmsMessageBatch incommingBatch) {
		this.retries.add(incommingBatch);		
	}

	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		
	}

	@Override
	public void send(IMessage message) {
			
	}

	public List<SmsMessageBatch> getRetries() {
		return this.retries;
	}

	public List<SmsMessageBatch> getResend() {
		return this.resend;
	}

	@Override
	public void shutdown() {
		// nothing to do	
	}

	@Override
	public void startUp() {
		// nothing to do		
	}

	@Override
	public InOutStatistics getInOutStatistics(String sessionId, int version) {
		return new InOutStatistics(incomming.size(), 0, outcomming.size(), 0);
	}

	@Override
	public void purgeMessages(String sessionId, int sessionVersion) {
		// nothing to do		
	}

	@Override
	public ISmsConnection getSmsConnection() {
		return null;
	}

}
