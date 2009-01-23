package org.mesh4j.sync.message.core;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.test.utils.TestHelper;


public class SyncSessionTests {

	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIDIsNull(){
		new SyncSession(null, 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIDIsEmpty(){
		new SyncSession("", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIsNull(){
		new SyncSession("a", 0, null, new SmsEndpoint("123"), true, true, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenEndpointIsNull(){
		new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), null, true, true, true);
	}
	
	@Test
	public void shouldBeginSyncInitializeInOutStatistics(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
		Assert.assertEquals(0, syncSession.getLastNumberInMessages());
		Assert.assertEquals(0, syncSession.getLastNumberOutMessages());
		
		syncSession.setLastNumberInMessages(5);
		syncSession.setLastNumberOutMessages(6);

		Assert.assertEquals(5, syncSession.getLastNumberInMessages());
		Assert.assertEquals(6, syncSession.getLastNumberOutMessages());
		
		syncSession.beginSync(true, true, true);
		
		Assert.assertEquals(0, syncSession.getLastNumberInMessages());
		Assert.assertEquals(0, syncSession.getLastNumberOutMessages());

		syncSession.setLastNumberInMessages(3);
		syncSession.setLastNumberOutMessages(2);

		Assert.assertEquals(3, syncSession.getLastNumberInMessages());
		Assert.assertEquals(2, syncSession.getLastNumberOutMessages());
		
		syncSession.beginSync(true, true, true, null, 1, "mysourceType");

		Assert.assertEquals(0, syncSession.getLastNumberInMessages());
		Assert.assertEquals(0, syncSession.getLastNumberOutMessages());

	}

	@Test
	public void shouldEndSyncSetInOutStatistics(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
		Assert.assertEquals(0, syncSession.getLastNumberInMessages());
		Assert.assertEquals(0, syncSession.getLastNumberOutMessages());
		
		syncSession.endSync(new Date(), 7, 8);
				
		Assert.assertEquals(7, syncSession.getLastNumberInMessages());
		Assert.assertEquals(8, syncSession.getLastNumberOutMessages());

	}
	
	@Test
	public void shouldBeginSyncInitializeStartAndEndDate(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
		
		Assert.assertNull(syncSession.getStartDate());
		Assert.assertNull(syncSession.getEndDate());
		
		syncSession.beginSync(true, true, true);
		
		Assert.assertNotNull(syncSession.getStartDate());
		Assert.assertNull(syncSession.getEndDate());
		
		Date startDate = TestHelper.makeDate(2008, 1, 1, 10, 20, 30, 0);
		syncSession.setStartDate(startDate);
		syncSession.setEndDate(new Date());
		
		syncSession.beginSync(true, true, true);

		Assert.assertNotNull(syncSession.getStartDate());
		Assert.assertFalse(startDate.equals(syncSession.getStartDate()));
		Assert.assertNull(syncSession.getEndDate());
	}

	@Test
	public void shouldEndSyncSetEndDate(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
		Assert.assertNull(syncSession.getEndDate());

		Date startDate = TestHelper.makeDate(2008, 1, 1, 10, 20, 30, 0);
		syncSession.setStartDate(startDate);

		Assert.assertNotNull(syncSession.getStartDate());
		Assert.assertNull(syncSession.getEndDate());
		
		syncSession.endSync(new Date(), 7, 8);
		
		Assert.assertNotNull(syncSession.getStartDate());
		Assert.assertEquals(startDate, syncSession.getStartDate());	
		Assert.assertNotNull(syncSession.getEndDate());
		Assert.assertTrue(syncSession.getStartDate().before(syncSession.getEndDate()));
	}
	
	@Test
	public void shouldCancelSyncSetEndDate(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true, true, true);
		Assert.assertNull(syncSession.getEndDate());

		Date startDate = TestHelper.makeDate(2008, 1, 1, 10, 20, 30, 0);
		syncSession.setStartDate(startDate);

		Assert.assertNotNull(syncSession.getStartDate());
		Assert.assertNull(syncSession.getEndDate());
		
		syncSession.cancelSync();
		
		Assert.assertNotNull(syncSession.getStartDate());
		Assert.assertEquals(startDate, syncSession.getStartDate());	
		Assert.assertNotNull(syncSession.getEndDate());
		Assert.assertTrue(syncSession.getStartDate().before(syncSession.getEndDate()));
	}
	
}
