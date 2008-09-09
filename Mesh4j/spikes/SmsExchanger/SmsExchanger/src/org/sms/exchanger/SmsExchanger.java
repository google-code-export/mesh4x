package org.sms.exchanger;

import java.io.IOException;
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

	private final static Log LOGGER = LogFactory.getLog(SmsExchanger.class);
	public final static String READ_MODE_ALL = "all";
	public final static String READ_MODE_UNREAD = "unread";
	public final static String READ_MODE_READ = "read";
	
	// MODEL VARIABLEs
	protected ISmsConnection connection;
	protected IMessageRepository messageRepository;
	protected boolean forceRead = false;
	protected String readMode = READ_MODE_UNREAD;
	
	// BUSINESS METHODS
	
	public SmsExchanger() {
		super();
	}

	public static void main(String[] args) {
		
		SmsExchanger sms = new SmsExchanger();
		
		try{
			PropertiesProvider prop = sms.loadProperties(args);
			
			sms.startUp(prop);
			sms.schedulerWatcher(prop);
		} catch (Exception e){
			LOGGER.error(e.getMessage(), e);
		}
	}

	private PropertiesProvider loadProperties(String[] args) throws IOException{
		PropertiesProvider prop = new PropertiesProvider();
		return prop;
	}

	private void schedulerWatcher(PropertiesProvider prop) {
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
		this.sendMessages();
		if(this.forceRead){
			boolean ok = this.receiveMessages();
			if(ok){
				this.forceRead = false;				
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
					this.messageRepository.addMessage(message);
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
			List<Message> messages = this.messageRepository.getAllMessagesToSend();
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
		return this.messageRepository.addMessage(message);
	}

	@Override
	public boolean notifySentMessage(Message message) {
		return this.messageRepository.deleteMessage(message);		
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
}
