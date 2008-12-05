package org.mesh4j.phone.ui;

import java.util.Date;

import javax.swing.JTextArea;

import org.mesh4j.phone.ui.translator.PhoneUITranslator;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.connection.smslib.IProgressMonitor;
import org.mesh4j.sync.message.channel.sms.connection.smslib.Modem;
import org.smslib.helper.CommPortIdentifier;

public class ConsoleProgressMonitor implements IProgressMonitor, ISmsConnectionInboundOutboundNotification{

	// MODEL VARIABLES
	private JTextArea console;
	private boolean stop;
	
	// BUSINESS METHODS
	public ConsoleProgressMonitor(JTextArea console){
		super();
		this.console = console;
	}
	
	public void stopDiscovery(){
		this.stop = true;
	}
	
	public void startDiscovery(){
		this.stop = false;
	}
	
	public boolean isStopped(){
		return stop;
	}
	
	// IProgressMonitor methods
	
	@Override
	public void checkingModem(CommPortIdentifier port, int baudRateAvailable) {
		log("\t"+PhoneUITranslator.getMessageCheckingModem(port.getName(), baudRateAvailable));
	}

	@Override
	public void checkingPortInfo(CommPortIdentifier port, int baudRateAvailable) {
		log("\t"+PhoneUITranslator.getMessageCheckingPort(port.getName(), baudRateAvailable));
		
	}

	@Override
	public void notifyAvailableModem(CommPortIdentifier port, int baudRateAvailable, Modem modem) {
		logAppendEndLine(PhoneUITranslator.getMessageAvailableModem(modem));		
	}

	@Override
	public void notifyAvailablePortInfo(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(PhoneUITranslator.getLabelAvailable());		
	}

	@Override
	public void notifyNonAvailableModem(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(PhoneUITranslator.getLabelNoAvailable());
	}

	@Override
	public void notifyNonAvailablePortInfo(CommPortIdentifier port, int baudRateAvailable) {
		logAppendEndLine(PhoneUITranslator.getLabelNoAvailable());
	}

	// ISmsConnectionInboundOutboundNotification methods

	@Override
	public void notifyReceiveMessage(String endpointId, String message, Date date) {
		this.log("\t"+PhoneUITranslator.getMessageNotifyReceiveMessageError(endpointId, message));
	}

	@Override
	public void notifyReceiveMessageError(String endpointId, String message, Date date) {
		this.log("\t"+PhoneUITranslator.getMessageNotifyReceiveMessage(endpointId, message));		
	}

	@Override
	public void notifySendMessage(String endpointId, String message) {
		this.log("\t"+PhoneUITranslator.getMessageNotifySendMessage(endpointId, message));
	}

	@Override
	public void notifySendMessageError(String endpointId, String message) {
		this.log("\t"+PhoneUITranslator.getMessageNotifySendMessageError(endpointId, message));		
	}
	
	@Override
	public void notifyReceiveMessageWasNotProcessed(String endpoint, String message, Date date) {
		this.log("\t"+PhoneUITranslator.getMessageNotifyReceiveMessageWasNotProcessed(endpoint, message, date));
	}

	
	// local methods
	public void log(String text) {
		this.console.setText(text + "\n"+ this.console.getText());
	}
	
	public void logAppendEndLine(String text) {
		String line, allWithOutLine;
		
		String consoleText = this.console.getText();
		int lfIndex = consoleText.indexOf("\n");
		if(lfIndex == -1){
			line = consoleText;
			allWithOutLine = "";
		} else {
			line = consoleText.substring(0, lfIndex);
			allWithOutLine = consoleText.substring(lfIndex, consoleText.length());
		}
		
		this.console.setText(line+ text + allWithOutLine);
	}
	
}
