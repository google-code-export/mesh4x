package org.mesh4j.sync.message.channel.sms;

import org.mesh4j.sync.message.channel.sms.core.ISmsReceiverRepository;
import org.mesh4j.sync.message.channel.sms.core.ISmsSenderRepository;
import org.mesh4j.sync.message.channel.sms.core.SmsChannel;
import org.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import org.mesh4j.sync.message.channel.sms.core.SmsSender;
import org.mesh4j.sync.message.channel.sms.core.repository.file.FileSmsChannelRepository;
import org.mesh4j.sync.message.channel.sms.schedule.AskLossMessagesScheduleTask;
import org.mesh4j.sync.message.channel.sms.schedule.ResendBatchWithoutACKScheduleTask;
import org.mesh4j.sync.message.schedule.timer.TimerScheduler;
import org.mesh4j.sync.validations.Guard;

public class SmsChannelFactory {

	public static ISmsChannel createChannel(ISmsConnection smsConnection, int senderRetryTimeOut, int receiverRetryTimeOut){
		return createChannel(smsConnection, senderRetryTimeOut, receiverRetryTimeOut, null, null);
	}
	
	public static ISmsChannel createChannelWithFileRepository(ISmsConnection smsConnection, int senderRetryTimeOut, int receiverRetryTimeOut, String repositoryBaseDirectory){
		FileSmsChannelRepository channelRepo = new FileSmsChannelRepository(repositoryBaseDirectory);
		return createChannel(smsConnection, senderRetryTimeOut, receiverRetryTimeOut, channelRepo, channelRepo);
	}
	
	public static ISmsChannel createChannel(ISmsConnection smsConnection, int senderRetryTimeOut, int receiverRetryTimeOut, ISmsSenderRepository senderRepository, ISmsReceiverRepository receiverRepository){
		Guard.argumentNotNull(smsConnection, "smsConnection");
		
		SmsSender sender = new SmsSender(smsConnection, senderRepository);
		SmsReceiver receiver = new SmsReceiver(receiverRepository);
		smsConnection.setMessageReceiver(receiver);
		
		SmsChannel channel = new SmsChannel(smsConnection, sender, receiver, smsConnection.getMessageEncoding(), smsConnection.getMaxMessageLenght());
		
		if(senderRetryTimeOut > 0){
			TimerScheduler.INSTANCE.schedule(new ResendBatchWithoutACKScheduleTask(channel, senderRetryTimeOut), senderRetryTimeOut);
		}
		
		if(receiverRetryTimeOut > 0){
			TimerScheduler.INSTANCE.schedule( new AskLossMessagesScheduleTask(channel, receiverRetryTimeOut), receiverRetryTimeOut);
		}
		return channel;
	}
}
