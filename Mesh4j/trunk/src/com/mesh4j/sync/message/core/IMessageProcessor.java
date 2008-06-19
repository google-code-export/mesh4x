package com.mesh4j.sync.message.core;

import java.util.List;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.ISyncSession;

public interface IMessageProcessor {

	List<IMessage> process(ISyncSession syncSession, IMessage message);

	String getMessageType();

}
