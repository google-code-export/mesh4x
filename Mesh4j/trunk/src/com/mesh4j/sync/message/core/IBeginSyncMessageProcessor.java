package com.mesh4j.sync.message.core;

import com.mesh4j.sync.message.IEndpoint;
import com.mesh4j.sync.message.IMessage;
import com.mesh4j.sync.message.ISyncSession;
import com.mesh4j.sync.message.ISyncSessionFactory;

public interface IBeginSyncMessageProcessor extends IMessageProcessor {

	IMessage createMessage(ISyncSession syncSession);

	ISyncSession createSession(ISyncSessionFactory syncSessionFactory, IMessage message);

	ISyncSession createSession(ISyncSessionFactory syncSessionFactory, String sourceId, IEndpoint target);

}
