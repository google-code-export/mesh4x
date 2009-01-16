package org.mesh4j.sync.message.core.repository;

import java.util.ArrayList;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.core.InMemoryMessageSyncAdapter;
import org.mesh4j.sync.message.core.MessageSyncAdapter;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

public class MessageSyncAdapterFactory implements IMessageSyncAdapterFactory {

	// MODEL VARIABLES
	private boolean supportInMemoryAdapter = false;
	private IOpaqueSyncAdapterFactory defaultSyncAdapterFactory;
	private ArrayList<ISyncAdapterFactory> syncAdapterFactories = new ArrayList<ISyncAdapterFactory>();
	
	// BUSINESS METHODS
	
	public MessageSyncAdapterFactory(IOpaqueSyncAdapterFactory defaultSyncAdapterFactory, boolean supportInMemoryAdapter, ISyncAdapterFactory ... allSyncAdapterFactories) {
		this.supportInMemoryAdapter = supportInMemoryAdapter;
		this.defaultSyncAdapterFactory = defaultSyncAdapterFactory;
		for (ISyncAdapterFactory syncAdapterFactory : allSyncAdapterFactories) {
			this.registerSyncAdapterFactory(syncAdapterFactory);
		}
	}

	@Override
	public IMessageSyncAdapter createSyncAdapter(String sourceId, IIdentityProvider identityProvider) {
		try{
			IMessageSyncAdapter msgSyncAdapter = createMessageSyncAdapter(sourceId, identityProvider);
			if(msgSyncAdapter == null && this.supportInMemoryAdapter){
				msgSyncAdapter = new InMemoryMessageSyncAdapter(sourceId);
			}
			return msgSyncAdapter;
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	private IMessageSyncAdapter createMessageSyncAdapter(String sourceId, IIdentityProvider identityProvider) throws Exception {
		IMessageSyncAdapter msgSyncAdapter = null;
		for (ISyncAdapterFactory syncAdapterFactory : this.syncAdapterFactories) {
			if(syncAdapterFactory.acceptsSourceId(sourceId)){
				ISyncAdapter syncAdapter = syncAdapterFactory.createSyncAdapter(sourceId, identityProvider);
				msgSyncAdapter = new MessageSyncAdapter(sourceId, syncAdapterFactory.getSourceType(), identityProvider, syncAdapter);
			}
		}
		if(msgSyncAdapter == null && this.defaultSyncAdapterFactory != null){
			String sourceIdForDefaultSyncAdapter = this.defaultSyncAdapterFactory.createSourceId(sourceId);
			ISyncAdapter syncAdapter = this.defaultSyncAdapterFactory.createSyncAdapter(sourceIdForDefaultSyncAdapter, identityProvider);
			msgSyncAdapter = new MessageSyncAdapter(sourceId, this.defaultSyncAdapterFactory.getSourceType(), identityProvider, syncAdapter);
		}
		return msgSyncAdapter;
	}

	public void registerSyncAdapterFactory(ISyncAdapterFactory syncAdapterFactory) {
		this.syncAdapterFactories.add(syncAdapterFactory);
	}

}
