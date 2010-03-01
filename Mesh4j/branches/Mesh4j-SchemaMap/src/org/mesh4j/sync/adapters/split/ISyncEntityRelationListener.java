package org.mesh4j.sync.adapters.split;

import org.mesh4j.sync.model.IContent;

public interface ISyncEntityRelationListener {

	void notifyNewSyncForContent(String syncId, IContent content);

	void notifyRemoveSync(String syncId);

}
