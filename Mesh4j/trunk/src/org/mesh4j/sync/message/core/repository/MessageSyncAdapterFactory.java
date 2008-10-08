package org.mesh4j.sync.message.core.repository;

import java.io.File;

import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;

public class MessageSyncAdapterFactory implements IMessageSyncAdapterFactory {

	// MODEL VARIABLES
	private String baseDirectory = "";
	private boolean supportInMemoryAdapter = false;
	
	// BUSINESS METHODS
	
	public MessageSyncAdapterFactory(String baseDirectory, boolean supportInMemoryAdapter){
		Guard.argumentNotNull(baseDirectory, "baseDirectory");
		
		this.baseDirectory = baseDirectory;
		this.supportInMemoryAdapter = supportInMemoryAdapter;
	}
	
	@Override
	public IMessageSyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) {
		
		IMessageSyncAdapter syncAdapter = null;
		if(KMLDOMLoaderFactory.isKML(sourceId)){
			String kmlFileName = this.baseDirectory + sourceId;
			DOMAdapter kmlAdapter = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(kmlFileName, identityProvider));
			syncAdapter = new MessageSyncAdapter(sourceId, identityProvider, kmlAdapter);
		} else {
			if(this.supportInMemoryAdapter){
				syncAdapter = new InMemoryMessageSyncAdapter(sourceId);
			} else {
				String feedFileName = this.baseDirectory + sourceId;
				File feedFile = new File(feedFileName);
				Feed feed = new Feed();
				FeedAdapter feedAdapter = new FeedAdapter(feedFile, RssSyndicationFormat.INSTANCE, identityProvider, IdGenerator.INSTANCE, feed);
				syncAdapter = new MessageSyncAdapter(sourceId, identityProvider, feedAdapter);
			}
		}
		return syncAdapter;
	}

}
