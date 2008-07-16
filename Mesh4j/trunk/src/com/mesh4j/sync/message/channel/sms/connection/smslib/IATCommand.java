package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface IATCommand {

	String execute(InputStream serialPortInputStream, OutputStream serialPortOutputStream) throws IOException;
}
