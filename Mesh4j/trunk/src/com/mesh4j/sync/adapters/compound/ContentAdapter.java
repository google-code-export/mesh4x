package com.mesh4j.sync.adapters.compound;

import java.util.List;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.model.Content;

public interface ContentAdapter {

	EntityContent normalizeContent(Content content);

	void save(EntityContent entity);

	EntityContent get(String entityId);

	void delete(EntityContent entity);

	List<EntityContent> getAll();

	String getEntityName();

}
