package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ATGMMCommand implements IATCommand {

	public static final ATGMMCommand INSTANCE = new ATGMMCommand();

	@Override
	public String execute(InputStream serialPortInputStream, OutputStream serialPortOutputStream) throws IOException {
		serialPortOutputStream.write('A');
		serialPortOutputStream.write('T');
		serialPortOutputStream.write('+');
		serialPortOutputStream.write('C');
		serialPortOutputStream.write('G');
		serialPortOutputStream.write('M');
		serialPortOutputStream.write('M');
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
