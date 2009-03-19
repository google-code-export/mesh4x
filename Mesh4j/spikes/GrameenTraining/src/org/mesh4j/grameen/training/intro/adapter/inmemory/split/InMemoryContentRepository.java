package org.mesh4j.grameen.training.intro.adapter.inmemory.split;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mesh4j.sync.ISyncAware;
import org.mesh4j.sync.adapters.split.IContentAdapter;
import org.mesh4j.sync.model.IContent;

public class InMemoryContentRepository implements IContentAdapter , ISyncAware{

	private Map<String,IContent> contents = new HashMap<String, IContent>();
	private String repositoryType = "";
	
	
	public InMemoryContentRepository(Map<String,IContent> contents,String repositoryType){
			this.contents = contents;
			this.repositoryType = repositoryType;
	}
	
	@Override
	public void delete(IContent content) {
		contents.remove(content.getId());
	}

	@Override
	public IContent get(String contentId) {
		return contents.get(contentId);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void save(IContent content) {
		contents.put(content.getId(), content);
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
