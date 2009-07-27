package org.mesh4j.sync.message.core;

import java.util.Vector;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.ISyncSession;

public interface IMessageProcessor {

	Vector<IMessage> process(ISyncSession syncSession, IMessage message);

	String getMessageType();

}
