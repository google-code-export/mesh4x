package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ATI3Command implements IATCommand {

	public static final ATI3Command INSTANCE = new ATI3Command();

	@Override
	public String execute(InputStream serialPortInputStream, OutputStream serialPortOutputStream) throws IOException {
		serialPortOutputStream.write('A');
		serialPortOutputStream.write('T');
		serialPortOutputStream.write('I');
		serialPortOutputStream.write('3');
		serialPortOutputStream.write('\r');
		
		String response = "";
		int c = serialPortInputStream.read();
		while (c != -1) {
			response += (char) c;
			c = serialPortInputStream.read();
		}
		return response;
	}

}
