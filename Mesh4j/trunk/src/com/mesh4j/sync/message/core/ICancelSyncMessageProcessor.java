package com.mesh4j.sync.message.core;

import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.ISyncSession;

public interface ICancelSyncMessageProcessor extends IMessageProcessor {

	IMessage createMessage(ISyncSession syncSession);

}
