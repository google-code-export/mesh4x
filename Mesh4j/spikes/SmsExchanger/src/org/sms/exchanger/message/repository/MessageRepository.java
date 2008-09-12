package org.sms.exchanger.message.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageRepository implements IMessageRepository {
	
	private final static Log LOGGER = LogFactory.getLog(MessageRepository.class);

	// MODEL VARIABLEs
	private String outboxDirectory;
	private String inboxDirectory;
	
	// BUSINESS METHODS
	public MessageRepository(String inboxDirectory, String outboxDirectory) {
		super();
		this.inboxDirectory = inboxDirectory;
		this.outboxDirectory = outboxDirectory;
	}
	
	@Override
	public void open() {
		File fileOutboxDir = new File(this.outboxDirectory);
		if(!fileOutboxDir.exists()){
			fileOutboxDir.mkdirs();
		}
		
		File fileInboxDir = new File(this.inboxDirectory);
		if(!fileInboxDir.exists()){
			fileInboxDir.mkdirs();
		}
	}
	
	@Override
	public List<Message> getOutcommingMessages() {
		return this.getMessages(this.outboxDirectory);
	}

	@Override
	public boolean addOutcommingMessage(Message message) {
		String fileName = getInboxMessageFileName(message);
		return addFile(fileName, message.getText());	
	}
	
	@Override
	public boolean deleteOutcommingMessage(Message message) {
		String fileName = this.getOutboxMessageFileName(message);
		return deleteFile(fileName);
		
	}
	
	@Override
	public List<Message> getIncommingMessages() {
		return this.getMessages(this.inboxDirectory);
	}
	
	@Override
	public boolean addIncommingMessage(Message message) {
		String fileName = getInboxMessageFileName(message);
		return addFile(fileName, message.getText());			
	}
	
	@Override
	public boolean deleteIncommingMessage(Message message) {
		String fileName = this.getInboxMessageFileName(message);
		return deleteFile(fileName);
	}

	@Override
	public void close() {
		// nothing to do
	}

	private List<Message> getMessages(String dir) {
		List<Message> result = new ArrayList<Message>();
		
		File fileDir = new File(dir);
		File[] files = fileDir.listFiles();
		for (File file : files) {
			if(file.isFile()){
				try{
					String[] fields = file.getName().split("_");
					String smsNumber = fields[0];
					String smsId = fields[1].substring(0, fields[1].length()-4);
					String smsText = readFile(file);
					
					Message message = new Message(smsId, smsNumber, smsText, new Date(file.lastModified()));
					result.add(message);
				} catch(Exception e){
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		Collections.sort(result, MessageComparator.INSTANCE);
		return result;
	}
	
	private boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
			return true;
		} else {
			return false;
		}
	}
	
	private boolean addFile(String fileName, String content) {
		try{
			
			FileWriter fw = new FileWriter(fileName);
			try{
				fw.write(content);
				fw.flush();
			} finally {
				fw.close();
			}
			return true;
		} catch(Exception e){
			LOGGER.error(e.getMessage(), e);
			return false;
		}
	}
	
	private String getInboxMessageFileName(Message message) {
		return this.inboxDirectory + message.getNumber() + "_" + message.getID() + ".txt";
	}

	private String getOutboxMessageFileName(Message message) {
		return this.outboxDirectory + message.getNumber() + "_" + message.getID() + ".txt";
	}
	
	private String readFile(File file) throws Exception{
		InputStream is = new FileInputStream(file);		
		StringBuffer result = new StringBuffer();
		Reader reader = new InputStreamReader(is);
		char[] cb = new char[2048];

		int amtRead = reader.read(cb);
		while (amtRead > 0) {
			result.append(cb, 0, amtRead);
			amtRead = reader.read(cb);
		}
		reader.close();
		return result.toString();
	}

}
