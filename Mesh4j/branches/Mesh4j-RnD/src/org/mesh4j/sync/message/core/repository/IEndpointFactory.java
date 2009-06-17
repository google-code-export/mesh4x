package org.mesh4j.sync.message.core.repository;

import org.mesh4j.sync.message.IEndpoint;

public interface IEndpointFactory {
	public IEndpoint makeIEndpoint(String endpoint);	
}
