package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.validations.Guard;

public class InMemoryContentRepository implements IContentAdapter  {

	// MODEL VARIABLES
	private Storage storage = null;
	private String repositoryType = "";
	private String entityName = "";
	
	// BUSINESS METHODS
	public InMemoryContentRepository(Storage storage,String repositoryType,String entityName){
			
			Guard.argumentNotNull(storage, "storage");
			Guard.argumentNotNullOrEmptyString(repositoryType, "repositoryType");
			Guard.argumentNotNullOrEmptyString(entityName, "entityName");
			
			this.storage = storage;
			this.repositoryType = repositoryType;
			this.entityName = entityName;
	}
	
	@Override
	public void delete(IContent content) {
		Guard.argumentNotNull(content, "content");	
		this.storage.deletRow(content);
	}

	@Override
	public IContent get(String contentId) {
		Guard.argumentNotNullOrEmptyString(contentId, "contentId");
		return (IContent)this.storage.getRow(contentId);
	}

	@Override
	public List<IContent> getAll(Date since) {
		//Guard.argumentNotNull(since, "since");
		List<IContent> allContents = new LinkedList<IContent>();
		Collection<Object> list = this.storage.getStorage().values();
		for(Object cont : list){
			allContents.add((IContent)cont);
		}
		return allContents;
	}

	@Override
	public String getType() {
		return this.repositoryType;
	}

	@Override
	public void save(IContent content) {
		Guard.argumentNotNull(content, "content");
		storage.addRow(content);
	}

	public String getEntityName(){
		return this.entityName;
	}

}
