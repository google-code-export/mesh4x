package com.mesh4j.sync.message.core;

import com.mesh4j.sync.message.encoding.IMessageEncoding;

public class NonMessageEncoding implements IMessageEncoding{

	public static final NonMessageEncoding INSTANCE = new NonMessageEncoding();

	@Override
	public String decode(String message) {
		return message;
	}

	@Override
	public String encode(String message) {
		return message;
	}

}
