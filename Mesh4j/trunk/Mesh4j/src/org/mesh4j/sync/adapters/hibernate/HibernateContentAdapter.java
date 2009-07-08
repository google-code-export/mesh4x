package org.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.mesh4j.sync.adapters.IdentifiableContent;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.adapters.split.IIdentifiableContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class HibernateContentAdapter implements IIdentifiableContentAdapter {

	// MODEL VARIABLES
	private IHibernateToXMLMapping mapping;
	private SessionFactory sessionFactory;
	
	// BUSINESS METHODS
	
	public HibernateContentAdapter(IHibernateSessionFactoryBuilder sessionFactoryBuilder, String entityName){
		Guard.argumentNotNull(sessionFactoryBuilder, "sessionFactoryBuilder");
		Guard.argumentNotNullOrEmptyString(entityName, "entityName");
		initializeSessionFactory(sessionFactoryBuilder, entityName);
	}

	public void initializeSessionFactory(IHibernateSessionFactoryBuilder sessionFactoryBuilder, String entityName) {
		if(this.sessionFactory != null){
			this.sessionFactory.close();
		}
		
		this.sessionFactory = sessionFactoryBuilder.buildSessionFactory();
		this.mapping = sessionFactoryBuilder.buildMeshMapping(entityName);
	}

	@Override
	public IdentifiableContent get(String meshId) {
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element entityElement = null;
		
		try{
			entityElement = (Element) dom4jSession.get(this.getType(), this.mapping.getHibernateId(meshId));
		}catch (Exception e) {
			throw new MeshException(e);
		}finally{
			session.close();
		}
		
		if(entityElement == null){
			return null;
		} else {
			return new IdentifiableContent(convertRowToXML(meshId, entityElement), this.mapping, meshId);
		}
	}

	public void save(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		
		Session session =  this.sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			Session dom4jSession = session.getSession(EntityMode.DOM4J);
			dom4jSession.saveOrUpdate(convertXMLToRow(entityContent.getPayload()));
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
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		Session session =  this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);
		Element entityElement = null;
		
		try{
			entityElement = (Element) dom4jSession.get(this.getType(), this.mapping.getHibernateId(entityContent.getId()));
		}catch (Exception e) {
			throw new MeshException(e);
		}finally{
			if(entityElement == null){
				session.close();
				return;
			}
		}
		
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			dom4jSession.delete(this.getType(), entityElement);
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

	public void deleteAll() {
		Session session = this.sessionFactory.openSession();
		Transaction tx = null;
		try{
			tx = session.beginTransaction();
			String hqlDelete = "delete " + this.getType();
			Session dom4jSession = session.getSession(EntityMode.DOM4J);		
			dom4jSession.createQuery(hqlDelete).executeUpdate();		
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
		String hqlQuery ="FROM " + this.getType();
		Session session = this.sessionFactory.openSession();
		Session dom4jSession = session.getSession(EntityMode.DOM4J);		
		List<Element> entities = null;
		
		try{
			entities = dom4jSession.createQuery(hqlQuery).list();
		}finally{
			session.close();
		}
		
		ArrayList<IContent> result = new ArrayList<IContent>();
		for (Element entityElement : entities) {
			try{
				String entityID = this.mapping.getMeshId(entityElement);
				IdentifiableContent entity = new IdentifiableContent(convertRowToXML(entityID, entityElement), this.mapping, entityID);
				result.add(entity);
			}catch (Exception e) {
				throw new MeshException(e);
			}
		}
		return result;
	}
	
	public String getType() {
		return this.mapping.getType();
	}
	
	private Element convertXMLToRow(Element payload) {
		try{
			return this.mapping.convertXMLToRow(payload.createCopy());
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private Element convertRowToXML(String meshId, Element entityElement){
		try{
			return this.mapping.convertRowToXML(meshId, entityElement);
		}catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public List<IContent> getAll() {
		return getAll(null);
	}

	@Override
	public String getID(IContent content) {
		IdentifiableContent entityContent = IdentifiableContent.normalizeContent(content, this.mapping);
		if(entityContent == null){
			return null;
		} else {
			return entityContent.getId();
		}
	}

	public IHibernateToXMLMapping getMapping() {
		return this.mapping;		
	}
	
	@Override
	public ISchema getSchema(){
		return this.mapping.getSchema();
	}
	
}
