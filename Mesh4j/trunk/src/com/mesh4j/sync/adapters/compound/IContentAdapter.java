package com.mesh4j.sync.adapters.compound;

import java.util.Date;
import java.util.List;

import com.mesh4j.sync.model.IContent;

public interface IContentAdapter {

	void save(IContent entity);

	IContent get(String entityId);

	void delete(IContent entity);

	List<IContent> getAll(Date since);

	String getType();

}
