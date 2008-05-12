package com.mesh4j.sync.adapters.hibernate;

import static com.mesh4j.sync.parsers.SyncInfoParser.SYNC_INFO;
import static com.mesh4j.sync.parsers.SyncInfoParser.SYNC_INFO_ATTR_ENTITY_NAME;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;

import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.validations.MeshException;

/**
 * Use CompoundRepositoryAdapter with a SyncHibernateRepository 
 */
@Deprecated

public class SyncDAO {

	// CONSTANTS	
	private final static Log Logger = LogFactory.getLog(SyncDAO.class);
	
	// MODEL VARIABLES
	private ISessionProvider sessionProvider;
	private SyncInfoParser syncInfoParser;
	
	// BUSINESS METHODS
	public SyncDAO(ISessionProvider sessionProvider, SyncInfoParser syncInfoParser) {
		super();
		this.sessionProvider = sessionProvider;
		this.syncInfoParser = syncInfoParser;
	}
	
	public SyncInfo get(String syncId) {
		Session session = getSession();
		Element syncInfoElement = (Element) session.get(SYNC_INFO, syncId);
		if(syncInfoElement == null){
			return null;
		}
		
		SyncInfo syncInfo = null;
		try {
			syncInfo = syncInfoParser.convertElement2SyncInfo(syncInfoElement);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}
		return syncInfo;
	}

	public void save(SyncInfo syncInfo) {
		Element syncInfoElement;
		try {
			syncInfoElement = syncInfoParser.convertSyncInfo2Element(syncInfo);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
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
				syncInfo = syncInfoParser.convertElement2SyncInfo(syncInfoElement);
				result.add(syncInfo);
			} catch (DocumentException e) {
				Logger.error(e.getMessage(), e);
				throw new MeshException(e);
			}
		}
		return result;

	}

	public String newSyncID() {
		return IdGenerator.newID();
	}

	public static File getMapping() {
		File syncMapping = new File(SyncDAO.class.getResource("SyncInfo.hbm.xml").getFile());   // TODO (JMT) REFACTORING: Spring, inject sync info mapping name
		return syncMapping;
	}

	public String getEntityName() {
		return SYNC_INFO;
	}

	private Session getSession() {
		return this.sessionProvider.getCurrentSession().getSession(EntityMode.DOM4J);
	}
}
