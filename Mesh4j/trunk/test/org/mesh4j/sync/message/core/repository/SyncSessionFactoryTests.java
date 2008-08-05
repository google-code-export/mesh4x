package org.mesh4j.sync.message.core.repository;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.message.ISyncSession;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.utils.IdGenerator;


public class SyncSessionFactoryTests {

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenTargetIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory();
		factory.createSession("1", 0, "123", null, true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory();
		factory.createSession(null, 0, "123", new SmsEndpoint("123"), true);		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIsEmpty(){
		SyncSessionFactory factory = new SyncSessionFactory();
		factory.createSession("", 0, "123", new SmsEndpoint("123"), true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIDIsNull(){
		SyncSessionFactory factory = new SyncSessionFactory();
		factory.createSession("1", 0, null, new SmsEndpoint("123"), true);
	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIDIsEmpty(){
		SyncSessionFactory factory = new SyncSessionFactory();
		factory.createSession("1", 0, "", new SmsEndpoint("123"), true);
	}

	@Test
	public void shouldCreateSessionReturnsNullWhenSourceIDDoesNotRegistered(){
		SyncSessionFactory factory = new SyncSessionFactory();
		Assert.assertNull(factory.createSession("1", 0, "123", new SmsEndpoint("123"), true));
	}
	
	@Test
	public void shouldBasicCreateSessionReturnsNullWhenSourceIDDoesNotRegistered(){
		SyncSessionFactory factory = new SyncSessionFactory();
		Assert.assertNull(factory.createSession("1", 0, "123", null, true, true, null, null, null, null,null));
	}


	@Test
	public void shouldCreateSession(){
		String sourceID = "123";
		String sessionID = IdGenerator.newID();
		SmsEndpoint endpoint = new SmsEndpoint("123");
		
		SyncSessionFactory factory = new SyncSessionFactory();
		factory.registerSource(new InMemoryMessageSyncAdapter(sourceID));
		
		ISyncSession syncSession = factory.createSession(sessionID, 0, sourceID, endpoint, true);
		Assert.assertNotNull(syncSession);
		Assert.assertNull(syncSession.getLastSyncDate());
		Assert.assertEquals(sessionID, syncSession.getSessionId());
		Assert.assertEquals(0, syncSession.getVersion());
		Assert.assertEquals(0, syncSession.getSnapshot().size());
		Assert.assertEquals(sourceID, syncSession.getSourceId());
		Assert.assertEquals(endpoint, syncSession.getTarget());
		Assert.assertTrue(syncSession.isCompleteSync());
		Assert.assertTrue(syncSession.isFullProtocol());
		Assert.assertFalse(syncSession.isOpen());
	}
}
