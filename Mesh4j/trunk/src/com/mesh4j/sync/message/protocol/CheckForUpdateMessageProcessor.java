package com.mesh4j.sync.message.protocol;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.io.XMLWriter;

import com.mesh4j.sync.adapters.feed.Feed;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.atom.AtomSyndicationFormat;
import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullIdentityProvider;
import com.mesh4j.sync.validations.MeshException;

public class CheckForUpdateMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private MessageSyncProtocol syncProtocol;
	private IDataSetManager dataSetManager;	
	
	private OkLastVersionMessageProcessor okLastVersion;
	private LastVersionStatusMessageProcessor lastVersionStatus;
	
	// METHODS
	public CheckForUpdateMessageProcessor(MessageSyncProtocol syncProtocol,
			IDataSetManager dataSetManager,
			OkLastVersionMessageProcessor okLastVersion,
			LastVersionStatusMessageProcessor lastVersionStatus) {
		super();
		this.syncProtocol = syncProtocol;
		this.dataSetManager = dataSetManager;
		this.okLastVersion = okLastVersion;
		this.lastVersionStatus = lastVersionStatus;
	}
	
	@Override
	public String getMessageType() {
		return "1";
	}
	
	public String createMessage(String dataSetId, List<Item> items) {
		String header = this.syncProtocol.createMessageHeader(dataSetId, this.getMessageType());
		String data = this.encode(items);
		return header+data;
	}

	@Override
	public List<String> process(String message) {
		if(canProcess(message)){
			List<Item> items = this.getItems(message);
			String dataSetId = this.getDataSetId(message);
			
			ArrayList<String> response = new ArrayList<String>();
			List<Item> lastVersionItems = this.analyzeChanges(dataSetId, items);
			if(lastVersionItems.isEmpty()){
				String msgResponse = this.okLastVersion.createMessage(dataSetId);
				response.add(msgResponse);
			} else {
				String msgResponse = this.lastVersionStatus.createMessage(dataSetId, lastVersionItems);
				response.add(msgResponse);
			}
			return response;
		} else {
			return IMessageSyncProtocol.NO_RESPONSE;
		}
	}

	private boolean canProcess(String message) {
		String messageType = message.substring(2, 3);
		return this.getMessageType().equals(messageType);
	}

	private List<Item> analyzeChanges(String dataSetId, List<Item> items) {
		// TODO (JMT) MeshSMS: analyze changes
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);		
		return dataSet.getItems();
	}

	protected String getData(String message) {
		if(message.length() >= 8){
			return message.substring(8, message.length());
		} else {
			return null;
		}
	}
	
	protected String getDataSetId(String message) {
		return message.substring(3, 8);
	}
	
	protected List<Item> getItems(String message) {
		String data = this.getData(message);
		if(data == null || data.trim().length() == 0){
			return new ArrayList<Item>();
		} else {
			return this.decode(data);
		}
	}

	private String encode(List<Item> items) {
		try {
			StringWriter stringWriter = new StringWriter();
			XMLWriter writer = new XMLWriter(stringWriter);
			
			Feed feed = new Feed(items);
			FeedWriter feedWriter = new FeedWriter(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
			feedWriter.write(writer, feed);
			return stringWriter.toString();
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	private List<Item> decode(String data) {
		try {		
			StringReader stringReader = new StringReader(data);
			FeedReader reader = new FeedReader(AtomSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE);
			Feed feed = reader.read(stringReader);
			return feed.getItems();
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
}
