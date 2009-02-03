package org.mesh4j.sync.payload.mappings;

import org.dom4j.Element;

public interface IPropertyResolver {

	boolean accepts(String variableTemplate);

	String getPropertyValue(Element element, String variableTemplate);

}
