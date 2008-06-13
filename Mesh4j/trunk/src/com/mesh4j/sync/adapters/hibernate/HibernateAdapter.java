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

import com.mesh4j.sync.AbstractSyncAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.adapters.SyncInfo;
import com.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import com.mesh4j.sync.filter.SinceLastUpdateFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.parsers.SyncInfoParser;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.validations.Guard;

public class HibernateAdapter extends AbstractSyncAdapter implements ISessionProvider {
	
	// MODEL VARIABLES
	private SyncDAO syncDAO;
	private EntityDAO entityDAO;
	private SessionFactory sessionFactory;
	private Session currentSession;
	private IIdentityProvider identityProvider;
	
	// BUSINESS METHODs
	public HibernateAdapter(String fileMappingName, IIdentityProvider identityProvider){
		super();
		
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNullOrEmptyString(fileMappingName, "fileMappingName");
		
		initialize(new File(fileMappingName), identityProvider);
	}
	
	public HibernateAdapter(File entityMapping, IIdentityProvider identityProvider){
		super();
		initialize(entityMapping, identityProvider);
	}

	private void initialize(File entityMapping,
			IIdentityProvider identityProvider) {
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(entityMapping, "entityMapping");
		
		if(!entityMapping.exists() || !entityMapping.canRead()){
			Guard.throwsArgumentException("Arg_InvalidHibernateFileMapping", entityMapping.getName());
		}
		
		this.identityProvider = identityProvider;
		
		this.initializeHibernate(SyncDAO.getMapping(), entityMapping);
		
		this.syncDAO = new SyncDAO(this, new SyncInfoParser(RssSyndicationFormat.INSTANCE, this.identityProvider));
		
		ClassMetadata classMetadata = this.getClassMetadata();
		String entityName = classMetadata.getEntityName();
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

	// TODO (JMT) REFACTORING: use dbCommand or AOP (Transaction annotation with Spring)
	@Override
	public void add(Item item) {
		
		Guard.argumentNotNull(item, "item");

		EntityContent entity = entityDAO.normalizeContent(item.getContent());
		
		Session session =  newSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			
			
			SyncInfo syncInfo = null;
			if (!item.isDeleted())
			{
				entityDAO.save(entity);
				syncInfo = new SyncInfo(item.getSync(), entity.getType(), entity.getId(), entity.getVersion());
			} else {
				syncInfo = new SyncInfo(item.getSync(), entityDAO.getEntityName(), item.getContent().getId(), item.getContent().getPayload().asXML().hashCode());	
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
				syncInfo.getSync().delete(this.getAuthenticatedUser(), new Date());
				syncDAO.save(syncInfo);
				
				entityDAO.delete(syncInfo.getId());
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
				EntityContent entity = entityDAO.get(syncInfo.getId());
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
				SyncInfo syncInfo = new SyncInfo(item.getSync(), entity.getType(), entity.getId(), entity.getVersion());
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
		
		EntityContent entity = entityDAO.get(syncInfo.getId());
		closeSession();
		
		this.updateSyncIfChanged(entity, syncInfo);
		
		
		if(syncInfo.isDeleted()){
			NullContent nullEntity = new NullContent(syncInfo.getSyncId());
			return new Item(nullEntity, syncInfo.getSync());
		} else {
			return new Item(entity, syncInfo.getSync());			
		}
	}

	private void updateSyncIfChanged(EntityContent entity, SyncInfo syncInfo){
		Session session = newSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
		
			Sync sync = syncInfo.getSync();
			if (entity != null && sync == null)
			{
				// Add sync on-the-fly.
				sync = new Sync(syncInfo.getSyncId(), this.getAuthenticatedUser(), new Date(), false);
				syncInfo.updateSync(sync);
				syncDAO.save(syncInfo);
			}
			else if (entity == null && sync != null)
			{
				if (!sync.isDeleted())
				{
					sync.delete(this.getAuthenticatedUser(), new Date());
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
					sync.update(this.getAuthenticatedUser(), new Date(), sync.isDeleted());
					syncInfo.setVersion(entity.getVersion());
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
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
	
		ArrayList<Item> result = new ArrayList<Item>();
		
		Session session = newSession();
		List<EntityContent> entities = entityDAO.getAll();
		List<SyncInfo> syncInfos = syncDAO.getAll(entityDAO.getEntityName());
		closeSession();
		
		Map<String, SyncInfo> syncInfoAsMapByEntity = this.makeSyncMapByEntity(syncInfos);
 
		for (EntityContent entity : entities) {
			
			SyncInfo syncInfo = syncInfoAsMapByEntity.get(entity.getId());			

			Sync sync;
			if(syncInfo == null){
				sync = new Sync(syncDAO.newSyncID(), this.getAuthenticatedUser(), new Date(), false);
				
				SyncInfo newSyncInfo = new SyncInfo(sync, entity.getType(), entity.getId(), entity.getVersion());
				
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
				updateSyncIfChanged(entity, syncInfo);
			}
			Item item = new Item(entity, sync);
			
			if(appliesFilter(item, since, filter)){
				result.add(item);
			}

		}

		for (SyncInfo syncInfo : syncInfos) {
			updateSyncIfChanged(null, syncInfo);
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
			syncInfoMap.put(syncInfo.getId(), syncInfo);
		}
		return syncInfoMap;
	}

	private boolean appliesFilter(Item item, Date since, IFilter<Item> filter) {  
		// TODO (JMT) db filter
		boolean dateOk = SinceLastUpdateFilter.applies(item, since);
		return filter.applies(item) && dateOk;
	}

	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(HibernateAdapter.class.getName());
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
	
	@Override
	public String getAuthenticatedUser() {
		return this.identityProvider.getAuthenticatedUser();
	}
}
