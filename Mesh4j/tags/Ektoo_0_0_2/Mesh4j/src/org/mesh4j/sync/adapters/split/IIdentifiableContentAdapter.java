package org.mesh4j.sync.adapters.split;

import org.mesh4j.sync.model.IContent;

public interface IIdentifiableContentAdapter extends IContentAdapter {

	String getID(IContent content);
}
