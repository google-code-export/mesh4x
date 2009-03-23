package org.mesh4j.sync.adapters.split;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.mesh4j.sync.model.IContent;

public class MockContentAdapter implements IContentAdapter {

	// MODEL VARIABLE
	private HashMap<String, IContent> contents = new HashMap<String, IContent>();
	
	// BUSINESS METHODS
	
	@Override
	public void delete(IContent content) {
		this.contents.remove(content.getId());
	}

	@Override
	public IContent get(String contentId) {
		return this.contents.get(contentId);
	}

	@Override
	public List<IContent> getAll(Date since) {
		return new ArrayList<IContent>(this.contents.values());
	}

	@Override
	public String getType() {
		return "MOCK";
	}

	@Override
	public void save(IContent content) {
		this.contents.put(content.getId(), content);
	}
}
