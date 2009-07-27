package org.mesh4j.sync.message.channel.sms.core.rms.storage;

import java.util.Vector;

import org.mesh4j.sync.adapters.rms.storage.IRmsStorage;
import org.mesh4j.sync.adapters.rms.storage.RmsStorage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.core.ISmsSenderRepository;
import org.mesh4j.sync.validations.Guard;

public class SmsSenderRepository implements ISmsSenderRepository{

	public final static String DEFAULT_STORAGE_NAME = "M4X_SMS_SENDER_REPOSITORY";
	
	// MODEL VARIABLES
	private IRmsStorage storage;
	private SmsMessageBatchParser parser;
	private SmsMessageBatchPendingACKRecordFilter pendingACKFilter;
	
	// BUSINESS METHODS
	public SmsSenderRepository() {
		this(DEFAULT_STORAGE_NAME);
	}
	
	public SmsSenderRepository(String storageName) {
		Guard.argumentNotNullOrEmptyString(storageName, "storageName");
		
		this.parser = new SmsMessageBatchParser();
		this.storage = new RmsStorage(parser, storageName);
		this.pendingACKFilter = new SmsMessageBatchPendingACKRecordFilter(this.parser);
	}

	public void deleteRecordStorage() {
		this.storage.deleteRecordStorage();		
	}

	public void deleteAll() {
		this.storage.deleteAll();		
	}

	public void receiveACK(String batchId) {
		SmsMessageBatchIdRecordFilter idFilter = new SmsMessageBatchIdRecordFilter(parser);
		idFilter.setId(batchId);
		
		SmsMessageBatch batch = (SmsMessageBatch)this.storage.get(idFilter);
		if(batch != null){
			batch.setACKWasReceived();
			this.storage.saveOrUpdate(batch, idFilter);
		}
	}

	public void removeAll(String sessionId, int sessionVersion) {
		SmsMessageBatchSessionRecordFilter filter = new SmsMessageBatchSessionRecordFilter(parser, sessionId);
		this.storage.delete(filter);		
	}

	public void save(SmsMessageBatch batch) {
		SmsMessageBatchIdRecordFilter idFilter = new SmsMessageBatchIdRecordFilter(parser);
		idFilter.setId(batch.getId());
		
		this.storage.saveOrUpdate(batch, idFilter);
	}

	public SmsMessageBatch get(String batchID) {
		SmsMessageBatchIdRecordFilter idFilter = new SmsMessageBatchIdRecordFilter(parser);
		idFilter.setId(batchID);
		return (SmsMessageBatch) this.storage.get(idFilter);
	}

	public Vector<SmsMessageBatch> getPendingACKOutcommingBatches() {
		return this.storage.getAll(this.pendingACKFilter, null);
	}
}
