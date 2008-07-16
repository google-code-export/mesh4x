package com.mesh4j.sync.message.channel.sms.connection.smslib;

import org.junit.Test;

public class ModemHelperTests {

	@Test
	public void shouldDetectModem(){
		//System.out.println(ModemHelper.getModem("COM3", 19200));
		System.out.println(ModemHelper.getModem("COM18", 19200));
		
		//System.out.println(ModemHelper.getModem("COM3"));
		//System.out.println(ModemHelper.getModem("COM18"));
		
		//System.out.println(ModemHelper.getAvailableModems());
	}
}