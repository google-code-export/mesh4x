package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;

public class InMemoryContentRepository implements IContentAdapter , ISyncAware{

	private Storage storage = null;
	private String repositoryType = "";
	private String entityName = "";
	
	public InMemoryContentRepository(Storage storage,String repositoryType,
			String entityName,String entityIdColumnName){
			this.storage = storage;
			this.repositoryType = repositoryType;
			this.entityName = entityName;
	}
	
	@Override
	public void delete(IContent content) {
		this.storage.deletRow(content);
	}

	@Override
	public IContent get(String contentId) {
		return (IContent)this.storage.getRow(contentId);
	}

	@Override
	public List<IContent> getAll(Date since) {
		List<IContent> allContents = new LinkedList<IContent>();
		List<Object> list = (List<Object>) this.storage.getStorage().values();
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
	public IContent normalize(IContent content) {
		return content;
	}

	@Override
	public void save(IContent content) {
		//we will decide if it is new row or old one.
		if(this.storage.getRow(content.getId()) == null){
			storage.addRow(content);
		}else{
			storage.updateRow(content);
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
