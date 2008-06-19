package com.mesh4j.sync.message.channel.sms;

import com.mesh4j.sync.message.IEndpoint;

public class SmsEndpoint implements IEndpoint {
	
	// MODEL VARIABLES
	private String smsNumber;

	public SmsEndpoint(String smsNumber) {
		super();
		this.smsNumber = smsNumber;
	}
	
	// METHODS
	
	public String getEndpointId(){
		return smsNumber;
	}

}
