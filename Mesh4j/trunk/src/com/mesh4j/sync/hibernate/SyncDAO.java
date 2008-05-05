package com.mesh4j.sync.hibernate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;

import com.mesh4j.sync.feed.FeedReader;
import com.mesh4j.sync.feed.FeedWriter;
import com.mesh4j.sync.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.utils.IdGenerator;


public class SyncDAO {

	// CONSTANTS
	private final static String SYNC_INFO = "SyncInfo";
	private final static String SYNC_INFO_ATTR_SYNC_ID = "sync_id";
	private final static String SYNC_INFO_ATTR_ENTITY_ID = "entity_id";
	private final static String SYNC_INFO_ATTR_ENTITY_NAME = "entity_name";
	private final static String SYNC_INFO_ATTR_ENTITY_VERSION = "entity_version";
	private final static String SYNC_INFO_ATTR_SYNC_DATA = "sync_data";
	
	private final static Log Logger = LogFactory.getLog(SyncDAO.class);
	
	// MODEL VARIABLES
	private SessionProvider sessionProvider;
	
	// BUSINESS METHODS
	public SyncDAO(SessionProvider sessionProvider) {
		super();
		this.sessionProvider = sessionProvider;
	}
	
	public SyncInfo get(String syncId) {
		Session session = getSession();
		Element syncInfoElement = (Element) session.get(SYNC_INFO, syncId);
		if(syncInfoElement == null){
			return null;
		}
		
		SyncInfo syncInfo = null;
		try {
			syncInfo = this.convertElement2SyncInfo(syncInfoElement);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
		}
		return syncInfo;
	}

	public void save(SyncInfo syncInfo) {
		Element syncInfoElement;
		try {
			syncInfoElement = this.convertSyncInfo2Element(syncInfo);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
			return;
		}
		
		Session session = getSession();		
		session.saveOrUpdate(SYNC_INFO, syncInfoElement);		
	}

	@SuppressWarnings("unchecked")
	public List<SyncInfo> getAll(String entityName) {
		String syncQuery ="FROM " + SYNC_INFO + " WHERE " + SYNC_INFO_ATTR_ENTITY_NAME + " = '" + entityName + "'";
		Session session = getSession();
		List<Element> syncElements = session.createQuery(syncQuery).list();
		
		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>(); 
		for (Element syncInfoElement : syncElements) {
			SyncInfo syncInfo;
			try {
				syncInfo = convertElement2SyncInfo(syncInfoElement);
				result.add(syncInfo);
			} catch (DocumentException e) {
				Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
			}
		}
		return result;

	}

	public String newSyncID() {
		return IdGenerator.newID();
	}
	
	protected SyncInfo convertElement2SyncInfo(Element syncInfoElement) throws DocumentException {
		Sync sync = this.convertElement2Sync(syncInfoElement);
		String entityName = syncInfoElement.element(SYNC_INFO_ATTR_ENTITY_NAME).getText();
		String entityId = syncInfoElement.element(SYNC_INFO_ATTR_ENTITY_ID).getText();
		int entityVersion = Integer.parseInt(syncInfoElement.element(SYNC_INFO_ATTR_ENTITY_VERSION).getText());
		SyncInfo syncInfo = new SyncInfo(sync, entityName, entityId, entityVersion);
		return syncInfo;
	}
	
	protected Sync convertElement2Sync(Element syncInfoElement) throws DocumentException {
		Element syncData = syncInfoElement.element(SYNC_INFO_ATTR_SYNC_DATA);
		Element syncElement = DocumentHelper.parseText(syncData.getText()).getRootElement();
		
		FeedReader reader = new FeedReader(RssSyndicationFormat.INSTANCE);
		Sync sync = reader.readSync(syncElement);
		return sync;
	}
	
	protected Element convertSyncInfo2Element(SyncInfo syncInfo) throws DocumentException {
		Element syncElementRoot = DocumentHelper.createElement(SYNC_INFO);
		syncElementRoot.addElement(SYNC_INFO_ATTR_SYNC_ID).addText(syncInfo.getSyncId());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_NAME).addText(syncInfo.getEntityName());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_ID).addText(syncInfo.getEntityId());
		syncElementRoot.addElement(SYNC_INFO_ATTR_ENTITY_VERSION).addText(String.valueOf(syncInfo.getEntityVersion()));
		
		String syncAsXML = convertSync2XML(syncInfo.getSync());
		syncElementRoot.addElement(SYNC_INFO_ATTR_SYNC_DATA).addText(syncAsXML);
		
		return syncElementRoot;
	}

	private String convertSync2XML(Sync sync) throws DocumentException {
		FeedWriter writer = new FeedWriter(RssSyndicationFormat.INSTANCE);
		Element syncData = DocumentHelper.createElement(RssSyndicationFormat.ATTRIBUTE_PAYLOAD);
		syncData.addNamespace(RssSyndicationFormat.SX_PREFIX, RssSyndicationFormat.NAMESPACE);
		writer.writeSync(syncData, sync);
		
		String syncAsXML = syncData.element(RssSyndicationFormat.SX_ELEMENT_SYNC).asXML();
		return syncAsXML;
	}

	public static File getMapping() {
		File syncMapping = new File(SyncDAO.class.getResource("SyncInfo.hbm.xml").getFile());   // TODO (JMT) inject sync info mapping name -> Spring?
		return syncMapping;
	}

	public String getEntityName() {
		return SYNC_INFO;
	}

	private Session getSession() {
		return this.sessionProvider.getCurrentSession().getSession(EntityMode.DOM4J);
	}
}
