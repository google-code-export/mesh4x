package com.mesh4j.sync.parsers;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.feed.FeedReader;
import com.mesh4j.sync.adapters.feed.FeedWriter;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.validations.Guard;

public class SyncInfoParser {

	// CONSTANTS
	public final static String SYNC_INFO = "SyncInfo";
	private final static String SYNC_INFO_ATTR_SYNC_ID = "sync_id";
	private final static String SYNC_INFO_ATTR_ENTITY_ID = "entity_id";
	public final static String SYNC_INFO_ATTR_ENTITY_NAME = "entity_name";
	private final static String SYNC_INFO_ATTR_ENTITY_VERSION = "entity_version";
	private final static String SYNC_INFO_ATTR_SYNC_DATA = "sync_data";
	
	// MODEL VARIABLES
	private ISyndicationFormat format;
	private ISecurity security;

	//BUSINESS METHODS 
	
	public SyncInfoParser(ISyndicationFormat format, ISecurity security) {
		Guard.argumentNotNull(format, "format");
		Guard.argumentNotNull(security, "security");
		
		this.format = format;
		this.security = security;
	}

	public SyncInfo convertElement2SyncInfo(Element syncInfoElement) throws DocumentException {
		Sync sync = this.convertElement2Sync(syncInfoElement);
		String entityName = syncInfoElement.element(SYNC_INFO_ATTR_ENTITY_NAME).getText();
		String entityId = syncInfoElement.element(SYNC_INFO_ATTR_ENTITY_ID).getText();
		int entityVersion = Integer.parseInt(syncInfoElement.element(SYNC_INFO_ATTR_ENTITY_VERSION).getText());
		SyncInfo syncInfo = new SyncInfo(sync, entityName, entityId, entityVersion);
		return syncInfo;
	}
	
	public Sync convertElement2Sync(Element syncInfoElement) throws DocumentException {
		Element syncData = syncInfoElement.element(SYNC_INFO_ATTR_SYNC_DATA);
		Element syncElement = DocumentHelper.parseText(syncData.getText()).getRootElement();
		
		FeedReader reader = new FeedReader(this.format, this.security);
		Sync sync = reader.readSync(syncElement);
		return sync;
	}
	
	public Sync convertSyncElement2Sync(Element syncElement){
		FeedReader reader = new FeedReader(this.format, this.security);
		Sync sync = reader.readSync(syncElement);
		return sync;
	}
	
	public Element convertSyncInfo2Element(SyncInfo syncInfo) {
		Element syncElementRoot = DocumentHelper.createElement(SYNC_INFO);
		syncElementRoot.addElement(SYNC_INFO_ATTR_SYNC_ID).addText(syncInfo.getSyncId());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_NAME).addText(syncInfo.getType());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_ID).addText(syncInfo.getId());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_VERSION).addText(String.valueOf(syncInfo.getVersion()));
		
		String syncAsXML = convertSync2XML(syncInfo.getSync());
		syncElementRoot.addElement(SYNC_INFO_ATTR_SYNC_DATA).addText(syncAsXML);
		
		return syncElementRoot;
	}

	public Element convertSync2Element(Sync sync) {
		FeedWriter writer = new FeedWriter(this.format, this.security);
		Element syncData = DocumentHelper.createElement(ISyndicationFormat.ATTRIBUTE_PAYLOAD);
		syncData.addNamespace(ISyndicationFormat.SX_PREFIX, ISyndicationFormat.NAMESPACE);
		writer.writeSync(syncData, sync);
		
		Element syncElement = syncData.element(ISyndicationFormat.SX_ELEMENT_SYNC);
		syncElement.detach();
		return syncElement;
	}
	
	public String convertSync2XML(Sync sync) {
		FeedWriter writer = new FeedWriter(this.format, this.security);
		Element syncData = DocumentHelper.createElement(RssSyndicationFormat.ATTRIBUTE_PAYLOAD);
		syncData.addNamespace(RssSyndicationFormat.SX_PREFIX, RssSyndicationFormat.NAMESPACE);
		writer.writeSync(syncData, sync);
		
		String syncAsXML = syncData.element(RssSyndicationFormat.SX_ELEMENT_SYNC).asXML();
		return syncAsXML;
	}
}
