package org.sms.exchanger;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sms.exchanger.connection.IMessageNotification;
import org.sms.exchanger.connection.ISmsConnection;
import org.sms.exchanger.connection.factory.ISmsConnectionFactory;
import org.sms.exchanger.connection.factory.SmsConnectionFactory;
import org.sms.exchanger.message.repository.IMessageRepository;
import org.sms.exchanger.message.repository.Message;
import org.sms.exchanger.message.repository.factory.IMessageRepositoryFactory;
import org.sms.exchanger.message.repository.factory.MessageRepositoryFactory;
import org.sms.exchanger.properties.PropertiesProvider;

public class SmsExchanger implements IMessageNotification{

	// CONSTANTS
	private final static Log LOGGER = LogFactory.getLog(SmsExchanger.class);
	
	public final static String READ_MODE_ALL = "all";
	public final static String READ_MODE_UNREAD = "unread";
	public final static String READ_MODE_READ = "read";
	private final static Object SEMAPHORE = new Object();
	
	// MODEL VARIABLEs
	protected ISmsConnection connection;
	protected IMessageRepository messageRepository;
	protected boolean forceRead = false;
	protected String readMode = READ_MODE_UNREAD;
	private String port;
	private int msgSrcPort = -1;
	private int msgDstport = -1;
	
	// BUSINESS METHODS
	
	public SmsExchanger() {
		super();
	}

	public SmsExchanger(String port, int msgSrcPort, int msgDstPort) {
		super();
		this.port = port;
		this.msgSrcPort = msgSrcPort;
		this.msgDstport = msgDstPort;
	}

	
	public static void main(String[] args) {
		SmsExchanger sms = new SmsExchanger();
		sms.startUp(args);
	}

	public void startUp(String[] args) {
		try{
			PropertiesProvider prop = this.loadProperties(args);
			
			this.startUp(prop);
			this.scheduleWatcher(prop);
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
		}
	}

	private PropertiesProvider loadProperties(String[] args) throws IOException{
		PropertiesProvider prop = new PropertiesProvider();
		if(this.port != null){
			prop.setProperty(IProperties.SMS_PORT, this.port);
		}
		if(this.msgSrcPort != -1){
			prop.setProperty(IProperties.SMS_MESSAGE_SOURCE_PORT, String.valueOf(this.msgSrcPort));
		}
		if(this.msgDstport != -1){
			prop.setProperty(IProperties.SMS_MESSAGE_DESTINATION_PORT, String.valueOf(this.msgDstport));
		}
		return prop;
	}

	private void scheduleWatcher(PropertiesProvider prop) {
		int period = prop.getInt(IProperties.WATCHER_PERIOD, IProperties.WATCHER_PERIOD_DEFAULT_VALUE);
		
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				try{
					sendAndReceiveMessages();
				} catch (RuntimeException e){
					LOGGER.error(e.getMessage(), e);
				}
			}			
		};		
		Timer timer = new Timer();
		timer.schedule(task, 0, period);
	}
	
	private void startUp(PropertiesProvider prop) throws Exception{
		
		this.readMode = prop.getString(IProperties.READ_MODE, READ_MODE_UNREAD);
		
		IMessageRepositoryFactory messageManagerFactory = (IMessageRepositoryFactory)prop.getInstance(IProperties.REPOSITORY_FACTORY, new MessageRepositoryFactory());
		this.messageRepository = messageManagerFactory.createMessageManager(prop);
		this.messageRepository.open();
		
		ISmsConnectionFactory connFactory = (ISmsConnectionFactory)prop.getInstance(IProperties.CONNECTION_FACTORY, new SmsConnectionFactory());
		this.connection = connFactory.createConnection(prop, this);	
		this.connection.connect();
	}
	
	protected void sendAndReceiveMessages() {
		synchronized (SEMAPHORE) {
			this.sendMessages();
			if(this.forceRead){
				boolean ok = this.receiveMessages();
				if(ok){
					this.forceRead = false;				
				}
			}
		}
	}

	public void shutdown() throws Exception{
		this.messageRepository.close();
		this.connection.disconnect();
	}

	private boolean receiveMessages() {
		try{
			List<Message> messages;
			if(READ_MODE_READ.equals(this.readMode)){
				messages = this.connection.getReadMessages();
			}else if(READ_MODE_UNREAD.equals(this.readMode)){
				messages = this.connection.getUnreadMessages();
			}else{
				messages = this.connection.getAllMessages();
			}	
			
			for (Message message : messages) {
				try{
					this.messageRepository.addIncommingMessage(message);
				} catch(Exception e){
					LOGGER.error(e.getMessage(), e);
				}
			}
			return true;
		} catch(Exception e){
			LOGGER.error(e.getMessage(), e);
			return false;
		}
	}

	protected void sendMessages() {
		try{
			List<Message> messages = this.messageRepository.getOutcommingMessages();
			for (Message message : messages) {
				try {
					this.connection.sendMessage(message);
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		} catch(Exception e){
			LOGGER.error(e.getMessage(), e);
		}

	}

	@Override
	public boolean notifyReceiveMessage(Message message) {
		return this.messageRepository.addIncommingMessage(message);
	}

	@Override
	public boolean notifySentMessage(Message message) {
		return this.messageRepository.deleteOutcommingMessage(message);		
	}
	
	public void synchronousExecution(PropertiesProvider prop) {
		try{
			this.startUp(prop);
			this.sendMessages();
			this.receiveMessages();
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
		}finally{
			try{
				this.shutdown();
			} catch (Exception e){
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void notifyStartUpGateway() {
		this.forceRead = true;
	}

	public void send(List<String> messageTexts, String smsNumber) {
		synchronized (SEMAPHORE) {
			Message message;
			for (String messageText : messageTexts) {
				message = new Message(this.connection.newMessageID(), smsNumber, messageText, new Date());
				this.messageRepository.addOutcommingMessage(message);	
			}
		}
	}

	public List<Message> getIncommingMessages() {
		return this.messageRepository.getIncommingMessages();
	}
	
	public void deleteIncommingMessage(Message message) {
		this.messageRepository.deleteIncommingMessage(message);
	}
}
