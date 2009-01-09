package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.IMessageReceiver;
import org.mesh4j.sync.message.channel.sms.ISmsChannel;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.core.Message;


public class SmsChannelWrapper implements ISmsChannel {

	// MODEL 
	private SmsChannel channel;
	private String lastSentBatchID;

	// BUSINESS METHODS
	public SmsChannelWrapper(SmsChannel channel){
		super();
		this.channel = channel;		
	}
	
	@Override
	public List<SmsMessageBatch> getIncommingBatches() {
		return this.channel.getIncommingBatches();
	}

	@Override
	public List<SmsMessageBatch> getOutcommingBatches() {
		return this.channel.getOutcommingBatches();
	}

	@Override
	public void receive(SmsMessageBatch batch) {
		this.channel.receive(batch);
	}

	@Override
	public void receiveACK(String batchId) {
		this.channel.receiveACK(batchId);
	}

	@Override
	public void resend(SmsMessageBatch outcommingBatch) {
		this.channel.resend(outcommingBatch);
	}

	@Override
	public void send(SmsMessageBatch batch, boolean ackIsRequired) {
		this.lastSentBatchID = batch.getId();
		this.channel.send(batch, ackIsRequired);
	}

	@Override
	public void sendAskForRetry(SmsMessageBatch incommingBatch) {
		this.channel.sendAskForRetry(incommingBatch);
	}

	@Override
	public void registerMessageReceiver(IMessageReceiver messageReceiver) {
		this.channel.registerMessageReceiver(messageReceiver);		
	}

	@Override
	public void send(IMessage message) {
		this.channel.send(message);
	}

	public SmsMessageBatch createBatch(Message message) {
		return this.channel.createBatch(message);
	}

	public String getLastSentBathID() {
		return lastSentBatchID;
	}
	
	@Override
	public void startUp() {
		this.channel.startUp();
	}
	
	@Override
	public void shutdown() {
		this.channel.shutdown();
	}

}
