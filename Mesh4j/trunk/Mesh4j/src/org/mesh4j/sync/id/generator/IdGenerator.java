package org.mesh4j.sync.id.generator;

import java.util.UUID;

public class IdGenerator implements IIdGenerator {

	public final static IdGenerator INSTANCE = new IdGenerator();
	
	public String newID() {
		return UUID.randomUUID().toString();
	}

}
