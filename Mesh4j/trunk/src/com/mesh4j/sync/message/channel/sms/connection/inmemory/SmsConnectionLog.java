package com.mesh4j.sync.message.channel.sms.connection.inmemory;

public class SmsConnectionLog implements ISmsConnectionLog{

	@Override
	public void log(String message) {
		System.out.println(message);		
	}

}
