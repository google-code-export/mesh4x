package org.mesh4j.sync.adapters.rms.storage;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.adapters.split.ISyncEntityRelationListener;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

//@deprecated
public class RmsStorageContentWithSyncAdapter implements IContentAdapter, ISyncEntityRelationListener {

	// MODEL VARIABLES
	private RmsStorageSyncIDRmsRecordRelation rmsStorageSyncIDRmsRecordRelation;
	private IRmsStorage storage;
	
	// BUSINESS METHODS
	public RmsStorageContentWithSyncAdapter(IRmsStorage storage) {
		Guard.argumentNotNull(storage, "storage");
		this.storage = storage;
		this.rmsStorageSyncIDRmsRecordRelation = new RmsStorageSyncIDRmsRecordRelation(storage.getStorageName()+"_sync");
		throw new MeshException("DEPRECATED CLASS");
	}

	public void delete(IContent entity) {
		try{
			int instanceID = this.getAssociationRMS().getInstanceID(entity.getId());
			IRmsStorage storage = this.getContentStorage();
			storage.deleteRecord(instanceID);
			this.getAssociationRMS().deleteAssociation(entity.getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public IContent get(String syncId) {
		try {
			int instanceID = this.getAssociationRMS().getInstanceID(syncId);
			if(instanceID == -1){
				return null;
			}
			
			IRmsStorage storage = this.getContentStorage();

			String xml = (String)storage.get(instanceID);
			
			return new RmsRecordContent(xml, syncId, instanceID, this.getType());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public Vector<IContent> getAll(Date since) {
		Vector<IContent> result = new Vector<IContent>();
		IRmsStorage storage = this.getContentStorage();
		int size = storage.getNumberOfRecords();
		int instanceId = 1;
		
		while (instanceId <= size) {
			try {
				String xml = (String)storage.get(instanceId);
				
				String syncID = this.getAssociationRMS().getSyncID(instanceId);		// Can be null
				result.addElement(new RmsRecordContent(xml, syncID, instanceId, this.getType()));
			} catch (Exception e) {
				e.printStackTrace();
				throw new MeshException(e.getMessage());
			}
			instanceId = instanceId + 1;
		}
		return result;
	}

	public String getType() {
		return this.storage.getStorageName();
	}

	public IContent normalize(IContent content) {
		try{
			int recordId = this.getAssociationRMS().getInstanceID(content.getId());
			return new RmsRecordContent(content.getPayload(), content.getId(), recordId, this.getType());
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public void save(IContent entity) {

		try {
			int instanceID = this.getAssociationRMS().getInstanceID(entity.getId());

			IRmsStorage storage = this.getContentStorage();

			if (instanceID == -1) {
				instanceID = storage.save(entity.getPayload());
				this.getAssociationRMS().addAssociation(entity.getId(), instanceID);
			} else {
				storage.update(instanceID, entity.getPayload());
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	public void notifyNewSyncForContent(String syncId, IContent content) {
		RmsRecordContent recordContent = (RmsRecordContent) content;
		recordContent.setSyncId(syncId);
		if(recordContent.getRecordId() != -1){
			try{
				this.getAssociationRMS().addAssociation(syncId, recordContent.getRecordId());
			} catch (Exception e) {
				e.printStackTrace();
				throw new MeshException(e.getMessage());
			}
		}		
	}

	private RmsStorageSyncIDRmsRecordRelation getAssociationRMS() {
		return rmsStorageSyncIDRmsRecordRelation;
	}

	public void notifyRemoveSync(String syncId) {
		try{
			this.getAssociationRMS().deleteAssociation(syncId);
		} catch (Exception e) {
			e.printStackTrace();
			throw new MeshException(e.getMessage());
		}
	}

	private IRmsStorage getContentStorage() {
		return this.storage;
	}

	public void deleteRecordStorage() {
		this.storage.deleteRecordStorage();
		this.rmsStorageSyncIDRmsRecordRelation.deleteRecordStorage();
	}

	public void deleteAll() {
		this.storage.deleteAll();
		this.rmsStorageSyncIDRmsRecordRelation.deleteAll();
	}
}
