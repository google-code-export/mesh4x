package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface IMessageSyncProtocol {

	public static final List<IMessage> NO_RESPONSE = new ArrayList<IMessage>();
		
	boolean isValidMessageProtocol(IMessage message);

	IMessage beginSync(String sourceId, IEndpoint endpoint, boolean fullProtocol);
	
	List<IMessage> processMessage(IMessage message);

	IMessage cancelSync(String sourceId, IEndpoint target);

	ISyncSession getSyncSession(String sourceId, IEndpoint target);

	void registerSourceIfAbsent(IMessageSyncAdapter adapter);

	void endSync(ISyncSession syncSession, Date date);

	void notifyBeginSync(ISyncSession syncSession);


}
