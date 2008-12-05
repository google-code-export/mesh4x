package org.mesh4j.sync.message.channel.sms.connection.smslib;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.validations.MeshException;
import org.smslib.Service;
import org.smslib.helper.CommPortIdentifier;
import org.smslib.helper.SerialPort;
import org.smslib.modem.SerialModemGateway;


public class ModemHelper {

	private final static Log LOGGER = LogFactory.getLog(ModemHelper.class);
	
	private final static int bauds[] = { 9600, 14400, 19200, 28800, 33600, 38400, 56000,
			57600, 115200, 230400, 460800 };

	public static List<Modem> getAvailableModems(IProgressMonitor progressMonitor) {
		ArrayList<Modem> result = new ArrayList<Modem>();
		Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
		
		boolean isStopped = progressMonitor == null ? false : progressMonitor.isStopped();
		
		while (portList.hasMoreElements() && !isStopped) {
			CommPortIdentifier port = portList.nextElement();
			Modem modem = getModem(progressMonitor, port);
			if(modem != null){
				result.add(modem);
			}
			isStopped = progressMonitor == null ? false : progressMonitor.isStopped();
		}
		return result;
	}

	
	public static String getPortInfo(String portName, int baudRate) {
		return getPortInfo(CommPortIdentifier.getPortIdentifier(portName), baudRate);
	}
	
	private static String getPortInfo(CommPortIdentifier port, int baudRate) {
		String result = null;
		SerialPort serialPort = null;
		try {
			serialPort = port.open("Mesh4jPortTester", 1971);
			serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
			serialPort.setSerialPortParams(baudRate,
					SerialPort.DATABITS_8, 
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			
			InputStream serialPortInputStream = serialPort.getInputStream();
			OutputStream serialPortOutputStream = serialPort.getOutputStream();
			serialPort.enableReceiveTimeout(1000);
			
			String response = ATCommand.INSTANCE.execute(serialPortInputStream, serialPortOutputStream);
			if (response.indexOf("OK") >= 0) {
				result = ATGMMCommand.INSTANCE.execute(serialPortInputStream, serialPortOutputStream);
				return result;
			} 
		} catch (Exception e) {
			LOGGER.info(e.getMessage(), e);
		} finally {
			if (serialPort != null) {
				serialPort.close();
			}
		}
		return result;
	}
	
	private static Modem getModem(CommPortIdentifier port, int baudRate) {
		try{
			SerialModemGateway gateway = new SerialModemGateway("modem.info", port.getName(), baudRate, "generic", "generic");
			Service srv = null;
			try {
				srv = new Service();
				
				gateway.setInbound(true);
				gateway.setOutbound(true);
				gateway.setSimPin("0000");
				srv.addGateway(gateway);
				
				srv.startService();
				
				Modem modem = new Modem(port.getName(), baudRate, gateway.getManufacturer(), gateway.getModel(), gateway.getSerialNo(), gateway.getImsi(), gateway.getSignalLevel(), gateway.getBatteryLevel());
				return modem;
			} finally {
				if(srv != null){
					srv.stopService();
				}
				gateway.stopGateway();
			}
		} catch(Exception e){
			throw new MeshException(e);
		}
	}

	public static Modem getModem(String portName, int baudRate) {
		try{
			CommPortIdentifier port = CommPortIdentifier.getPortIdentifier(portName);
			if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				String response = getPortInfo(port, baudRate);
				if(!hasError(response)){
					return getModem(port, baudRate);
				}
			}
			return null;
		} catch(Exception e){
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
	
	public static Modem getModem(String portName) {
		CommPortIdentifier port = CommPortIdentifier.getPortIdentifier(portName);
		return getModem(null, port);
	}
	
	private static Modem getModem(IProgressMonitor progressMonitor, CommPortIdentifier port) {
		if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
			int baudRateAvailable = 0;
			for (int i = 0; i < bauds.length; i++) {
				try{
					if (progressMonitor != null) {
						if(progressMonitor.isStopped()){
							return null;
						}
						progressMonitor.checkingPortInfo(port, bauds[i]);
					}
					
					String response = getPortInfo(port, bauds[i]);
					if(!hasError(response)){
						if (progressMonitor != null) {
							progressMonitor.notifyAvailablePortInfo(port, bauds[i]);
						}
						baudRateAvailable = bauds[i];
					} else {
						if (progressMonitor != null) {
							progressMonitor.notifyNonAvailablePortInfo(port, bauds[i]);
						}
					}
				} catch(MeshException e){
					LOGGER.info(e.getMessage(), e);
				}
			}
			
			if(baudRateAvailable > 0){
				if (progressMonitor != null) {
					progressMonitor.checkingModem(port, baudRateAvailable);
				}
				Modem modem = getModem(port, baudRateAvailable);
				if(modem != null){
					if (progressMonitor != null) {
						progressMonitor.notifyAvailableModem(port, baudRateAvailable, modem);
					}
					return modem;
				} else {
					if (progressMonitor != null) {
						progressMonitor.notifyNonAvailableModem(port, baudRateAvailable);
					}
				}
			}
		}
		return null;
	}
	

	private static boolean hasError(String response) {
		return response == null || response.indexOf("ERROR") >= 0;
	}
}
