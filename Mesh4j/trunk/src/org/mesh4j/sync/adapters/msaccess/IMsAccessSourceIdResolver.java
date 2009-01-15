package org.mesh4j.sync.adapters.msaccess;

public interface IMsAccessSourceIdResolver {

	String getFileName(String sourceId);

	String getTableName(String sourceId);

	String getSourceName(String sourceId);

}
