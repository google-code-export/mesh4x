package com.mesh4j.sync.adapters.kml;

import com.mesh4j.sync.security.IIdentityProvider;

public interface IKMLMeshDomLoader {

	IIdentityProvider getIdentityProvider();

	void read();

	void write();

	IKMLMeshDocument getDocument();

}