package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.Date;

import org.junit.Test;
import org.mesh4j.sync.message.channel.sms.ISmsBatchReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.MessageBatchFactory;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessage;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.core.MessageFormatter;
import org.mesh4j.sync.message.channel.sms.core.SmsReceiver;
import org.mesh4j.sync.message.encoding.CompressBase64MessageEncoding;
import org.mesh4j.sync.utils.IdGenerator;


public class SmsLibConnectionOnLineTests implements ISmsConnectionInboundOutboundNotification, ISmsBatchReceiver {

//	@Test
//	public void should() throws InterruptedException{
//		
//		Modem modem = ModemHelper.getModem("COM23", 115200);
//		
//		SmsLibConnectionOnlineClient client = new SmsLibConnectionOnlineClient("mesh4j.sync", modem.getComPort(), modem.getBaudRate(), modem.getManufacturer(), modem.getModel());
//		client.startService();
//		
//		//client.sendMessage("01136544867", "hola");
//		//client.sendMessage("01136540460", "hola", false);
//		client.send("0111555627633", "hola", false);
//		
//		int i = 0;
//		while(i <= 1){
//			i = i + 1;
//			Thread.sleep(60000);
//			client.send("0111555627633", "jmt" + i, false);
//		}
//		
//		client.stopService();
//	}
	
	@Test
	public void should() throws InterruptedException{
		
		SmsReceiver messageReceiver = new SmsReceiver();
		messageReceiver.setBatchReceiver(this);
		
		Modem modem = ModemHelper.getModem("COM23", 115200);
		
		SmsLibAsynchronousConnection client = new SmsLibAsynchronousConnection("mesh4j.sync", modem.getComPort(), modem.getBaudRate(), modem.getManufacturer(), modem.getModel());
		client.setMessageReceiver(messageReceiver);
		client.startService();
		
		//client.sendMessage("01136544867", "nokia 950");
		//client.sendMessage("01136540460", "nokia 750");
		//client.sendMessage("0111555627633", "motorola");
		String smsNumber = "01136544867";
		
		//String originalText = TestHelper.newText(300);
		String originalText = "<Placemark xmlns=\"http://earth.google.com/kml/2.2\"><name>abcnfdjewbqf dkwqnf dwkqfnqw fqwkf </name><visibility>0</visibility><LookAt><longitude>-95.26548319399998</longitude><latitude>38.95938957099998</latitude><altitude>0</altitude><range>6000264.254089176</range><tilt>0</tilt><heading>-9.382636310317375e-014</heading></LookAt><styleUrl>#msn_ylw-pushpin</styleUrl><Point><coordinates>-95.265483194,38.95938957099998,0</coordinates></Point></Placemark>";
		System.out.println("SEND MESSAGE: " + originalText);
		String encodedText = CompressBase64MessageEncoding.INSTANCE.encode(originalText);
		
		MessageBatchFactory factory = new MessageBatchFactory(120-MessageFormatter.getBatchHeaderLenght());
		SmsMessageBatch batch = factory.createMessageBatch(IdGenerator.newID(), new SmsEndpoint(smsNumber), "H", "00000", encodedText);
		
		for (SmsMessage message : batch.getMessages()) {
			client.send(smsNumber, message.getText(), false);	
		}
		
		client.readAll();
		client.stopService();
	}
	

	@Override
	public void notifyReceiveMessage(String endpointId, String message,
			Date date) {
		System.out.println("receive " + endpointId + " "+ message + " " + date);
		
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message,
			Date date) {
		System.out.println("receive error " + endpointId + " "+ message + " " + date);
		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		System.out.println("send " + endpointId + " "+ message);
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		System.out.println("send error " + endpointId + " "+ message);
	}


	@Override
	public void receive(SmsMessageBatch batch) {
		batch.reconstitutePayload();
		String decodedString = CompressBase64MessageEncoding.INSTANCE.decode(batch.getPayload());
		System.out.println("RECEIVE MESSAGE: " + decodedString);
	}


	@Override
	public void receiveACK(String batchId) {
		System.out.println("receiveACK " + batchId);		
	}
}
