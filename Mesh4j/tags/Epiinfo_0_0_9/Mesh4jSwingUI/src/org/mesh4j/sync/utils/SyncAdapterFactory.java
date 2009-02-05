package org.mesh4j.sync.utils;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.http.HttpSyncAdapterFactory;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.properties.PropertiesProvider;
import org.mesh4j.sync.security.IIdentityProvider;

public class SyncAdapterFactory implements ISyncAdapterFactory {

	// MODEL VARIABLES
	private KMLDOMLoaderFactory kmlFactory;
	private HttpSyncAdapterFactory httpFactory;
	
	// BUSINESS METHODS
	public SyncAdapterFactory(PropertiesProvider propertiesProvider) {
		super();
		this.kmlFactory = new KMLDOMLoaderFactory();
		this.httpFactory = new HttpSyncAdapterFactory();
	}

	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return this.kmlFactory.acceptsSource(sourceId, sourceDefinition) ||
			this.httpFactory.acceptsSource(sourceId, sourceDefinition);
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		if(this.kmlFactory.acceptsSource(sourceAlias, sourceDefinition)){
			return this.kmlFactory.createSyncAdapter(sourceAlias, sourceDefinition, identityProvider);
		} else if(this.httpFactory.acceptsSource(sourceAlias, sourceDefinition)){
			return this.httpFactory.createSyncAdapter(sourceAlias, sourceDefinition, identityProvider);
		} else {
			if(isKml(sourceDefinition)){
				return this.kmlFactory.createSyncAdapter(sourceAlias, KMLDOMLoaderFactory.createSourceDefinition(sourceDefinition), identityProvider);
			} else{
				return null;
			}
		}
	}

	@Override
	public String getSourceType() {
		return "";
	}

	public boolean isKml(String endpoint) {
		return KMLDOMLoaderFactory.isKML(endpoint);
	}

	public boolean isHTTP(String endpoint) {
		return HttpSyncAdapterFactory.isURL(endpoint);
	}
}
