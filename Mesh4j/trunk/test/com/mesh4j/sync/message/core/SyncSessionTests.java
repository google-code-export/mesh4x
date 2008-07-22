package com.mesh4j.sync.message.core;

import org.junit.Test;

import com.mesh4j.sync.message.channel.sms.SmsEndpoint;

public class SyncSessionTests {

	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIDIsNull(){
		new SyncSession(null, 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIDIsEmpty(){
		new SyncSession("", 0, new InMemoryMessageSyncAdapter("123"), new SmsEndpoint("123"), true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIsNull(){
		new SyncSession("a", 0, null, new SmsEndpoint("123"), true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenEndpointIsNull(){
		new SyncSession("a", 0, new InMemoryMessageSyncAdapter("123"), null, true);
	}
}
