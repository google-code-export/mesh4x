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

public class LastVersionStatusMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private MessageSyncProtocol syncProtocol;
	private IDataSetManager dataSetManager;
	
	// METHODS
	public LastVersionStatusMessageProcessor(MessageSyncProtocol syncProtocol,
			IDataSetManager dataSetManager) {
		super();
		this.syncProtocol = syncProtocol;
		this.dataSetManager = dataSetManager;
	}
	
	@Override
	public String getMessageType() {
		return "3";
	}

	@Override
	public List<String> process(String message) {
		if(canProcess(message)){
			List<Item> items = this.getItems(message);
			String dataSetId = this.getDataSetId(message);
	
			for (Item item : items) {
				this.merge(dataSetId, item);
			}
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}

	public String createMessage(String dataSetId, List<Item> items) {
		String header = this.syncProtocol.createMessageHeader(dataSetId, this.getMessageType());
		String data = this.encode(items);
		return header+data;
	}

	private void merge(String dataSetId, Item item) {
		
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
		dataSet.add(item);
		// TODO (JMT) MeshSMS: merge
//		Item originalItem = null;
//		Item incomingItem = null;
//		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
//		if (result.isMergeNone() && result.getProposed() != null
//				&& result.getProposed().hasSyncConflicts()) {
//			dataSet.notifyConflict(result.getProposed());
//		}
//		if (result.getOperation() == null || result.getOperation().isRemoved()) {
//			throw new UnsupportedOperationException();
//		} else if (result.getOperation().isAdded()) {
//			dataSet.add(result.getProposed());
//		} else if (result.getOperation().isUpdated() || result.getOperation().isConflict()) {
//			dataSet.update(result.getProposed());
//		}
	}	

	protected String getData(String message) {
		if(message.length() > 8){
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
		if(data == null){
			return new ArrayList<Item>();
		} else {
			return this.decode(data);
		}
	}
	
	private boolean canProcess(String message) {
		String messageType = message.substring(2, 3);
		return this.getMessageType().equals(messageType);
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
