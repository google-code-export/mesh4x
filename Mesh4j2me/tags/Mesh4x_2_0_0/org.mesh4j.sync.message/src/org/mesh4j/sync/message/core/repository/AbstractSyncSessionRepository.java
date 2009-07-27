package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.message.IEndpoint;
import org.mesh4j.sync.message.IMessageSyncAdapter;
import org.mesh4j.sync.message.core.ISyncSessionRepository;
import org.mesh4j.sync.validations.Guard;

import de.enough.polish.util.HashMap;

public abstract class AbstractSyncSessionRepository implements ISyncSessionRepository {

	// MODEL VARIANBLES
	private HashMap adapters = new HashMap();
	private IEndpointFactory endpointFactory;
	private IMessageSyncAdapterFactory adapterFactory;
	
	// BUSINESS METHODS
	public AbstractSyncSessionRepository(IEndpointFactory endpointFactory, IMessageSyncAdapterFactory adapterFactory) {
		Guard.argumentNotNull(adapterFactory, "adapterFactory");
		Guard.argumentNotNull(endpointFactory, "endpointFactory");
		
		this.adapterFactory = adapterFactory;
		this.endpointFactory = endpointFactory;
	}

	public IMessageSyncAdapter getSource(String sourceId) {
		IMessageSyncAdapter syncAdapter = (IMessageSyncAdapter) this.adapters.get(sourceId);
		if(syncAdapter != null){
			return syncAdapter;
		}
		
		syncAdapter = this.adapterFactory.createSyncAdapter(sourceId);
		if(syncAdapter != null){
			this.adapters.put(sourceId, syncAdapter);
			return syncAdapter;
		} else {
			return null;
		}

	}	

	public void registerSource(IMessageSyncAdapter source) {
		this.adapters.put(source.getSourceId(), source);
	}
	
	public void registerSourceIfAbsent(IMessageSyncAdapter source) {
		if(this.adapters.get(source.getSourceId()) == null){
			this.adapters.put(source.getSourceId(), source);
		}
	}	
	
	public IEndpoint getEndpoint(String endpointId) {
		return this.endpointFactory.makeIEndpoint(endpointId);
	}
	
	public String normalizeEndpointId(String endpointId){
		// endpointId = "sms://+541234567777:3333"
		return endpointId.substring(6, endpointId.length() - 5).replace('+', ' ').trim();
	}
	
	public void deleteAll(){
		this.adapters = new HashMap();
	}
}