package org.mesh4j.sync.adapters.hibernate;

import org.hibernate.Session;
import org.mesh4j.sync.ISyncAware;

public interface ISessionProvider extends ISyncAware{

	Session getCurrentSession();
}
