package org.mesh4j.sync.id.generator;

import java.util.Random;

public class IdGenerator implements IIdGenerator {

	public final static IdGenerator INSTANCE = new IdGenerator();
	
	// MODEL VARIABLES
	private String LAST_ID = "";	
	private Random ID_GENERATOR = new Random();

	// BUSINESS METHODS
	public synchronized String newID() {
		String currentID = randomString();
		while (LAST_ID == currentID) {
			currentID = randomString();
		}
		LAST_ID = currentID;
		return String.valueOf(LAST_ID);
	}

	private String randomString(){
		StringBuffer sb = new StringBuffer();
		sb.append(random());
		sb.append("-");
		sb.append(random());
		return sb.toString().substring(0, 36);
	}
	
	private long random() {
		long i = ID_GENERATOR.nextLong();
		if (i < 0) {
			i = i * -1;
		}
		return i;
	}
	
}
