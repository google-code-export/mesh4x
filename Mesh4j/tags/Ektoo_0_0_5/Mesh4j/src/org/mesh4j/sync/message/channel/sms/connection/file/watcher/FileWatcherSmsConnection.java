package org.mesh4j.sync.message.channel.sms.connection.file.watcher;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.channel.sms.connection.ISmsConnectionInboundOutboundNotification;
import org.mesh4j.sync.message.channel.sms.core.AbstractSmsConnection;
import org.mesh4j.sync.message.encoding.IMessageEncoding;

public class FileWatcherSmsConnection extends AbstractSmsConnection {

	private final static Log LOGGER = LogFactory.getLog(FileWatcherSmsConnection.class);
	
	// MODEL VARIABLES
	private String endpointId;
	private FileMessageRepository messageRepository;
	private Timer timer;
	
	// BUSINESS METHODS
	
	public FileWatcherSmsConnection(String endpointId, String inDir, String outDir, int maxMessageLenght, IMessageEncoding encoding, ISmsConnectionInboundOutboundNotification[] smsAware) {
		super(maxMessageLenght, encoding, smsAware);
		this.endpointId = endpointId;
		this.messageRepository = new FileMessageRepository(inDir, outDir);
	}

	@Override
	public void shutdown() {
		this.messageRepository.close();
		this.timer.cancel();
		this.timer.purge();
		this.timer = null;
	}

	@Override
	public void startUp() {
		this.messageRepository.open();
		
		int period = 1000;		
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				try{
					receiveMessages();
				} catch (RuntimeException e){
					LOGGER.error(e.getMessage(), e);
				}
			}			
		};		
		this.timer = new Timer();
		timer.schedule(task, 0, period);
	}

	protected void receiveMessages() {
		List<FileMessage> inMessages = this.messageRepository.getIncommingMessages();
		for (FileMessage fileMessage : inMessages) {
			boolean ok = this.processReceivedMessage(fileMessage.getNumber(), fileMessage.getText(), fileMessage.getDate());
			if(ok){
				this.messageRepository.deleteIncommingMessage(fileMessage);
			}
		}		
	}

	@Override
	protected void send(String endpointId, String message, boolean bynary) {
		FileMessage msg = new FileMessage(IdGenerator.INSTANCE.newID(), this.endpointId, message, new Date());
		this.messageRepository.addOutcommingMessage(endpointId, msg);
		this.notifySendMessage(endpointId, message);
	}
}
