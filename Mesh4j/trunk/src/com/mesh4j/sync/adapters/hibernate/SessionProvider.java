package com.mesh4j.sync.adapters.hibernate;

import org.hibernate.Session;

public interface SessionProvider {

	Session getCurrentSession();
}
