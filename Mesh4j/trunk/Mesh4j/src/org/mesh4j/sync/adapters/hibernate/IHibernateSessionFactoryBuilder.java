package org.mesh4j.sync.adapters.hibernate;

import org.hibernate.SessionFactory;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;

public interface IHibernateSessionFactoryBuilder {

	public SessionFactory buildSessionFactory();

	public String getIdentifierPropertyName(String entityName);
	
	public IHibernateToXMLMapping buildMeshMapping(String entityName, String idNode);

	public IHibernateToXMLMapping buildMeshMapping(String entityName);

	public boolean isMsAccess();

}