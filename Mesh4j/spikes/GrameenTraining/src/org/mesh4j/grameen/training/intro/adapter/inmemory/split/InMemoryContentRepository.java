package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.hibernate.EntityContent;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;

public class InMemoryContentRepository implements IContentAdapter , ISyncAware{

	private Storage storage = null;
	private String repositoryType = "";
	private String entityName = "";
	private String entityIdColumnName = "";
	
	
	public InMemoryContentRepository(Storage storage,String repositoryType,
			String entityName,String entityIdColumnName){
			this.storage = storage;
			this.repositoryType = repositoryType;
			this.entityName = entityName;
			this.entityIdColumnName = entityIdColumnName;
	}
	
	@Override
	public void delete(IContent content) {
		this.storage.deletRow(content);
	}

	@Override
	public IContent get(String contentId) {
		return this.storage.getRow(contentId);
	}

	@Override
	public List<IContent> getAll(Date since) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		return this.repositoryType;
	}

	@Override
	public IContent normalize(IContent content) {
		return EntityContent.normalizeContent(content, this.entityName, entityIdColumnName);
	}

	@Override
	public void save(IContent content) {
		//it will be new record or update the old one
		EntityContent entityContent = EntityContent.normalizeContent(content, this.entityName, this.entityIdColumnName);
		//we will decide if it is new row or old one.
		if(this.storage.getRow(entityContent.getId()) == null){
			storage.addRow(entityContent);
		}else{
			storage.updateRow(entityContent);
		}
	}

	@Override
	public void beginSync() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endSync() {
		// TODO Auto-generated method stub
		
	}

}
