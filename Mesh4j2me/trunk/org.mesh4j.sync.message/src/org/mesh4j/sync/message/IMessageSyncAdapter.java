package org.mesh4j.sync.message;

import java.util.Vector;

import org.mesh4j.sync.model.Item;

public interface IMessageSyncAdapter{

	String getSourceId();

	Vector<Item> getAll();
	
	Vector<Item> synchronizeSnapshot(ISyncSession syncSession);
}
