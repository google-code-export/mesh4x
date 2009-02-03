package org.mesh4j.sync.message.channel.sms.connection.smslib;

import org.smslib.IOutboundMessageNotification;
import org.smslib.Library;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

public class SendMessageCommand {

	private Service srv;
	
	public void execute(SerialModemGateway gateway, String smsNum, String smsText) throws Exception {
		try{
			OutboundNotification outboundNotification = new OutboundNotification();
			srv = new Service();
						
			System.out.println("Example: Send message from a serial gsm modem.");
			System.out.println(Library.getLibraryDescription());
			System.out.println("Version: " + Library.getLibraryVersion());
			
			gateway.setInbound(true);
			gateway.setOutbound(true);
			gateway.setSimPin("0000");
			gateway.setOutboundNotification(outboundNotification);
			srv.addGateway(gateway);
			srv.startService();
			
			System.out.println();
			System.out.println("Modem Information:");
			System.out.println("  Manufacturer: " + gateway.getManufacturer());
			System.out.println("  Model: " + gateway.getModel());
			System.out.println("  Serial No: " + gateway.getSerialNo());
			System.out.println("  SIM IMSI: " + gateway.getImsi());
			System.out.println("  Signal Level: " + gateway.getSignalLevel() + "%");
			System.out.println("  Battery Level: " + gateway.getBatteryLevel()+ "%");
			System.out.println();
			
			// Send a message synchronously.
			OutboundMessage msg = new OutboundMessage(smsNum, smsText);
			srv.sendMessage(msg);
			
			System.out.println(msg);
			// Or, send out a WAP SI message.
			// OutboundWapSIMessage wapMsg = new
			// OutboundWapSIMessage("+306948494037", new
			// URL("https://mail.google.com/"), "Visit GMail now!");
			// srv.sendMessage(wapMsg);
			// System.out.println(wapMsg);
			// You can also queue some asynchronous messages to see how the
			// callbacks
			// are called...
			// msg = new OutboundMessage("+309999999999", "Wrong number!");
			// msg.setPriority(OutboundMessage.Priorities.LOW);
			// srv.queueMessage(msg, gateway.getGatewayId());
			// msg = new OutboundMessage("+308888888888", "Wrong number!");
			// msg.setPriority(OutboundMessage.Priorities.HIGH);
			// srv.queueMessage(msg, gateway.getGatewayId());
			
			System.out.println("End.....");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			srv.stopService();
			gateway.stopGateway();
		}
	}

	public class OutboundNotification implements IOutboundMessageNotification {
		public void process(String gatewayId, OutboundMessage msg) {
			System.out.println("Outbound handler called from Gateway: "
					+ gatewayId);
			System.out.println(msg);
		}
	}

}
