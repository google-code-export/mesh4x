package org.smscl;

import org.junit.Test;
import org.sms.exchanger.SmsExchanger;

public class TestSmsCommandLine {

	@Test
	public void shouldSendAndReceiveMessages(){
		SmsExchanger.main(new String[]{});
	}
	
//	@Test
//	public void shouldSendMessages(){
//		SmsCommandLine.main(new String[]{this.getClass().getResource("test1.properties").getFile()});
//	}
//	
//	@Test
//	public void shouldReceiveMessages(){
//		SmsCommandLine.main(new String[]{this.getClass().getResource("test2.properties").getFile()});
//	}
}
