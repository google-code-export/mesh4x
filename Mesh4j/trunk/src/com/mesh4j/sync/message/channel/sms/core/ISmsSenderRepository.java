package com.mesh4j.sync.message.channel.sms.core;

import java.util.List;

import com.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;

public interface ISmsSenderRepository {

	void writeOutcomming(List<SmsMessageBatch> batches);
	List<SmsMessageBatch> readOutcomming();	
}
