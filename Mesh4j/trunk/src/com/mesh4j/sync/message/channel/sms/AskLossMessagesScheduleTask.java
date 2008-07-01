package com.mesh4j.sync.message.channel.sms;

import java.util.Date;
import java.util.StringTokenizer;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.core.Message;
import com.mesh4j.sync.message.schedule.timer.ScheduleTimerTask;
import com.mesh4j.sync.utils.IdGenerator;
// TODO (JMT) MeshSMS: refactoring soon
public class AskLossMessagesScheduleTask extends ScheduleTimerTask {

	// MODEL
	private SmsReceiver receiver;
	private SmsSender sender;
	private SmsChannel channel;
	private int delay;
	
	// BUSINESS METHODS
	public AskLossMessagesScheduleTask(SmsChannel channel, SmsReceiver receiver, SmsSender sender, int receiverDelay) {
		super("AskLossMessagesScheduleTask"+IdGenerator.newID());
		this.receiver = receiver;
		this.delay = receiverDelay;
		this.channel = channel;
		this.sender = sender;
	}

	@Override
	public void run() {
		long nowTime = System.currentTimeMillis();
		long min = nowTime - delay;
		
		for (SmsMessageBatch ongoingBatch : this.receiver.getOngoingBatches()) {
			if(ongoingBatch.getDateTimeLastMessage().getTime() < min){
				
				StringBuffer sb = new StringBuffer();
				sb.append(ongoingBatch.getId());
				sb.append("|");
				
				for (int i = 0; i < ongoingBatch.getExpectedMessageCount(); i++) {
					SmsMessage msg = ongoingBatch.getMessage(i);
					msg.setLastModificationDate(new Date());
					if(msg == null){
						sb.append(i);
						sb.append("|");
					}
				}
				Message message = new Message("R", "R", "", sb.toString(), ongoingBatch.getEndpoint());
				message.setAckIsRequired(false);
				
				this.channel.send(message);
			}
		}
	}

	public boolean isRetry(IMessage message){
		return message != null  && "R".equals(message.getProtocol()) && "R".equals(message.getMessageType());
	}

	public void sendRetry(IMessage message) {
		StringTokenizer st = new StringTokenizer(message.getData(), "|");
		String batchID = st.nextToken();
		
		SmsMessageBatch batch = this.sender.getOngoingBatch(batchID);
		if(batch != null){
			while(st.hasMoreTokens()){
				int seq = Integer.valueOf(st.nextToken());
				SmsMessage smsMessage = batch.getMessage(seq);
				this.sender.send(smsMessage, batch.getEndpoint());
			}
		}
		
	}
}
