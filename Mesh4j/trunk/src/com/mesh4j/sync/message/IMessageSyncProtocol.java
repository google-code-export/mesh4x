package com.mesh4j.sync.message;

import java.util.ArrayList;
import java.util.List;

public interface IMessageSyncProtocol {

	public static final List<IMessage> NO_RESPONSE = new ArrayList<IMessage>();
	
	IMessage beginSync(ISyncSession syncSession);

	List<IMessage> processMessage(ISyncSession syncSession, IMessage message);

	 boolean isValidMessageProtocol(IMessage message);

	IMessage cancelSync(ISyncSession syncSession);
}
