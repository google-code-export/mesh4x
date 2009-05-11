package org.mesh4j.sync.adapters.split;

import java.util.Date;
import java.util.List;

import org.mesh4j.sync.model.IContent;


public interface IContentAdapter {

	void save(IContent content);

	IContent get(String contentId);

	void delete(IContent content);

	List<IContent> getAll(Date since);

	String getType();

}
