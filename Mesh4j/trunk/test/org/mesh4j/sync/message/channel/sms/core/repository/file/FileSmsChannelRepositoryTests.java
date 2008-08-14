package org.mesh4j.sync.message.channel.sms.core.repository.file;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.test.utils.TestHelper;


public class FileSmsChannelRepositoryTests {

	@Test
	public void shouldCreateFile(){
		FileSmsChannelRepository repo = new FileSmsChannelRepository(TestHelper.baseDirectoryForTest());
		
		ArrayList<SmsMessageBatch> outcomming = createBatchList();
		ArrayList<SmsMessageBatch> incommingCompleted = createBatchList();
		ArrayList<SmsMessageBatch> incommingOngoing = createBatchList();
		ArrayList<DiscardedBatchRecord> incommingDiscarded = createDiscardedBatchList();
				
		repo.writeOutcomming(outcomming);
		repo.writeIncomming(incommingOngoing);
		repo.writeIncommingCompleted(incommingCompleted);
		repo.writeIncommingDiscarded(incommingDiscarded);
		
		Assert.assertTrue(repo.getOutcommingFile().exists());
		Assert.assertTrue(repo.getIncommingFile().exists());
	}
	
	@Test
	public void shouldReadFile(){
		FileSmsChannelRepository repo = new FileSmsChannelRepository(TestHelper.baseDirectoryForTest());
		
		Assert.assertTrue(repo.getOutcommingFile().exists());
		Assert.assertTrue(repo.getIncommingFile().exists());

		Assert.assertFalse(repo.readOutcomming().isEmpty());
		Assert.assertFalse(repo.readIncomming().isEmpty());
		Assert.assertFalse(repo.readIncommingCompleted().isEmpty());
		Assert.assertFalse(repo.readIncommingDicarded().isEmpty());
	}
	
	private ArrayList<SmsMessageBatch> createBatchList(){
		ArrayList<SmsMessageBatch> result = new ArrayList<SmsMessageBatch>();
		result.add(createBatch());
		return result;
	}
	
	private ArrayList<DiscardedBatchRecord> createDiscardedBatchList(){
		ArrayList<DiscardedBatchRecord> result = new ArrayList<DiscardedBatchRecord>();
		result.add(new DiscardedBatchRecord(createBatch(), null));
		return result;
	}
	
	private SmsMessageBatch createBatch(){
		SmsMessageBatch batch = new SmsMessageBatch(
			IdGenerator.INSTANCE.newID(),
			new SmsEndpoint(IdGenerator.INSTANCE.newID()),
			"H", 
			"00012", 
			3);
		batch.addMessage(0, new SmsMessage("message 0"));
		return batch;
	}
	
	// TODO (JMT) MeshSMS: Tests
}
