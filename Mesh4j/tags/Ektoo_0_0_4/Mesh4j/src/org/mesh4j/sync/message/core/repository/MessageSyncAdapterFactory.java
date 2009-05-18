package org.mesh4j.sync.message.core.repository;

import java.util.ArrayList;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class MessageSyncAdapterFactory implements IMessageSyncAdapterFactory {

	// MODEL VARIABLES
	private boolean supportInMemoryAdapter = false;
	private IOpaqueSyncAdapterFactory defaultSyncAdapterFactory;
	private ArrayList<ISyncAdapterFactory> syncAdapterFactories = new ArrayList<ISyncAdapterFactory>();
	private ISourceIdMapper sourceIdMapper;
	
	// BUSINESS METHODS
	
	public MessageSyncAdapterFactory(ISourceIdMapper sourceIdMapper, IOpaqueSyncAdapterFactory defaultSyncAdapterFactory, boolean supportInMemoryAdapter, ISyncAdapterFactory ... allSyncAdapterFactories) {
		super();
		
		Guard.argumentNotNull(sourceIdMapper, "sourceIdMapper");
		
		this.sourceIdMapper = sourceIdMapper;
		this.supportInMemoryAdapter = supportInMemoryAdapter;
		this.defaultSyncAdapterFactory = defaultSyncAdapterFactory;
		for (ISyncAdapterFactory syncAdapterFactory : allSyncAdapterFactories) {
			this.registerSyncAdapterFactory(syncAdapterFactory);
		}
	}

	@Override
	public IMessageSyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) {
		try{
			String sourceDefinition = sourceIdMapper.getSourceDefinition(sourceId);
			IMessageSyncAdapter msgSyncAdapter = createMessageSyncAdapter(sourceId, sourceDefinition, identityProvider);
			if(msgSyncAdapter == null && this.supportInMemoryAdapter){
				msgSyncAdapter = new InMemoryMessageSyncAdapter(sourceId);
			}
			return msgSyncAdapter;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private IMessageSyncAdapter createMessageSyncAdapter(String sourceId, String sourceDefinition, IIdentityProvider identityProvider) throws Exception {
		IMessageSyncAdapter msgSyncAdapter = null;
		for (ISyncAdapterFactory syncAdapterFactory : this.syncAdapterFactories) {
			if(syncAdapterFactory.acceptsSource(sourceId, sourceDefinition)){
				ISyncAdapter syncAdapter = syncAdapterFactory.createSyncAdapter(sourceId, sourceDefinition, identityProvider);
				if(syncAdapter != null){
					msgSyncAdapter = new MessageSyncAdapter(sourceId, syncAdapterFactory.getSourceType(), identityProvider, syncAdapter, syncAdapterFactory);
				}
			}
		}
		if(msgSyncAdapter == null && this.defaultSyncAdapterFactory != null){
			String defaultSourceDefinition = this.defaultSyncAdapterFactory.createSourceDefinition(sourceId, sourceDefinition);
			ISyncAdapter syncAdapter = this.defaultSyncAdapterFactory.createSyncAdapter(sourceId, defaultSourceDefinition, identityProvider);
			msgSyncAdapter = new MessageSyncAdapter(sourceId, this.defaultSyncAdapterFactory.getSourceType(), identityProvider, syncAdapter, this.defaultSyncAdapterFactory);
		}
		return msgSyncAdapter;
	}

	public void registerSyncAdapterFactory(ISyncAdapterFactory syncAdapterFactory) {
		this.syncAdapterFactories.add(syncAdapterFactory);
	}

	@Override
	public void removeSourceId(String sourceId) {
		this.sourceIdMapper.removeSourceDefinition(sourceId);
		
	}

}
