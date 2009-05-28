package org.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.mesh4j.sync.AbstractSyncAdapter;
import org.mesh4j.sync.IFilter;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.filter.SinceLastUpdateFilter;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.NullContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class HibernateAdapter extends AbstractSyncAdapter implements ISessionProvider {
	
	// MODEL VARIABLES
	private SyncDAO syncDAO;
	private EntityDAO entityDAO;
	private SessionFactory sessionFactory;
	private Session currentSession;
	private IIdentityProvider identityProvider;
	private IIdGenerator idGenerator;
	
	// BUSINESS METHODs
	public HibernateAdapter(IHibernateSessionFactoryBuilder builder, IIdentityProvider identityProvider, IIdGenerator idGenerator){
		Guard.argumentNotNull(builder, "builder");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		Guard.argumentNotNull(idGenerator, "idGenerator");
		initialize(builder, identityProvider, idGenerator);
	}
	
	private void initialize(IHibernateSessionFactoryBuilder builder, IIdentityProvider identityProvider, IIdGenerator idGenerator) {

		this.identityProvider = identityProvider;
		this.idGenerator = idGenerator;
		
		this.sessionFactory = builder.buildSessionFactory();
		
		ClassMetadata classMetadata = this.getClassMetadata();
		String entityName = classMetadata.getEntityName();
		String entityIDNode = classMetadata.getIdentifierPropertyName();

		this.syncDAO = new SyncDAO(this, new SyncInfoParser(RssSyndicationFormat.INSTANCE, this.identityProvider, this.idGenerator, entityName+"_sync"));
		this.entityDAO = new EntityDAO(this, builder.buildMeshMapping(entityName, entityIDNode));
	}

	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(){
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		for (String entityName : map.keySet()) {
			if(!entityName.endsWith("_sync")){
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
		}catch (Exception e) {
			if (tx != null) {
				tx.rollback();
			}
			throw new MeshException(e);
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
				EntityContent entity;
				try{
					entity = entityDAO.get(syncInfo.getId());
				} catch (Exception e) {
					throw new MeshException(e);
				} finally{
					closeSession();
				}
				
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
			}catch (Exception e) {
				if (tx != null) {
					tx.rollback();
				}
				throw new MeshException(e);
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
		
		EntityContent entity = null;
		try{
			entity = entityDAO.get(syncInfo.getId());
		}catch (Exception e) {
			throw new MeshException(e);
		}finally{
			closeSession();
		}
		
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
				if ((!syncInfo.isDeleted() && syncInfo.contentHasChanged(entity)) || syncInfo.isDeleted())
				{
					sync.update(this.getAuthenticatedUser(), new Date(), false);
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
		
		List<EntityContent> entities;
		List<SyncInfo> syncInfos;
		
		Session session = newSession();
		try{
			entities = entityDAO.getAll();
			syncInfos = syncDAO.getAll(entityDAO.getEntityName());
		} catch (Exception e) {
			throw new MeshException(e);
		} finally {
			closeSession();
		}
		
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
