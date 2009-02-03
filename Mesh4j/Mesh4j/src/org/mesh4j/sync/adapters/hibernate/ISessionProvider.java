package org.mesh4j.sync.adapters.hibernate;

import org.hibernate.Session;

public interface ISessionProvider {

	Session getCurrentSession();
}
