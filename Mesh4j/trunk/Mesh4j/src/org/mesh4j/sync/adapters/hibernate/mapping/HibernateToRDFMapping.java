package org.mesh4j.sync.adapters.hibernate.mapping;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class HibernateToRDFMapping implements IHibernateToXMLMapping {

	// MODEL VARIABLES
	private IRDFSchema schema;
	private String entityNode;
	private String idNode;
	
	// BUSINESS METHODS
	public HibernateToRDFMapping(IRDFSchema schema, String entityNode, String idNode){
		Guard.argumentNotNull(schema, "schema");		
		this.schema = schema;
		
		this.initialize(entityNode, idNode);
	}
	
	@Override
	public Element convertRowToXML(String id, Element element) throws Exception {
		RDFInstance instance = this.schema.createNewInstance("uri:urn:" + id, element.asXML(), this.idNode);
		String rdfXml = instance.asXML();
		return XMLHelper.parseElement(rdfXml);
	}

	@Override
	public Element convertXMLToRow(String id, Element element) throws Exception {
		RDFInstance instance = this.schema.createNewInstance("uri:urn:" + id, element.asXML());
		String xml = instance.asPlainXML(this.idNode);
		return XMLHelper.parseElement(xml);
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
