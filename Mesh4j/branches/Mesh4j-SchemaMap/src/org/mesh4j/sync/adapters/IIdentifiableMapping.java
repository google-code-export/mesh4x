package org.mesh4j.sync.adapters;

import org.dom4j.Element;

public interface IIdentifiableMapping {

	public static final String ID_SEPARATOR = ",";

	String getType();

	String getId(Element payload);

	Element getTypeElement(Element payload);

}
