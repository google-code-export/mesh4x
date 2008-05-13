package com.mesh4j.sync.adapters.compound;

import java.util.List;

import com.mesh4j.sync.adapters.IIdentifiableContent;
import com.mesh4j.sync.model.IContent;

public interface IContentAdapter {

	IIdentifiableContent normalizeContent(IContent content);

	void save(IIdentifiableContent entity);

	IIdentifiableContent get(String entityId);

	void delete(IIdentifiableContent entity);

	List<IIdentifiableContent> getAll();

	String getType();

}
