package org.mesh4j.sync.message.core;

import java.util.List;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.ISyncSession;


public interface IMessageProcessor {

	List<IMessage> process(ISyncSession syncSession, IMessage message);

	String getMessageType();

}
