package org.mesh4j.sync.adapters.rms.storage;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;

public class RmsStorageContentAdapter implements IContentAdapter{

	// MODEL VARIABLES
	private IRmsStorage storage;
	private ContentObjectParser parser;
	
	// BUSINESS METHODS
	public RmsStorageContentAdapter(String storageName) {
		Guard.argumentNotNullOrEmptyString(storageName, "storageName");
		
		this.parser = new ContentObjectParser(storageName);
		this.storage = new RmsStorage(parser, storageName);
	}

	public void delete(IContent entity) {
		ContentIdRecordFilter filter = new ContentIdRecordFilter(this.parser, entity.getId());
		this.storage.delete(filter);
	}

	public IContent get(String entityId) {
		ContentIdRecordFilter filter = new ContentIdRecordFilter(this.parser, entityId);
		return (IContent) this.storage.get(filter);
	}

	public Vector<IContent> getAll(Date since) {
		ContentSinceDateRecordFilter filter = new ContentSinceDateRecordFilter(this.parser, since);
		return this.storage.getAll(filter, null);
	}
	
	public void save(IContent entity) {
		ContentIdRecordFilter filter = new ContentIdRecordFilter(this.parser, entity.getId());
		this.storage.saveOrUpdate(entity, filter);
	}

	public String getType() {
		return this.storage.getStorageName();
	}

	public void deleteRecordStorage() {
		this.storage.deleteRecordStorage();		
	}

	public void deleteAll() {
		this.storage.deleteRecordStorage();		
	}

	public IContent normalize(IContent content) {
		return content;
	}
}
