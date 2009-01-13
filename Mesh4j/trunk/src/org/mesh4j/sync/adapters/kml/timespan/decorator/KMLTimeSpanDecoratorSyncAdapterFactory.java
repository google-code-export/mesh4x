package org.mesh4j.sync.adapters.kml.timespan.decorator;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.security.IIdentityProvider;

public class KMLTimeSpanDecoratorSyncAdapterFactory implements ISyncAdapterFactory{

	// MODEL VARIABLES
	private ISyncAdapterFactory syncFactory;
	private String baseDirectory;
	private IKMLGeneratorFactory kmlGeneratorFactory;
	
	// BUSINESS METHODS
	
	public KMLTimeSpanDecoratorSyncAdapterFactory(String baseDirectory, ISyncAdapterFactory syncFactory, IKMLGeneratorFactory kmlGeneratorFactory) {
		super();		
		this.syncFactory = syncFactory;
		this.baseDirectory = baseDirectory;
		this.kmlGeneratorFactory = kmlGeneratorFactory;
	}
	
	public KMLTimeSpanDecoratorSyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception{
		ISyncAdapter syncAdapter = this.syncFactory.createSyncAdapter(sourceId, identityProvider);
		
		String sourceName = this.syncFactory.getSourceName(sourceId);
		String kmlFileName = this.baseDirectory + "/" + sourceName + ".kml";
		
		IKMLGenerator kmlGenerator = this.kmlGeneratorFactory.createKMLGenereator(sourceName);
		KMLTimeSpanDecoratorSyncAdapter syncAdapterDecorator = new KMLTimeSpanDecoratorSyncAdapter(kmlGenerator, sourceName, kmlFileName, syncAdapter);
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
	public String getSourceType() {
		return this.syncFactory.getSourceType();
	}
}
