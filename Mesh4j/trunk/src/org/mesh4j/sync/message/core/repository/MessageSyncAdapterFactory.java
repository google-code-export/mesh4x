package org.mesh4j.sync.message.core.repository;

import java.io.File;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.dom.DOMAdapter;
import org.mesh4j.sync.adapters.feed.Feed;
import org.mesh4j.sync.adapters.feed.FeedAdapter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.kml.KMLDOMLoaderFactory;
import org.mesh4j.sync.adapters.msaccess.MsAccessSyncAdapterFactory;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

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
		try{
			// TODO (JMT) create a registry of Factories
			IMessageSyncAdapter syncAdapter = null;
			if(KMLDOMLoaderFactory.isKML(sourceId)){
				String kmlFileName = this.baseDirectory + sourceId;
				DOMAdapter kmlAdapter = new DOMAdapter(KMLDOMLoaderFactory.createDOMLoader(kmlFileName, identityProvider));
				syncAdapter = new MessageSyncAdapter(sourceId, identityProvider, kmlAdapter);
			} else if(MsAccessSyncAdapterFactory.isMsAccess(sourceId)){
				String[] elements = sourceId.substring("access:".length(), sourceId.length()).split("@");
				String mdbFileName = elements[0];
				String tableName = elements[1];
				ISyncAdapter msAccessAdapter = MsAccessSyncAdapterFactory.createSyncAdapterFromFile(this.baseDirectory+"/"+mdbFileName, tableName, this.baseDirectory);
				syncAdapter = new MessageSyncAdapter(sourceId, identityProvider, msAccessAdapter);
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
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

}
