package org.mesh4j.sync.message.core;

import org.mesh4j.sync.message.encoding.IMessageEncoding;

public class NonMessageEncoding implements IMessageEncoding{

	public static final NonMessageEncoding INSTANCE = new NonMessageEncoding();

	public String decode(String message) {
		return message;
	}

	public String encode(String message) {
		return message;
	}

	public boolean isBynary() {
		return false;
	}

}
