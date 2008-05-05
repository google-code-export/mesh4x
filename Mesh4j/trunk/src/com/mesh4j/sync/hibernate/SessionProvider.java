package com.mesh4j.sync.hibernate;

import org.hibernate.Session;

public interface SessionProvider {

	Session getCurrentSession();
}
