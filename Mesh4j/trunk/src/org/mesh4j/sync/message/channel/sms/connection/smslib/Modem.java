package org.mesh4j.sync.message.channel.sms.connection.smslib;

public class Modem {

	// MODEL VARIABLE
	private String comPort;
	private int baudRate;
	private String manufacturer;
	private String model;
	private String serialNro;
	private String imsi;
	private int signalLevel;
	private int batteryLevel;

	// BUSINESS METHODS
	public Modem(String comPort, int baudRate, String manufacturer,
			String model, String serialNro, String imsi, int signalLevel,
			int batteryLevel) {
		this.comPort = comPort;
		this.baudRate = baudRate;
		this.manufacturer = manufacturer;
		this.model = model;
		this.serialNro = serialNro;
		this.imsi = imsi;
		this.signalLevel = signalLevel;
		this.batteryLevel = batteryLevel;
	}

	public String getComPort() {
		return comPort;
	}
	public int getBaudRate() {
		return baudRate;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public String getModel() {
		return model;
	}
	
	public String getSerialNro() {
		return serialNro;
	}

	public String getImsi() {
		return imsi;
	}

	public int getSignalLevel() {
		return signalLevel;
	}

	public int getBatteryLevel() {
		return batteryLevel;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.manufacturer);
		sb.append(" [");
		sb.append(this.model);
		sb.append("] [");
		sb.append(this.comPort);
		sb.append(" - ");
		sb.append(this.baudRate);
		sb.append("]");
		return sb.toString();
	}

}