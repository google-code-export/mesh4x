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
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.compound.SyncRepository;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.utils.IdGenerator;

// TODO (JMT) tests
public class HibernateSyncRepository implements SyncRepository{

	// CONSTANTS	
	private final static Log Logger = LogFactory.getLog(HibernateSyncRepository.class);
	
	// MODEL VARIABLES
	private SyncInfoParser syncInfoParser;
	private SessionFactory sessionFactory;
	
	// BUSINESS METHODS
	public HibernateSyncRepository(SyncInfoParser syncInfoParser) {
		super();
		this.syncInfoParser = syncInfoParser;
		initializeHibernate();
	}
	
	private void initializeHibernate() {
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.addFile(getMapping());		
		this.sessionFactory = hibernateConfiguration.buildSessionFactory();
	}
	
	public SyncInfo get(String syncId) {
		Element syncInfoElement = getSyncInfo(syncId);
		if(syncInfoElement == null){
			return null;
		}
		SyncInfo syncInfo = convertElement2SyncInfo(syncInfoElement);
		return syncInfo;
	}

	private SyncInfo convertElement2SyncInfo(Element syncInfoElement) {
		SyncInfo syncInfo = null;
		try {
			syncInfo = syncInfoParser.convertElement2SyncInfo(syncInfoElement);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
		}
		return syncInfo;
	}

	private Element getSyncInfo(String syncId) {
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element syncInfoElement = (Element) dom4jSession.get(SYNC_INFO, syncId);
		session.close();
		return syncInfoElement;
	}

	public void save(SyncInfo syncInfo) {
		Element syncInfoElement = convertSyncInfo2Element(syncInfo);
		if(syncInfoElement != null){
			saveSyncInfo(syncInfoElement);
		}
	}

	private void saveSyncInfo(Element syncInfoElement) {
		Session session =  this.sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Session dom4jSession = session.getSession(EntityMode.DOM4J);
			dom4jSession.saveOrUpdate(SYNC_INFO, syncInfoElement);
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}finally{
			session.close();
		}
	}

	private Element convertSyncInfo2Element(SyncInfo syncInfo) {
		Element syncInfoElement;
		try {
			syncInfoElement = syncInfoParser.convertSyncInfo2Element(syncInfo);
		} catch (DocumentException e) {
			Logger.error(e.getMessage(), e); // TODO (JMT) throws an exception
			return null;
		}
		return syncInfoElement;
	}

	@SuppressWarnings("unchecked")
	public List<SyncInfo> getAll(String entityName) {
		List<Element> syncElements = getAllSyncInfo(entityName);
		
		ArrayList<SyncInfo> result = new ArrayList<SyncInfo>(); 
		for (Element syncInfoElement : syncElements) {
			SyncInfo syncInfo = this.convertElement2SyncInfo(syncInfoElement);
			result.add(syncInfo);
		}
		return result;

	}

	@SuppressWarnings("unchecked")
	private List<Element> getAllSyncInfo(String entityName) {
		String syncQuery ="FROM " + SYNC_INFO + " WHERE " + SYNC_INFO_ATTR_ENTITY_NAME + " = '" + entityName + "'";
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);

		List<Element> syncElements = dom4jSession.createQuery(syncQuery).list();
		
		session.close();
		return syncElements;
	}

	public String newSyncID(EntityContent content) {
		return IdGenerator.newID();
	}

	private File getMapping() {
		File syncMapping = new File(this.getClass().getResource("SyncInfo.hbm.xml").getFile());   // TODO (JMT) inject sync info mapping name -> Spring?
		return syncMapping;
	}

	public String getEntityName() {
		return SYNC_INFO;
	}

}
