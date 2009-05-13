package org.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;
import org.mesh4j.sync.model.IContent;


public class EntityDAO {

	// MODEL VARIABLES
	private IHibernateToXMLMapping mapping;
	private ISessionProvider sessionProvider;
	
	// BUSINESS METHODS
	
	public EntityDAO(ISessionProvider sessionProvider, IHibernateToXMLMapping mapping) {
		super();
		this.mapping = mapping;
		this.sessionProvider = sessionProvider;
	}

	public EntityContent get(String entityId) throws Exception {
		Session session = getSession();
		Element entityElement = (Element) session.get(this.getEntityName(), entityId);
		if(entityElement == null){
			return null;
		} else {
			return new EntityContent(this.mapping.convertRowToXML(entityId, entityElement), this.getEntityName(), entityId);
		}
	}

	public void save(EntityContent entity) throws Exception {
		Session session = getSession();
		session.saveOrUpdate(this.mapping.convertXMLToRow(entity.getPayload().createCopy()));
	}

	public void delete(EntityContent entity) {
		this.delete(entity.getId());
	}
	
	public void delete(String entityId) {
		Session session = getSession();
		Element entityElement = (Element) session.get(this.getEntityName(), entityId);
		if(entityElement != null){
			session.delete(this.getEntityName(), entityElement);	
		}	
	}

	public void update(EntityContent entity) throws Exception {
		Session session = getSession();
		session.saveOrUpdate(this.mapping.convertXMLToRow(entity.getPayload().createCopy()));
	}

	@SuppressWarnings("unchecked")
	public List<EntityContent> getAll() throws Exception {
		String hqlQuery ="FROM " + this.getEntityName();
		Session session = this.getSession();
		
		List<Element> entities = session.createQuery(hqlQuery).list();
		ArrayList<EntityContent> result = new ArrayList<EntityContent>();
		for (Element entityElement : entities) {
			String entityID = entityElement.element(getIDNode()).getText();
			EntityContent entity = new EntityContent(this.mapping.convertRowToXML(entityID, entityElement), this.getEntityName(), entityID);
			result.add(entity);
		}
		return result;
	}

	private String getIDNode() {
		return this.mapping.getIDNode();
	}

	public EntityContent normalizeContent(IContent content){
		return EntityContent.normalizeContent(content, this.getEntityName(), this.getIDNode());
	}
	
	public String getEntityName() {
		return this.mapping.getEntityNode();
	}
	
	private Session getSession() {
		return this.sessionProvider.getCurrentSession().getSession(EntityMode.DOM4J);
	}

}
