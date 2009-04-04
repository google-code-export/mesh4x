package org.mesh4j.sync.adapters.hibernate.mapping;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.payload.schema.SchemaTypeFormat;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class HibernateToRDFMapping implements IHibernateToXMLMapping {
	
	private static final String DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";
	private static HashMap<String, ISchemaTypeFormat> FORMATS = new HashMap<String, ISchemaTypeFormat>();
	
	static{
		FORMATS.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat(DATE_FORMAT)));
	}
	
	// MODEL VARIABLES
	private IRDFSchema schema;
	private String entityNode;
	private String idNode;
	
	// BUSINESS METHODS
	public HibernateToRDFMapping(IRDFSchema schema, String entityNode, String idNode){
		Guard.argumentNotNull(schema, "schema");		
		Guard.argumentNotNullOrEmptyString(entityNode, "entityNode");
		Guard.argumentNotNullOrEmptyString(idNode, "idNode");

		this.schema = schema;
		this.entityNode = entityNode;
		this.idNode = idNode;
	}
	
	@Override
	public Element convertRowToXML(String id, Element element) throws Exception {
		RDFInstance instance = this.schema.createNewInstanceFromPlainXML(id, element.asXML(), FORMATS);
		String rdfXml = instance.asXML();
		return XMLHelper.parseElement(rdfXml);
	}

	@Override
	public Element convertXMLToRow(Element element) throws Exception {
		String rdfXml;
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(element.getName())){
			Element rdfElement = element.element("RDF");
			if(rdfElement == null){
				Guard.throwsArgumentException("payload");
			}
			rdfXml = rdfElement.asXML();
		} else {
			rdfXml = element.asXML();
		}
		RDFInstance instance = this.schema.createNewInstanceFromRDFXML(rdfXml);
		String xml = instance.asPlainXML(FORMATS);
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

	public IRDFSchema getRDFSchema() {
		return this.schema;
	}
}
