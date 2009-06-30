package org.mesh4j.sync.adapters.hibernate.mapping;

import java.io.Serializable;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IHibernateToXMLMapping extends IIdentifiableMapping{

	Element convertRowToXML(String id, Element element) throws Exception;

	Element convertXMLToRow(Element element) throws Exception;
	
	ISchema getSchema();

	String getMeshId(Element entityElement);

	Serializable getHibernateId(String meshId);
}
