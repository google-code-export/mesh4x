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
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.split.ISyncRepository;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class HibernateSyncRepository implements ISyncRepository, ISyncAware{

	// CONSTANTS	
	private final static Log Logger = LogFactory.getLog(HibernateSyncRepository.class);
	
	// MODEL VARIABLES
	private IHibernateSessionFactoryBuilder sessionFactoryBuilder;
	private SyncInfoParser syncInfoParser;
	private SessionFactory sessionFactory;
	
	// BUSINESS METHODS
	public HibernateSyncRepository(IHibernateSessionFactoryBuilder sessionFactoryBuilder, SyncInfoParser syncInfoParser) {
		Guard.argumentNotNull(sessionFactoryBuilder, "sessionFactoryBuilder");
		this.syncInfoParser = syncInfoParser;
		initializeSessionFactory(sessionFactoryBuilder);
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
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}
		return syncInfo;
	}

	private Element getSyncInfo(String syncId) {
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element syncInfoElement = null;
		try{
			syncInfoElement = (Element) dom4jSession.get(getEntityName(), syncId);
		} catch (Exception e) {
			// TODO: handle exception
		}
		finally{
			session.close();
		}
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
			dom4jSession.saveOrUpdate(getEntityName(), syncInfoElement);
			tx.commit();
		}catch (Throwable e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new MeshException(e);
		}finally{
			session.close();
		}
	}

	private Element convertSyncInfo2Element(SyncInfo syncInfo) {
		Element syncInfoElement = syncInfoParser.convertSyncInfo2Element(syncInfo);
		return syncInfoElement;
	}

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
		String syncQuery ="FROM " + getEntityName() + " WHERE " + SYNC_INFO_ATTR_ENTITY_NAME + " = '" + entityName + "'";
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);

		List<Element> syncElements = null;
		
		try{
			syncElements = dom4jSession.createQuery(syncQuery).list();
		} finally{
			session.close();
		}
		return syncElements;
	}

	public String newSyncID(IContent content) {
		return IdGenerator.INSTANCE.newID();
	}

	public String getEntityName() {
		return this.syncInfoParser.getEntityName();
	}
	
	public void initializeSessionFactory(IHibernateSessionFactoryBuilder sessionFactoryBuilder) {
		this.sessionFactoryBuilder = sessionFactoryBuilder;
		
		if(this.sessionFactory != null){
			this.sessionFactory.close();
		}
		
		this.sessionFactory = sessionFactoryBuilder.buildSessionFactory();
	}

	@Override
	public void beginSync() {
		if(this.sessionFactory == null || this.sessionFactory.isClosed()){
			initializeSessionFactory(this.sessionFactoryBuilder);
		}
	}

	@Override
	public void endSync() {
		try{
			SessionFactory factory = this.sessionFactory;
			this.sessionFactory = null;
			factory.close();
		} catch (Throwable e) {
			throw new MeshException(e);
		}
	}
}
