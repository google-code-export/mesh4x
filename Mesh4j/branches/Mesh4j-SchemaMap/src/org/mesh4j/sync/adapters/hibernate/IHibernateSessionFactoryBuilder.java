package org.mesh4j.sync.adapters.hibernate;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.mesh4j.sync.adapters.hibernate.mapping.IHibernateToXMLMapping;

import com.hp.hpl.jena.rdf.model.Resource;

public interface IHibernateSessionFactoryBuilder {

	public SessionFactory buildSessionFactory();

	public String getIdentifierPropertyName(SessionFactory sessionFactory, String entityName);
	
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName);
	
	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName, String idNode);

	public boolean isMsAccess();

/*	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName, String idNode, 
			Map<String, Resource> syncSchema, Map<String, String> schemaConversionMap);

	public IHibernateToXMLMapping buildMeshMapping(SessionFactory sessionFactory, String entityName,
			Map<String, Resource> syncSchema, Map<String, String> schemaConversionMap);*/

}