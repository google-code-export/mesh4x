package org.mesh4j.sync.message.core;

import org.mesh4j.sync.message.IMessage;
import org.mesh4j.sync.message.ISyncSession;

public interface ICancelSyncMessageProcessor extends IMessageProcessor {

	IMessage createMessage(ISyncSession syncSession);

}
