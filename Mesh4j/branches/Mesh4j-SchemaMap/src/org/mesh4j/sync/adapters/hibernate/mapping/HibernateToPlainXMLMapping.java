package org.mesh4j.sync.adapters.hibernate.mapping;

import java.io.Serializable;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.AbstractPlainXmlIdentifiableMapping;

public class HibernateToPlainXMLMapping extends AbstractPlainXmlIdentifiableMapping implements IHibernateToXMLMapping {

	// BUSINESS METHODS
	public HibernateToPlainXMLMapping(String entityNode, String idNode){
		super(entityNode, idNode, null, null);
	}
	
	@Override
	public Element convertRowToXML(String id, Element element) {
		return element;
	}

	@Override
	public Element convertXMLToRow(Element element) {
		return element;
	}

	@Override
	public String getMeshId(Element entityElement) {
		if(entityElement == null){
			return null;
		}

		Element idElement = entityElement.element(idColumnName);
		if(idElement == null){
			return null;
		}
		return idElement.getText();
	}

	@Override
	public Serializable getHibernateId(String meshId) {
		return meshId;
	}

}
