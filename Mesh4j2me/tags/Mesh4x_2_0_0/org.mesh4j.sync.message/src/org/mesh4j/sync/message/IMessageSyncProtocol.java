package org.mesh4j.sync.message;

import java.util.Date;
import java.util.Vector;

import org.mesh4j.sync.message.core.IBeginSyncMessageProcessor;

public interface IMessageSyncProtocol {

	public static final Vector<IMessage> NO_RESPONSE = new Vector<IMessage>();
		
	boolean isValidMessageProtocol(IMessage message);

	Vector<IMessage> processMessage(IMessage message);

	ISyncSession getSyncSession(String sourceId, IEndpoint target);

	void registerSourceIfAbsent(IMessageSyncAdapter adapter);
	void registerSyncAware(IMessageSyncAware syncAware);
	void cleanAllSessions();

	IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol);
	void beginSync(ISyncSession syncSession, Date sinceDate, int sessionVersion);
	void notifyBeginSync(ISyncSession syncSession);
	IBeginSyncMessageProcessor getBeginMessageProcessor();
	
	void endSync(ISyncSession syncSession, Date date);

	IMessage cancelSync(String sourceId, IEndpoint target);
	void cancelSync(ISyncSession syncSession);
}
