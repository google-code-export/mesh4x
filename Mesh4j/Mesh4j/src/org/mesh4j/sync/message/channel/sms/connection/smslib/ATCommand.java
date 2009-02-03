package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ATCommand implements IATCommand{

	public static final ATCommand INSTANCE = new ATCommand();

	@Override
	public String execute(InputStream serialPortInputStream,
			OutputStream serialPortOutputStream) throws IOException {
		
		int c = serialPortInputStream.read();
		while (c != -1) {
			c = serialPortInputStream.read();
		}
		
		serialPortOutputStream.write('A');
		serialPortOutputStream.write('T');
		serialPortOutputStream.write('\r');
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// nothing to do
		}
		
		String response = "";
		c = serialPortInputStream.read();
		while (c != -1) {
			response += (char) c;
			c = serialPortInputStream.read();
		}
		return response;
	}

}
