package org.mesh4j.sync.adapters.feed.pfif.mapping;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IPfifToPlainXmlMapping extends IIdentifiableMapping {
	
	public Element convertPfifToXML(Element pfifPayload);
	public Element convertXMLToPfif(Element xmlPayload);
	public ISchema getSchema();
	
}
