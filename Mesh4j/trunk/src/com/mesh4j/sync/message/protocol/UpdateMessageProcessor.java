package com.mesh4j.sync.message.protocol;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.merge.MergeBehavior;
import com.mesh4j.sync.merge.MergeResult;
import com.mesh4j.sync.message.IDataSet;
import com.mesh4j.sync.message.IDataSetManager;
import com.mesh4j.sync.message.IMessageSyncProtocol;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.MeshException;

public class UpdateMessageProcessor implements IMessageProcessor {

	// MODEL VARIABLES
	private MessageSyncProtocol syncProtocol;
	private IDataSetManager dataSetManager;
	private FeedReader feedReader;
	private FeedWriter feedWriter;
	
	// METHODS	
	
	public UpdateMessageProcessor(MessageSyncProtocol syncProtocol, IDataSetManager dataSetManager
			, ISyndicationFormat syndicationFormat, IIdentityProvider identityProvider) {
		super();
		this.dataSetManager = dataSetManager;
		this.syncProtocol = syncProtocol;
		this.feedReader = new FeedReader(syndicationFormat, identityProvider);
		this.feedWriter = new FeedWriter(syndicationFormat, identityProvider);
	}

	@Override
	public String getMessageType() {
		return "5";
	}
	
	public String createMessage(String dataSetId, String syncId) {
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
		Item item = dataSet.get(syncId);
		return createMessage(dataSetId, item);
	}
	
	public String createMessage(String dataSetId, Item item) {
		String data = this.encode(item);
		String msg = MessageFormatter.createMessage(dataSetId, this.getMessageType(), data);
		return msg;
	}
	
	@Override
	public List<String> process(String message) {
		if(this.canProcess(message)){
			String dataSetId = MessageFormatter.getDataSetId(message);
			String data = MessageFormatter.getData(message);
			
			Item incomingItem = this.decode(data);
			this.merge(dataSetId, incomingItem);
		}
		return IMessageSyncProtocol.NO_RESPONSE;
	}
	
	private void merge(String dataSetId, Item incomingItem) {
		IDataSet dataSet = this.dataSetManager.getDataSet(dataSetId);
		Item originalItem = dataSet.get(incomingItem.getSyncId());
		
		MergeResult result = MergeBehavior.merge(originalItem, incomingItem);
		if (result.isMergeNone()) {
			Item conflicItem = this.importItem(result, dataSet);
			if(conflicItem != null){
				this.syncProtocol.notifyConflict(dataSetId, conflicItem);
			}
		}
	}
	
	private Item importItem(MergeResult result, IDataSet dataSet) {
		if (result.getOperation() == null
				|| result.getOperation().isRemoved()) {
			throw new UnsupportedOperationException();
		} else if (result.getOperation().isAdded()) {
			dataSet.add(result.getProposed());
		} else if (result.getOperation().isUpdated()
				|| result.getOperation().isConflict()) {
			dataSet.update(result.getProposed());
		}
		if (result.isMergeNone() && result.getProposed() != null
				&& result.getProposed().hasSyncConflicts()) {
			return result.getProposed();
		}
		return null;
	}

	private String encode(Item item) {
		Element root = DocumentHelper.createElement("payload");
		this.feedWriter.write(root, item);
		Element itemElement = (Element) root.elements().get(0);
		
		String xml = XMLHelper.canonicalizeXML(itemElement);
		return xml;
	}

	private Item decode(String data) {
		try {
			Document document = DocumentHelper.parseText(data);
			Element itemElement = document.getRootElement(); 
			
			Item item = this.feedReader.readItem(itemElement);
			return item;
		} catch (DocumentException e) {
			throw new MeshException(e);
		}
	}
	
	private boolean canProcess(String message) {
		String messageType = MessageFormatter.getMessageType(message);
		return this.getMessageType().equals(messageType);
	}

}
