package org.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;

public class HibernateContentAdapter implements IContentAdapter {

	// MODEL VARIABLES
	private String entityName;
	private String entityIDNode;
	private SessionFactory sessionFactory;
	
	// BUSINESS METHODS
	
	public HibernateContentAdapter(IHibernateSessionFactoryBuilder sessionFactoryBuilder, String entityName){
		super();
		initializeSessionFactory(sessionFactoryBuilder, entityName);
	}

	public void initializeSessionFactory(IHibernateSessionFactoryBuilder sessionFactoryBuilder, String entityName) {
		this.sessionFactory = sessionFactoryBuilder.buildSessionFactory();
		
		ClassMetadata classMetadata = this.getClassMetadata(entityName);
		this.entityName = classMetadata.getEntityName();
		this.entityIDNode = classMetadata.getIdentifierPropertyName();
	}

	
	@SuppressWarnings("unchecked")
	private ClassMetadata getClassMetadata(String entityName){
		Map<String, ClassMetadata> map = sessionFactory.getAllClassMetadata();
		for (Iterator<ClassMetadata> iterator = map.values().iterator(); iterator.hasNext();) {
			ClassMetadata classMetadata = iterator.next(); 
			if(classMetadata.getEntityName().equals(entityName)){
				return classMetadata;
			}
		}
		return null;
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

	public void save(IContent content) {
		EntityContent entityContent = EntityContent.normalizeContent(content, this.entityName, this.entityIDNode);
		
		Session session =  this.sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Session dom4jSession = session.getSession(EntityMode.DOM4J);
			dom4jSession.saveOrUpdate(entityContent.getPayload().createCopy());
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

	public void delete(IContent content) {
		Session session =  this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element entityElement = (Element) dom4jSession.get(this.entityName, content.getId());
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
	public List<IContent> getAll(Date since) {
		String hqlQuery ="FROM " + this.entityName;
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);		
		List<Element> entities = dom4jSession.createQuery(hqlQuery).list();
		session.close();
		
		ArrayList<IContent> result = new ArrayList<IContent>();
		for (Element entityElement : entities) {
			String entityID = entityElement.element(this.entityIDNode).getText();
			EntityContent entity = new EntityContent(entityElement, this.entityName, entityID);
			result.add(entity);
		}
		return result;
	}
	
	public String getType() {
		return entityName;
	}

	public List<IContent> getAll() {
		return getAll(null);
	}

	@Override
	public IContent normalize(IContent content) {
		return EntityContent.normalizeContent(content, this.entityName, this.entityIDNode);
	}
	
}
