package org.mesh4j.sync.message.channel.sms.core;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.core.repository.IEndpointFactory;

public class SmsEndpointFactory implements IEndpointFactory {

	public final static SmsEndpointFactory INSTANCE = new SmsEndpointFactory();
	
	public IEndpoint makeIEndpoint(String endpoint) {
		return new SmsEndpoint(endpoint);
	}

}
