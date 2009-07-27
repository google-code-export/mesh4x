package org.mesh4j.sync.message.channel.sms.core.rms.storage;

import java.util.Vector;

import org.mesh4j.sync.adapters.rms.storage.IRmsStorage;
import org.mesh4j.sync.adapters.rms.storage.RmsStorage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.core.ISmsReceiverRepository;
import org.mesh4j.sync.validations.Guard;

public class SmsReceiverRepository implements ISmsReceiverRepository {

	public final static String DEFAULT_STORAGE_NAME = "M4X_SMS_RECEIVER_REPOSITORY";
	
	// MODEL VARIABLES
	private IRmsStorage storage;
	private SmsMessageBatchParser parser;
	private SmsMessageBatchIncompleteRecordFilter incompleteFilter; 

	// BUSINESS METHODS
	public SmsReceiverRepository() {
		this(DEFAULT_STORAGE_NAME);
	}
	
	public SmsReceiverRepository(String storageName) {
		Guard.argumentNotNullOrEmptyString(storageName, "storageName");

		this.parser = new SmsMessageBatchParser();
		this.storage = new RmsStorage(parser, storageName);
		this.incompleteFilter = new SmsMessageBatchIncompleteRecordFilter(this.parser);

	}

	public void deleteRecordStorage() {
		this.storage.deleteRecordStorage();
	}

	public void deleteAll() {
		this.storage.deleteAll();
	}

	public void removeAll(String sessionId, int sessionVersion) {
		SmsMessageBatchSessionRecordFilter filter = new SmsMessageBatchSessionRecordFilter(
				parser, sessionId);
		this.storage.delete(filter);
	}


	public SmsMessageBatch get(String batchId) {
		SmsMessageBatchIdRecordFilter idFilter = new SmsMessageBatchIdRecordFilter(parser);
		idFilter.setId(batchId);
		return (SmsMessageBatch) this.storage.get(idFilter);
	}

	public void save(SmsMessageBatch batch) {
		SmsMessageBatchIdRecordFilter idFilter = new SmsMessageBatchIdRecordFilter(parser);
		idFilter.setId(batch.getId());
		
		this.storage.saveOrUpdate(batch, idFilter);
	}

	public Vector<SmsMessageBatch> getIncompleteIncommingBatches() {
		return this.storage.getAll(this.incompleteFilter, null);
	}
}
