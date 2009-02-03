package org.mesh4j.sync.message.channel.sms.core;

import java.util.List;

import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;


public interface ISmsSenderRepository {

	void writeOutcomming(List<SmsMessageBatch> batches);
	List<SmsMessageBatch> readOutcomming();	
	
	void writeOutcommingCompleted(List<SmsMessageBatch> batches);
	List<SmsMessageBatch> readOutcommingCompleted();	
}
