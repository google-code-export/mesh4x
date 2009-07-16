package org.mesh4j.sync.adapters.feed;

import org.dom4j.Element;

public interface IContentReader {

	void readContent(String syncId, Element payload, Element contentElement);

}
