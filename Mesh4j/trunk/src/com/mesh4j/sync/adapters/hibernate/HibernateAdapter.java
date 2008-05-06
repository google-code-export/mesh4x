package com.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import com.mesh4j.sync.AbstractRepositoryAdapter;
import com.mesh4j.sync.Filter;
import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.security.Security;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.validations.Guard;

/**
 * Use CompoundRepositoryAdapter
 */
@Deprecated

public class HibernateAdapter extends AbstractRepositoryAdapter implements SessionProvider {
	
	// MODEL VARIABLES
	private SyncDAO syncDAO;
	private EntityDAO entityDAO;
	private SessionFactory sessionFactory;
	private Session currentSession;
	
	// BUSINESS METHODs
	public HibernateAdapter(String fileMappingName){
		this(new File(fileMappingName));		
	}
	
	public HibernateAdapter(File entityMapping){
		
		this.initializeHibernate(SyncDAO.getMapping(), entityMapping);
		
		this.syncDAO = new SyncDAO(this, new SyncInfoParser());
		
		ClassMetadata classMetadata = this.getClassMetadata();
		String entityName = classMetadata.getEntityName();						// TODO (JMT) set node attribute value
		String entityIDNode = classMetadata.getIdentifierPropertyName();
		this.entityDAO = new EntityDAO(entityName, entityIDNode, this);
	}

	private void initializeHibernate(File syncMapping, File entityMapping) {
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.addFile(entityMapping);	
		hibernateConfiguration.addFile(syncMapping);		
		this.sessionFactory = hibernateConfiguration.buildSessionFactory();
	}
	
	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(){
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		for (String entityName : map.keySet()) {
			if(!syncDAO.getEntityName().equals(entityName)){
				ClassMetadata classMetadata = map.get(entityName);
				return classMetadata;
			}
		}
		return null;
	}

