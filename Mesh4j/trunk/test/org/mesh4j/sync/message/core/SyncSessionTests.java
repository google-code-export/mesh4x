package org.mesh4j.sync.message.core;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;


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
	
	public void shouldBeginSyncInitializeInOutStatistics(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), null, true, true, true);
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
	
	public void shouldEndSyncSetInOutStatistics(){
		SyncSession syncSession = new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), null, true, true, true);
		Assert.assertEquals(0, syncSession.getLastNumberInMessages());
		Assert.assertEquals(0, syncSession.getLastNumberOutMessages());
		
		syncSession.endSync(new Date(), 7, 8);
				
		Assert.assertEquals(7, syncSession.getLastNumberInMessages());
		Assert.assertEquals(8, syncSession.getLastNumberOutMessages());

	}
	
}
