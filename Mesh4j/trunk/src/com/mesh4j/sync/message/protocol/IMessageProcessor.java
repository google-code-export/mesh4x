package com.mesh4j.sync.message.protocol;

import java.util.List;

import com.mesh4j.sync.message.IMessage;


public interface IMessageProcessor {

	List<IMessage> process(IMessage message);

	String getMessageType();

}