	// TODO (JMT) use dbCommand or AOP (Transaction annotation with Spring)
	@Override
	public void add(Item item) {
		
		Guard.argumentNotNull(item, "item");

		EntityContent entity = entityDAO.normalizeContent(item.getContent());
		
		Session session =  newSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			
			if (!item.isDeleted())
			{
				entityDAO.save(entity);
			}
			SyncInfo syncInfo = new SyncInfo(item.getSync(), entity);
			syncDAO.save(syncInfo);
			
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}finally{
			closeSession();
		}
	}

	@Override
	public void delete(String syncId) {
		
		Guard.argumentNotNullOrEmptyString(syncId, "id");

		Session session = newSession();
		SyncInfo syncInfo = syncDAO.get(syncId);
		closeSession();
		
		session = newSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			if (syncInfo != null)
			{
				syncInfo.getSync().delete(Security.getAuthenticatedUser(), new Date());
				syncDAO.save(syncInfo);
				
				entityDAO.delete(syncInfo.getEntityId());
			}
			
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}finally{
			closeSession();
		}
	}
	
	@Override
	public void update(Item item) {
		
		Guard.argumentNotNull(item, "item");
		
		if (item.isDeleted())
		{
			Session session = newSession();
			SyncInfo syncInfo = syncDAO.get(item.getSyncId());
			closeSession();
			if(syncInfo != null){
				session = newSession();
				syncInfo.updateSync(item.getSync());
				EntityContent entity = entityDAO.get(syncInfo.getEntityId());
				closeSession();
				
				session = newSession();
				Transaction tx = null;
				try{
					tx = session.beginTransaction();
					if(entity != null){
						entityDAO.delete(entity);
					}
					syncDAO.save(syncInfo);
					tx.commit();
				}catch (RuntimeException e) {
					if (tx != null) {
						tx.rollback();
					}
					throw e;
				}finally{
					closeSession();
				}
			}
		}
		else
		{
			Session session = newSession();
			Transaction tx = null;
			try{
				tx = session.beginTransaction();
				EntityContent entity = entityDAO.normalizeContent(item.getContent());
				entityDAO.save(entity);
				SyncInfo syncInfo = new SyncInfo(item.getSync(), entity);
				syncDAO.save(syncInfo);	
				tx.commit();
			}catch (RuntimeException e) {
				if (tx != null) {
					tx.rollback();
				}
				throw e;
			}finally{
				closeSession();
			}
		}
	}

	@Override
	public Item get(String syncId) {
		
		Guard.argumentNotNullOrEmptyString(syncId, "id");

		newSession();
		SyncInfo syncInfo = syncDAO.get(syncId);
		
		if(syncInfo == null){
			return null;
		}
		
		EntityContent entity = entityDAO.get(syncInfo.getEntityId());
		closeSession();
		
		this.updateSync(entity, syncInfo);
		
		
		if(syncInfo.isDeleted()){
			NullContent nullEntity = new NullContent(syncInfo.getSyncId());
			return new Item(nullEntity, syncInfo.getSync());
		} else {
			return new Item(entity, syncInfo.getSync());			
		}
	}

	private void updateSync(EntityContent entity, SyncInfo syncInfo){
		Session session = newSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
		
			Sync sync = syncInfo.getSync();
			if (entity != null && sync == null)
			{
				// Add sync on-the-fly.
				sync = new Sync(syncInfo.getSyncId(), Security.getAuthenticatedUser(), new Date(), false);
				syncInfo.updateSync(sync);
				syncDAO.save(syncInfo);
			}
			else if (entity == null && sync != null)
			{
				if (!sync.isDeleted())
				{
					sync.delete(Security.getAuthenticatedUser(), new Date());
					syncDAO.save(syncInfo);
				}
			}
			else
			{
				/// Ensures the Sync information is current WRT the 
				/// item actual data. If it's not, a new 
				/// update will be added. Used when exporting/retrieving 
				/// items from the local stores.
				if (!syncInfo.isDeleted() && syncInfo.contentHasChanged(entity))
				{
					sync.update(Security.getAuthenticatedUser(), new Date(), sync.isDeleted());
					syncInfo.setEntityVersion(entity.getEntityVersion());
					syncDAO.save(syncInfo);
				}
			}
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}finally{
			closeSession();
		}
	}

	@Override
	protected List<Item> getAll(Date since, Filter<Item> filter) {
	
		ArrayList<Item> result = new ArrayList<Item>();
		
		Session session = newSession();
		List<EntityContent> entities = entityDAO.getAll();
		List<SyncInfo> syncInfos = syncDAO.getAll(entityDAO.getEntityName());
		closeSession();
		
		Map<String, SyncInfo> syncInfoAsMapByEntity = this.makeSyncMapByEntity(syncInfos);
 
		for (EntityContent entity : entities) {
			
			SyncInfo syncInfo = syncInfoAsMapByEntity.get(entity.getEntityId());			

			Sync sync;
			if(syncInfo == null){
				sync = new Sync(syncDAO.newSyncID(), Security.getAuthenticatedUser(), new Date(), false);
				
				SyncInfo newSyncInfo = new SyncInfo(sync, entity);
				
				session = newSession();
				Transaction tx = null;
				try{
					tx = session.beginTransaction();
					syncDAO.save(newSyncInfo);
					tx.commit();
				}catch (RuntimeException e) {
					if (tx != null) {
						tx.rollback();
					}
					throw e;
				}finally{
					closeSession();
				}	
				
			} else {
				sync = syncInfo.getSync();
				syncInfos.remove(syncInfo);
				updateSync(entity, syncInfo);
			}
			Item item = new Item(entity, sync);
			
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}

		}

		for (SyncInfo syncInfo : syncInfos) {
			updateSync(null, syncInfo);
			Item item = new Item(
				new NullContent(syncInfo.getSync().getId()),
				syncInfo.getSync());
			
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}
		}
		return result;
	}

	private Map<String, SyncInfo> makeSyncMapByEntity(List<SyncInfo> syncInfos) {
		HashMap<String, SyncInfo> syncInfoMap = new HashMap<String, SyncInfo>();
		for (SyncInfo syncInfo : syncInfos) {
			syncInfoMap.put(syncInfo.getEntityId(), syncInfo);
		}
		return syncInfoMap;
	}

	private boolean appliesFilter(Item item, Date since, Filter<Item> filter) {
		boolean dateOk = since == null || (item.getSync().getLastUpdate() == null || since.compareTo(item.getSync().getLastUpdate().getWhen()) <= 0);  // TODO (JMT) create db filter
		return filter.applies(item) && dateOk;
	}

	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(HibernateAdapter.class.getName());
	}

	@Override
	public boolean supportsMerge() {
		return false;
	}
	
	@Override
	public List<Item> merge(List<Item> items) {
		throw new UnsupportedOperationException();
	}
	
	public void deleteAll() {
		Session session = newSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			String hqlDelete = "delete " + syncDAO.getEntityName();
			session.createQuery( hqlDelete ).executeUpdate();
			
			hqlDelete = "delete " + entityDAO.getEntityName();
			session.createQuery( hqlDelete ).executeUpdate();
			
			tx.commit();
		}catch (RuntimeException e) {
			if (tx != null) {
				tx.rollback();
			}
			throw e;
		}finally{
			closeSession();
		}		
	}

	private void closeSession() {
		if(currentSession != null){
			currentSession.close();
			currentSession = null;
		}
		
	}

	private Session newSession() {
		currentSession = this.sessionFactory.openSession();
		return currentSession;
	}

	@Override
	public Session getCurrentSession() {
		return currentSession;
	}
}
