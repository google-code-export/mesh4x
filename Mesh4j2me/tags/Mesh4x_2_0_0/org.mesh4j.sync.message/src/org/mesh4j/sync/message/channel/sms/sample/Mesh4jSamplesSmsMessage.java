package org.mesh4j.sync.message.channel.sms.sample;

import java.util.Date;
import java.util.Vector;

import javax.microedition.lcdui.StringItem;

import org.mesh4j.sync.message.channel.sms.ISmsBatchReceiver;
import org.mesh4j.sync.message.channel.sms.ISmsReceiver;
import org.mesh4j.sync.message.channel.sms.SmsEndpoint;
import org.mesh4j.sync.message.channel.sms.batch.DiscardedBatchRecord;
import org.mesh4j.sync.message.channel.sms.batch.SmsMessageBatch;
import org.mesh4j.sync.message.channel.sms.connection.SmsConnection;
import org.mesh4j.sync.message.channel.sms.connection.SmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.core.NonMessageEncoding;

public class Mesh4jSamplesSmsMessage implements ISmsReceiver{

	private String response = null;
	
	public static void sendSms(StringItem si) {
		si.setText("Start sms ");
		Mesh4jSamplesSmsMessage smsTest = new Mesh4jSamplesSmsMessage();
		
		smsTest.response = null;
		SmsConnection smsConnection = new SmsConnection("sms://:3333", 160, NonMessageEncoding.INSTANCE, SmsConnectionInboundOutboundNotification.INSTANCE);
		smsConnection.startConnection();
		try{
			smsConnection.setMessageReceiver(smsTest);
			si.setText("send: " + "sms://+5550000:5000 message: test send");
			smsConnection.send("sms://+5550000:5000", "test send", false);
			while(smsTest.response == null){
				sleep();
			}
			si.setText("receive: " + smsTest.response);
		}catch (Exception e) {
			si.setText("fail: " + e.getMessage());
			e.getMessage();
		}finally{
			smsConnection.closeConnection();	
		}
		
	}

	private static void sleep() {
		try{
			Thread.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveSms(SmsEndpoint endpoint, String message, Date date) {
		this.response = "Sms: "+endpoint.getEndpointId()+ " message: " + message+ " date: " + date.toString();
	}

	public Vector<SmsMessageBatch> getCompletedBatches() {
		return null;
	}

	public Vector<DiscardedBatchRecord> getDiscardedBatches() {
		return null;
	}

	public Vector<SmsMessageBatch> getOngoingBatches() {
		return null;
	}

	public int getOngoingBatchesCount() {
		return 0;
	}

	public void purgeBatches(String sessionId, int sessionVersion) {
	}

	public void setBatchReceiver(ISmsBatchReceiver smsBatchReceiver) {

	}

	public Vector<SmsMessageBatch> getIncompleteIncommingBatches() {
		return new Vector<SmsMessageBatch>();
	}


}
