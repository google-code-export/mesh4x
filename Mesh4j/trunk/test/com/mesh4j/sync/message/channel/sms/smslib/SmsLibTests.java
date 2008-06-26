package com.mesh4j.sync.message.channel.sms.smslib;

import org.junit.Test;
import org.smslib.modem.SerialModemGateway;

public class SmsLibTests {

	@Test	
	public void shouldReadMessages() throws Exception{
		SerialModemGateway gateway = new SerialModemGateway("modem.com1", "COM1", 57600, "Nokia", "6310i");
		
		ReadMessageCommand command = new ReadMessageCommand();
		command.execute(gateway);
	}
	
	@Test	
	public void shouldSendMessage() throws Exception{
		SerialModemGateway gateway = new SerialModemGateway("modem.com1", "COM1", 57600, "Nokia", "6310i");

		SendMessageCommand command = new SendMessageCommand();
		command.execute(gateway);
	}

	
}

