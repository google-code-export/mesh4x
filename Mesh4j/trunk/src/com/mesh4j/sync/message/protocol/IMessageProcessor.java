package com.mesh4j.sync.message.protocol;

import java.util.List;


public interface IMessageProcessor {

	List<String> process(String message);

	String getMessageType();

}
