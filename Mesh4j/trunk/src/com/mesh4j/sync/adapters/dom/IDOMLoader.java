package com.mesh4j.sync.adapters.dom;

import com.mesh4j.sync.security.IIdentityProvider;

public interface IDOMLoader {

	IIdentityProvider getIdentityProvider();

	void read();

	void write();

	IMeshDOM getDOM();

	String getFriendlyName();

}