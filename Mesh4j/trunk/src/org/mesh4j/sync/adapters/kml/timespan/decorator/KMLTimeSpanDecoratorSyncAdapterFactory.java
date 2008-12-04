package org.mesh4j.sync.adapters.kml.timespan.decorator;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.security.IIdentityProvider;

public class KMLTimeSpanDecoratorSyncAdapterFactory implements ISyncAdapterFactory{

	// MODEL VARIABLES
	private ISyncAdapterFactory syncFactory;
	private String baseDirectory;
	private IKMLGenerator kmlGenerator;
	
	// BUSINESS METHODS
	
	public KMLTimeSpanDecoratorSyncAdapterFactory(String baseDirectory, ISyncAdapterFactory syncFactory, IKMLGenerator kmlGenerator) {
		super();		
		this.syncFactory = syncFactory;
		this.baseDirectory = baseDirectory;
		this.kmlGenerator = kmlGenerator;
	}
	
	public ISyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception{
		ISyncAdapter syncAdapter = this.syncFactory.createSyncAdapter(sourceId, identityProvider);
		
		String sourceName = this.syncFactory.getSourceName(sourceId);
		String kmlFileName = this.baseDirectory + "/" + sourceName + ".kml";
		ISyncAdapter syncAdapterDecorator = new KMLTimeSpanDecoratorSyncAdapter(this.kmlGenerator, sourceName, kmlFileName, syncAdapter);
		return syncAdapterDecorator;
	}

	@Override
	public boolean acceptsSourceId(String sourceId) {
		return this.syncFactory.acceptsSourceId(sourceId);
	}

	@Override
	public String getSourceName(String sourceId) {
		return this.syncFactory.getSourceName(sourceId);
	}

	@Override
	public String getSourceType(String sourceId) {
		return this.syncFactory.getSourceType(sourceId);
	}
}
