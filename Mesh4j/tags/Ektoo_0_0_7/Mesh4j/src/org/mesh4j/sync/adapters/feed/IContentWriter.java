package org.mesh4j.sync.adapters.feed;

import org.dom4j.Element;
import org.mesh4j.sync.model.Item;

public interface IContentWriter {

	void writeContent(ISyndicationFormat syndicationFormat, Element itemElement, Item item);

	boolean mustWriteSync(Item item);

}
