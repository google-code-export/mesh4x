package com.mesh4j.sync.message;

import com.mesh4j.sync.message.encoding.IMessageEncoding;

public class MockMessageEncoding implements IMessageEncoding{

	public static final MockMessageEncoding INSTANCE = new MockMessageEncoding();

	@Override
	public String decode(String message) {
		return message;
	}

	@Override
	public String encode(String message) {
		return message;
	}

	@Override
	public boolean isBynary() {
		return false;
	}

}
