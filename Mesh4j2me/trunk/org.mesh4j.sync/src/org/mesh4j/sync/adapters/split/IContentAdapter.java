package org.mesh4j.sync.adapters.split;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.model.IContent;


public interface IContentAdapter {

	void save(IContent entity);

	IContent get(String entityId);

	void delete(IContent entity);

	Vector<IContent> getAll(Date since);

	String getType();

	IContent normalize(IContent content);

	void deleteAll();

}
