package org.mesh4j.sync.adapters.hibernate;

import static org.mesh4j.sync.parsers.SyncInfoParser.SYNC_INFO_ATTR_ENTITY_NAME;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.validations.MeshException;


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
		Element syncInfoElement = (Element) session.get(getEntityName(), syncId);
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
		Element syncInfoElement = syncInfoParser.convertSyncInfo2Element(syncInfo);
		Session session = getSession();		
		session.saveOrUpdate(getEntityName(), syncInfoElement);		
	}

	@SuppressWarnings("unchecked")
	public List<SyncInfo> getAll(String entityName) {
		String syncQuery ="FROM " + getEntityName() + " WHERE " + SYNC_INFO_ATTR_ENTITY_NAME + " = '" + entityName + "'";
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
		return IdGenerator.INSTANCE.newID();
	}

	public String getEntityName() {
		return this.syncInfoParser.getEntityName();
	}

	private Session getSession() {
		return this.sessionProvider.getCurrentSession().getSession(EntityMode.DOM4J);
	}
}
