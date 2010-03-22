package org.mesh4j.sync.adapters.feed.pfif.mapping;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.AbstractPlainXmlIdentifiableMapping;

public class PfifToPlainXmlMapping extends AbstractPlainXmlIdentifiableMapping implements IPfifToPlainXmlMapping{
	
 
	
	public PfifToPlainXmlMapping(String type, String idColumnName,
			String lastUpdateColumnName, String lastUpdateColumnDateTimeFormat) {
		super(type, idColumnName, lastUpdateColumnName, lastUpdateColumnDateTimeFormat);
	}

	@Override
	public Element convertPfifToXML(Element pfifPayload) {
		return pfifPayload;
	}

	@Override
	public Element convertXMLToPfif(Element xmlPayload) {
		return xmlPayload;
	}


}
