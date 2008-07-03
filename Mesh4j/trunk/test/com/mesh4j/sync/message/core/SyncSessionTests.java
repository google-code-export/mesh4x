package com.mesh4j.sync.message.core;

import org.junit.Test;

import com.mesh4j.sync.message.channel.sms.SmsEndpoint;
import com.mesh4j.sync.message.protocol.MockMessageSyncAdapter;

public class SyncSessionTests {

	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIDIsNull(){
		new SyncSession(null, new MockMessageSyncAdapter("123"), new SmsEndpoint("123"), true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSessionIDIsEmpty(){
		new SyncSession("", new MockMessageSyncAdapter("123"), new SmsEndpoint("123"), true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenSourceIsNull(){
		new SyncSession("a", null, new SmsEndpoint("123"), true);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void shouldCreateSessionFailsWhenEndpointIsNull(){
		new SyncSession("a", new MockMessageSyncAdapter("123"), null, true);
	}
}
