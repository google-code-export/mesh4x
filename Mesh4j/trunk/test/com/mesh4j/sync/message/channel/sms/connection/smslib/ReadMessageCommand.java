package com.mesh4j.sync.message.channel.sms.connection.smslib;

import java.util.ArrayList;
import java.util.List;

import org.smslib.ICallNotification;
import org.smslib.IInboundMessageNotification;
import org.smslib.InboundMessage;
import org.smslib.Library;
import org.smslib.Service;
import org.smslib.InboundMessage.MessageClasses;
import org.smslib.Message.MessageTypes;
import org.smslib.modem.SerialModemGateway;

public class ReadMessageCommand {
	
	private Service srv;

	public void execute(SerialModemGateway gateway) throws Exception {
		
		List<InboundMessage> msgList;
		
		// Create the notification callback method for Inbound & Status Report messages.
		InboundNotification inboundNotification = new InboundNotification();
		
		// Create the notification callback method for inbound voice calls.
		CallNotification callNotification = new CallNotification();
		try {
			System.out.println("Example: Read messages from a serial gsm modem.");
			System.out.println(Library.getLibraryDescription());
			System.out.println("Version: " + Library.getLibraryVersion());
			
			// Create new Service object - the parent of all and the main interface to you.
			srv = new Service();
			
			// Do we want the Gateway to be used for Inbound messages? If not,
			// SMSLib will never read messages from this Gateway.
			gateway.setInbound(true);
			
			// Do we want the Gateway to be used for Outbound messages? If not,
			// SMSLib will never send messages from this Gateway.
			gateway.setOutbound(true);
			gateway.setSimPin("0000");
			
			// Set up the notification methods.
			gateway.setInboundNotification(inboundNotification);
			gateway.setCallNotification(callNotification);
			
			// Add the Gateway to the Service object.
			srv.addGateway(gateway);
			
			// Similarly, you may define as many Gateway objects, representing
			// various GSM modems, add them in the Service object and control
			// all of them.
			// Start! (i.e. connect to all defined Gateways)
			srv.startService();
			
			System.out.println();
			System.out.println("Modem Information:");
			System.out.println("  Manufacturer: " + gateway.getManufacturer());
			System.out.println("  Model: " + gateway.getModel());
			System.out.println("  Serial No: " + gateway.getSerialNo());
			System.out.println("  SIM IMSI: " + gateway.getImsi());
			System.out.println("  Signal Level: " + gateway.getSignalLevel() + "%");
			System.out.println("  Battery Level: " + gateway.getBatteryLevel() + "%");
			System.out.println();
			
			// Read Messages. The reading is done via the Service object and
			// affects all Gateway objects defined. This can also be more
			// directed to a specific
			// Gateway - look the JavaDocs for information on the Service method
			// calls.
			msgList = new ArrayList<InboundMessage>();
			srv.readMessages(msgList, MessageClasses.ALL);
			for (InboundMessage msg : msgList)
				System.out.println(msg);
			
			// Sleep now. Emulate real world situation and give a chance to the
			// notifications
			// methods to be called in the event of message or voice call
			// reception.
			System.out.println("end....");
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			srv.stopService();
			gateway.stopGateway();
		}
	}

	public class InboundNotification implements IInboundMessageNotification {
		public void process(String gatewayId, MessageTypes msgType, InboundMessage msg) {
			if (msgType == MessageTypes.INBOUND)
				System.out
						.println(">>> New Inbound message detected from Gateway: "
								+ gatewayId);
			else if (msgType == MessageTypes.STATUSREPORT)
				System.out
						.println(">>> New Inbound Status Report message detected from Gateway: "
								+ gatewayId);
			System.out.println(msg);
			try {
				// Uncomment following line if you wish to delete the message
				// upon arrival.
				// srv.deleteMessage(msg);
			} catch (Exception e) {
				System.out.println("Oops!!! Something gone bad...");
				e.printStackTrace();
			}
		}
	}

	public class CallNotification implements ICallNotification {
		public void process(String gatewayId, String callerId) {
			System.out.println(">>> New call detected from Gateway: "
					+ gatewayId + " : " + callerId);
		}
	}

}
