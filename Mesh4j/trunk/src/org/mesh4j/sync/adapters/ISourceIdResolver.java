package org.mesh4j.sync.adapters;

public interface ISourceIdResolver {

	public String getSource(String sourceId);
	
	public void putSource(String sourceId, String source);
	
}
