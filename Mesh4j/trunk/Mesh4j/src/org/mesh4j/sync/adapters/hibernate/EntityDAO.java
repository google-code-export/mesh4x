package org.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.mesh4j.sync.model.IContent;


public class EntityDAO {

	// MODEL VARIABLES
	private String entityName;
	private String entityIDNode;
	private ISessionProvider sessionProvider;
	
	// BUSINESS METHODS
	
	public EntityDAO(String entityName, String entityIDNode, ISessionProvider sessionProvider) {
		super();
		this.entityName = entityName;
		this.entityIDNode = entityIDNode;
		this.sessionProvider = sessionProvider;
	}

	public EntityContent get(String entityId) {
		Session session = getSession();
		Element entityElement = (Element) session.get(this.entityName, entityId);
		if(entityElement == null){
			return null;
		} else {
			return new EntityContent(entityElement, this.entityName, entityId);
		}
	}

	public void save(EntityContent entity) {
		Session session = getSession();
		session.saveOrUpdate(entity.getPayload().createCopy());
	}

	public void delete(EntityContent entity) {
		this.delete(entity.getId());
	}
	
	public void delete(String entityId) {
		Session session = getSession();
		Element entityElement = (Element) session.get(this.entityName, entityId);
		if(entityElement != null){
			session.delete(this.entityName, entityElement);	
		}	
	}

	public void update(EntityContent entity) {
		Session session = getSession();
		session.saveOrUpdate(entity.getPayload().createCopy());		
	}

	@SuppressWarnings("unchecked")
	public List<EntityContent> getAll() {
		String hqlQuery ="FROM " + this.entityName;
		Session session = this.getSession();
		
		List<Element> entities = session.createQuery(hqlQuery).list();
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
	
	private Session getSession() {
		return this.sessionProvider.getCurrentSession().getSession(EntityMode.DOM4J);
	}

}
