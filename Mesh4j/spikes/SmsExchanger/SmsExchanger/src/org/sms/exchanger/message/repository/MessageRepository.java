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
	private String outboundDirectory;
	private String inboundDirectory;
	
	// BUSINESS METHODS
	public MessageRepository(String inboundDirectory, String outboundDirectory) {
		super();
		this.inboundDirectory = inboundDirectory;
		this.outboundDirectory = outboundDirectory;
	}

	@Override
	public boolean addMessage(Message message) {
		try{
			String fileName = getInboundMessageFileName(message);
			FileWriter fw = new FileWriter(fileName);
			try{
				fw.write(message.getText());
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

	private String getInboundMessageFileName(Message message) {
		return this.inboundDirectory + message.getNumber() + "_" + message.getID() + ".txt";
	}
	private String getOutboundMessageFileName(Message message) {
		return this.outboundDirectory + message.getNumber() + "_" + message.getID() + ".txt";
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

	@Override
	public List<Message> getAllMessagesToSend() {
		List<Message> result = new ArrayList<Message>();
		
		File fileDir = new File(this.outboundDirectory);
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


	@Override
	public boolean deleteMessage(Message message) {
		String fileName = this.getOutboundMessageFileName(message);
		File file = new File(fileName);
		if(file.exists()){
			file.delete();
			return true;
		} else {
			return false;
		}
		
	}

	@Override
	public void open() {
		File fileOutboundDir = new File(this.outboundDirectory);
		if(!fileOutboundDir.exists()){
			fileOutboundDir.mkdirs();
		}
		
		File fileInboundDir = new File(this.inboundDirectory);
		if(!fileInboundDir.exists()){
			fileInboundDir.mkdirs();
		}
	}
	
	@Override
	public void close() {
		// nothing to do
	}
}
