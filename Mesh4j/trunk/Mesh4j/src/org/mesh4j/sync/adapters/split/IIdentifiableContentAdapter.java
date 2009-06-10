package org.mesh4j.sync.adapters.split;

import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IIdentifiableContentAdapter extends IContentAdapter {

	String getID(IContent content);
	
	String getIdNode();
	
	ISchema getSchema();
}
