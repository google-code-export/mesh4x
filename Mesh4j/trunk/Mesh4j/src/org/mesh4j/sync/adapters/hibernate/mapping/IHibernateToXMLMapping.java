package org.mesh4j.sync.adapters.hibernate.mapping;

import org.dom4j.Element;

public interface IHibernateToXMLMapping {

	String getEntityNode();

	String getIDNode();

	Element convertRowToXML(String id, Element element) throws Exception;

	Element convertXMLToRow(Element element) throws Exception;

}
