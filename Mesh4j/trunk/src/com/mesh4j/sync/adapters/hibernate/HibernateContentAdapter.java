package com.mesh4j.sync.adapters.hibernate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.compound.IContentAdapter;
import com.mesh4j.sync.model.IContent;

public class HibernateContentAdapter implements IContentAdapter {

	// MODEL VARIABLES
	private String entityName;
	private String entityIDNode;
	private SessionFactory sessionFactory;
	
	// BUSINESS METHODS
	
	public HibernateContentAdapter(File entityMapping){
			
		this.initializeHibernate(entityMapping);
		
		ClassMetadata classMetadata = this.getClassMetadata();
		this.entityName = classMetadata.getEntityName();
		this.entityIDNode = classMetadata.getIdentifierPropertyName();
	}

	private void initializeHibernate(File entityMapping) {
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.addFile(entityMapping);	
		this.sessionFactory = hibernateConfiguration.buildSessionFactory();
	}
	
	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(){
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		ClassMetadata classMetadata = map.values().iterator().next();
		return classMetadata;
	}

	public EntityContent get(String entityId) {
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element entityElement = (Element) dom4jSession.get(this.entityName, entityId);
		session.close();
		
		if(entityElement == null){
			return null;
		} else {
			return new EntityContent(entityElement, this.entityName, entityId);
		}
	}

	public void save(EntityContent entity) {
		Session session =  this.sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Session dom4jSession = session.getSession(EntityMode.DOM4J);
			dom4jSession.saveOrUpdate(entity.getPayload().createCopy());
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

	public void delete(EntityContent entity) {
		Session session =  this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element entityElement = (Element) dom4jSession.get(this.entityName, entity.getEntityId());
		if(entityElement == null){
			session.close();
			return;
		}
		
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			dom4jSession.delete(this.entityName, entityElement);
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

	@SuppressWarnings("unchecked")
	public List<EntityContent> getAll() {
		String hqlQuery ="FROM " + this.entityName;
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);		
		List<Element> entities = dom4jSession.createQuery(hqlQuery).list();
		session.close();
		
		ArrayList<EntityContent> result = new ArrayList<EntityContent>();
		for (Element entityElement : entities) {
			String entityID = entityElement.element(this.entityIDNode).getText();
			EntityContent entity = new EntityContent(entityElement, this.entityName, entityID);
			result.add(entity);
		}
		return result;
	}

	public EntityContent normalizeContent(IContent content){
		return EntityContent.normalizeContent(content, this.entityName, this.entityIDNode);
	}
	
	public String getEntityName() {
		return entityName;
	}
	
}
