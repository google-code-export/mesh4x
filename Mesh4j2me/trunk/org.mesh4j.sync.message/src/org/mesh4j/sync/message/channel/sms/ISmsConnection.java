package org.mesh4j.sync.message.channel.sms;

import java.util.Vector;

import org.mesh4j.sync.message.encoding.IMessageEncoding;

public interface ISmsConnection {

	public void setMessageReceiver(ISmsReceiver messageReceiver);

	public void send(Vector<String> messages, SmsEndpoint endpoint);

	public int getMaxMessageLenght();

	public IMessageEncoding getMessageEncoding();

}
