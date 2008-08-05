package org.mesh4j.sync.parsers;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.FeedReader;
import org.mesh4j.sync.adapters.feed.FeedWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;


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
	private IIdentityProvider identityProvider;

	//BUSINESS METHODS 
	
	public SyncInfoParser(ISyndicationFormat format, IIdentityProvider identityProvider) {
		Guard.argumentNotNull(format, "format");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		this.format = format;
		this.identityProvider = identityProvider;
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
		
		FeedReader reader = new FeedReader(this.format, this.identityProvider);
		Sync sync = reader.readSync(syncElement);
		return sync;
	}
	
	public Sync convertSyncElement2Sync(Element syncElement){
		return convertSyncElement2Sync(syncElement, this.format, this.identityProvider);
	}
	
	public static Sync convertSyncElement2Sync(Element syncElement, ISyndicationFormat format, IIdentityProvider identityProvider){
		FeedReader reader = new FeedReader(format, identityProvider);
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
		return convertSync2Element(sync, this.format, this.identityProvider);
	}
	
	public static Element convertSync2Element(Sync sync, ISyndicationFormat format, IIdentityProvider identityProvider) {
		FeedWriter writer = new FeedWriter(format, identityProvider);
		Element syncData = DocumentHelper.createElement(ISyndicationFormat.ELEMENT_PAYLOAD);
		syncData.addNamespace(ISyndicationFormat.SX_PREFIX, ISyndicationFormat.NAMESPACE);
		writer.writeSync(syncData, sync);
		
		Element syncElement = syncData.element(ISyndicationFormat.SX_QNAME_SYNC);
		syncElement.detach();
		return syncElement;
	}
	
	public String convertSync2XML(Sync sync) {
		FeedWriter writer = new FeedWriter(this.format, this.identityProvider);
		Element syncData = DocumentHelper.createElement(RssSyndicationFormat.ELEMENT_PAYLOAD);
		syncData.addNamespace(RssSyndicationFormat.SX_PREFIX, RssSyndicationFormat.NAMESPACE);
		writer.writeSync(syncData, sync);
		
		String syncAsXML = syncData.element(ISyndicationFormat.SX_QNAME_SYNC).asXML();
		return syncAsXML;
	}
}
