package org.mesh4j.sync.message.channel.sms.connection.smslib;

import org.smslib.helper.CommPortIdentifier;

public interface IProgressMonitor {

	void checkingPortInfo(CommPortIdentifier port, int i);

	void notifyAvailablePortInfo(CommPortIdentifier port, int i);

	void notifyNonAvailablePortInfo(CommPortIdentifier port, int i);

	void checkingModem(CommPortIdentifier port, int baudRateAvailable);

	void notifyAvailableModem(CommPortIdentifier port, int baudRateAvailable, Modem modem);

	void notifyNonAvailableModem(CommPortIdentifier port, int baudRateAvailable);
	
	boolean isStopped();

}
