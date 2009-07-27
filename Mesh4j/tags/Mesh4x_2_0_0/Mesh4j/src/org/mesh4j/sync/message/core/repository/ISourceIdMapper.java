package org.mesh4j.sync.message.core.repository;

public interface ISourceIdMapper {

	String getSourceDefinition(String sourceId);

	void removeSourceDefinition(String sourceId);

}
