package org.mesh4j.sync.adapters.hibernate.mapping;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IHibernateToXMLMapping {

	String getEntityNode();

	String getIDNode();

	Element convertRowToXML(String id, Element element) throws Exception;

	Element convertXMLToRow(Element element) throws Exception;
	
	ISchema getSchema();
}
