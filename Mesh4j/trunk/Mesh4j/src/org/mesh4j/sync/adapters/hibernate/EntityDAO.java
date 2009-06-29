package org.mesh4j.sync.adapters.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.mesh4j.sync.adapters.IdentifiableContent;
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

	public IdentifiableContent get(String entityId) throws Exception {
		Session session = getSession();
		Element entityElement = (Element) session.get(this.getEntityName(), entityId);
		if(entityElement == null){
			return null;
		} else {
			return new IdentifiableContent(this.mapping.convertRowToXML(entityId, entityElement), this.mapping, entityId);
		}
	}

	public void save(IdentifiableContent entity) throws Exception {
		Session session = getSession();
		session.saveOrUpdate(this.mapping.convertXMLToRow(entity.getPayload().createCopy()));
	}

	public void delete(IdentifiableContent entity) {
		this.delete(entity.getId());
	}
	
	public void delete(String entityId) {
		Session session = getSession();
		Element entityElement = (Element) session.get(this.getEntityName(), entityId);
		if(entityElement != null){
			session.delete(this.getEntityName(), entityElement);	
		}	
	}

	public void update(IdentifiableContent entity) throws Exception {
		Session session = getSession();
		session.saveOrUpdate(this.mapping.convertXMLToRow(entity.getPayload().createCopy()));
	}

	@SuppressWarnings("unchecked")
	public List<IdentifiableContent> getAll() throws Exception {
		String hqlQuery ="FROM " + this.getEntityName();
		Session session = this.getSession();
		
		List<Element> entities = session.createQuery(hqlQuery).list();
		ArrayList<IdentifiableContent> result = new ArrayList<IdentifiableContent>();
		for (Element entityElement : entities) {
			String entityID = this.mapping.getMeshId(entityElement);
			IdentifiableContent entity = new IdentifiableContent(this.mapping.convertRowToXML(entityID, entityElement), this.mapping, entityID);
			result.add(entity);
		}
		return result;
	}

	public IdentifiableContent normalizeContent(IContent content){
		return IdentifiableContent.normalizeContent(content, this.mapping);
	}
	
	public String getEntityName() {
		return this.mapping.getType();
	}
	
	private Session getSession() {
		return this.sessionProvider.getCurrentSession().getSession(EntityMode.DOM4J);
	}

	public IHibernateToXMLMapping getMapping() {
		return this.mapping;
	}

}
