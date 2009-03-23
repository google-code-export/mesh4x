package org.mesh4j.sync.adapters.hibernate.mapping;

import org.dom4j.Element;
import org.mesh4j.sync.validations.Guard;

public class HibernateToPlainXMLMapping implements IHibernateToXMLMapping {

	// MODEL VARIABLES
	private String entityNode;
	private String idNode;
	
	// BUSINESS METHODS
	public HibernateToPlainXMLMapping(String entityNode, String idNode){
		super();
		this.initialize(entityNode, idNode);
	}
	
	@Override
	public Element convertRowToXML(String id, Element element) {
		return element;
	}

	@Override
	public Element convertXMLToRow(String id, Element element) {
		return element;
	}

	@Override
	public String getEntityNode() {
		return this.entityNode;
	}

	@Override
	public String getIDNode() {
		return this.idNode;
	}

	private void initialize(String entityNode, String idNode) {
		Guard.argumentNotNullOrEmptyString(entityNode, "entityNode");
		Guard.argumentNotNullOrEmptyString(idNode, "idNode");
		
		this.entityNode = entityNode;
		this.idNode = idNode;
	}

}
