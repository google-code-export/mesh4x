package org.mesh4j.sync.adapters.hibernate;

import org.hibernate.SessionFactory;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;

public interface IHibernateSessionFactoryBuilder {

	public SessionFactory buildSessionFactory();

	public String getIdentifierPropertyName(SessionFactory sessionFactory, String entityName);
	
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName);
	
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName, String idNode);

	public boolean isMsAccess();

}