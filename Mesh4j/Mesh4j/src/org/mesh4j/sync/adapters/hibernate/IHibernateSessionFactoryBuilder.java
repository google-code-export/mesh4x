package org.mesh4j.sync.adapters.hibernate;

import org.hibernate.SessionFactory;

public interface IHibernateSessionFactoryBuilder {

	public abstract SessionFactory buildSessionFactory();

}