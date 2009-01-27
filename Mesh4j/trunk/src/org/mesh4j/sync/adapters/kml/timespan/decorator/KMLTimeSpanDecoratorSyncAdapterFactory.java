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
	
	public KMLTimeSpanDecoratorSyncAdapter createSyncAdapter(String sourceAlias, String sourceDefinition, IIdentityProvider identityProvider) throws Exception{
		ISyncAdapter syncAdapter = this.syncFactory.createSyncAdapter(sourceAlias, sourceDefinition, identityProvider);
		
		String kmlFileName = this.baseDirectory + "/" + sourceAlias + ".kml";
		
		IKMLGenerator kmlGenerator = this.kmlGeneratorFactory.createKMLGenereator(sourceAlias);
		KMLTimeSpanDecoratorSyncAdapter syncAdapterDecorator = new KMLTimeSpanDecoratorSyncAdapter(kmlGenerator, sourceAlias, kmlFileName, syncAdapter);
		return syncAdapterDecorator;
	}

	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		return this.syncFactory.acceptsSource(sourceId, sourceDefinition);
	}

	@Override
	public String getSourceType() {
		return this.syncFactory.getSourceType();
	}
}
