package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.channel.sms.core.ISmsReceiverRepository;
import com.mesh4j.sync.message.channel.sms.core.ISmsSenderRepository;
import com.mesh4j.sync.message.channel.sms.core.SmsChannel;
import com.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import com.mesh4j.sync.message.channel.sms.core.SmsSender;
import com.mesh4j.sync.message.channel.sms.schedule.AskLossMessagesScheduleTask;
import com.mesh4j.sync.message.channel.sms.schedule.ResendBatchWithoutACKScheduleTask;
import com.mesh4j.sync.message.schedule.timer.TimerScheduler;
import com.mesh4j.sync.validations.Guard;

public class SmsChannelFactory {

	public static ISmsChannel createChannel(ISmsConnection smsConnection, int senderRetryTimeOut, int receiverRetryTimeOut){
		return createChannel(smsConnection, senderRetryTimeOut, receiverRetryTimeOut, null, null);
	}
	
	public static ISmsChannel createChannel(ISmsConnection smsConnection, int senderRetryTimeOut, int receiverRetryTimeOut, ISmsSenderRepository senderRepository, ISmsReceiverRepository receiverRepository){
		Guard.argumentNotNull(smsConnection, "smsConnection");
		
		SmsSender sender = new SmsSender(smsConnection, senderRepository);
		SmsReceiver receiver = new SmsReceiver(receiverRepository);
		smsConnection.registerSmsReceiver(receiver);
		
		SmsChannel channel = new SmsChannel(sender, receiver, smsConnection.getMessageEncoding(), smsConnection.getMaxMessageLenght());
		
		if(senderRetryTimeOut > 0){
			TimerScheduler.INSTANCE.schedule(new ResendBatchWithoutACKScheduleTask(channel, senderRetryTimeOut), senderRetryTimeOut);
		}
		
		if(receiverRetryTimeOut > 0){
			TimerScheduler.INSTANCE.schedule( new AskLossMessagesScheduleTask(channel, receiverRetryTimeOut), receiverRetryTimeOut);
		}
		return channel;
	}
}
