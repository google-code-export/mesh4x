package org.mesh4j.sync.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileMessageRepository {
	
	private final static Log LOGGER = LogFactory.getLog(FileMessageRepository.class);

	private final static Comparator<FileMessage> FILE_MESSAGE_COMPARATOR = new Comparator<FileMessage>(){
		@Override
		public int compare(FileMessage o1, FileMessage o2) {
			if(o1.getDate() == null && o2.getDate() == null){
				return 0;
			}
			
			if(o1.getDate() == null){
				return -1;
			}
			
			if(o2.getDate() == null){
				return 1;
			}
			return o1.getDate().compareTo(o2.getDate());
		}	
	};
	
	// MODEL VARIABLEs
	private String outboxDirectory;
	private String inboxDirectory;
	
	// BUSINESS METHODS
	public FileMessageRepository(String inboxDirectory, String outboxDirectory) {
		super();
		this.inboxDirectory = inboxDirectory;
		this.outboxDirectory = outboxDirectory;
	}
	
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
	
	public boolean addOutcommingMessage(String endpoint, FileMessage message) {
		String fileName = getOutboxMessageFileName(endpoint, message);
		return addFile(fileName, message.getText());	
	}
	
	public List<FileMessage> getIncommingMessages() {
		return this.getMessages(this.inboxDirectory);
	}
	
	public boolean addIncommingMessage(FileMessage message) {
		String fileName = getInboxMessageFileName(message);
		return addFile(fileName, message.getText());			
	}
	
	public boolean deleteIncommingMessage(FileMessage message) {
		String fileName = this.getInboxMessageFileName(message);
		return deleteFile(fileName);
	}

	public void close() {
		// nothing to do
	}

	private List<FileMessage> getMessages(String dir) {
		List<FileMessage> result = new ArrayList<FileMessage>();
		
		File fileDir = new File(dir);
		File[] files = fileDir.listFiles();
		for (File file : files) {
			if(file.isFile()){
				try{
					String[] fields = file.getName().split("_");
					String smsNumber = fields[0];
					String smsId = fields[1].substring(0, fields[1].length()-4);
					String smsText = readFile(file);
					
					FileMessage message = new FileMessage(smsId, smsNumber, smsText, new Date(file.lastModified()));
					result.add(message);
				} catch(Exception e){
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		Collections.sort(result, FILE_MESSAGE_COMPARATOR);
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
	
	private String getInboxMessageFileName(FileMessage message) {
		return this.inboxDirectory + message.getNumber() + "_" + message.getID() + ".txt";
	}

	private String getOutboxMessageFileName(String endpoint, FileMessage message) {
		return this.outboxDirectory + endpoint + "\\in\\" + message.getNumber() + "_" + message.getID() + ".txt";
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
