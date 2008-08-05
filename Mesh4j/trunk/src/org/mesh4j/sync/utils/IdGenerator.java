package org.mesh4j.sync.utils;

import java.util.UUID;

public class IdGenerator {

	public static String newID(){
		return UUID.randomUUID().toString();
	}
}
