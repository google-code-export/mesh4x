package org.mesh4j.sync.adapters.hibernate.mapping;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.payload.schema.ISchemaTypeFormat;
import org.mesh4j.sync.payload.schema.SchemaTypeFormat;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.CompositeProperty;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

public class HibernateToRDFMapping extends AbstractRDFIdentifiableMapping implements IHibernateToXMLMapping {
	
	private static final String DATE_FORMAT = "yyyy-mm-dd hh:mm:ss";
	private static HashMap<String, ISchemaTypeFormat> FORMATS = new HashMap<String, ISchemaTypeFormat>();
	
	static{
		FORMATS.put(IRDFSchema.XLS_DATETIME, new SchemaTypeFormat(new SimpleDateFormat(DATE_FORMAT)));
	}
	
	// BUSINESS METHODS
	public HibernateToRDFMapping(IRDFSchema rdfSchema){
		super(rdfSchema);
	}
	
	@Override
	public Element convertRowToXML(String meshId, Element element) throws Exception {
		RDFInstance instance = null;
		if(this.rdfSchema.hasCompositeId()){
			instance = this.rdfSchema.createNewInstanceFromPlainXML(meshId, element.asXML(), FORMATS, new String[]{"id"}); 
		} else {
			instance = this.rdfSchema.createNewInstanceFromPlainXML(meshId, element.asXML(), FORMATS); 
		}
		return instance.asElementXML();
	}

	@Override
	public Element convertXMLToRow(Element element) throws Exception {
		String rdfXml;
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(element.getName())){
			Element rdfElement = element.element(IRDFSchema.ELEMENT_RDF);
			if(rdfElement == null){
				Guard.throwsArgumentException("payload");
			}
			rdfXml = rdfElement.asXML();
		} else {
			rdfXml = element.asXML();
		}
		RDFInstance instance = this.rdfSchema.createNewInstanceFromRDFXML(rdfXml);
		
		String xml = null;
		if(this.rdfSchema.hasCompositeId()){
			CompositeProperty compositeId = new CompositeProperty("id", this.rdfSchema.getIdentifiablePropertyNames());			
			xml = instance.asPlainXML(FORMATS, new CompositeProperty[]{compositeId});
		} else {
			xml = instance.asPlainXML(FORMATS);
		}
		return XMLHelper.parseElement(xml);
	}

	@Override
	public String getMeshId(Element entityElement) {
		if(entityElement == null){
			return null;
		}

		Element element = entityElement;
		if(this.rdfSchema.hasCompositeId()){
			element = entityElement.element("id");
		}
		
		List<String> idValues = new ArrayList<String>();
		String idCellValue;
		List<String> idColumnNames = this.rdfSchema.getIdentifiablePropertyNames();
		for (String idColumnName : idColumnNames) {
			Element idElement = element.element(idColumnName);
			if(idElement == null){
				return null;
			}
			idCellValue = idElement.getText();
			if(idCellValue == null){
				return null;
			} else {
				idValues.add(idCellValue);
			}
		}
		return makeId(idValues);	
	}

	@Override
	public Serializable getHibernateId(String meshId) {
		if(this.rdfSchema.hasCompositeId()){
			String[] meshIds = getIds(meshId);
			List<String> propertyNames = this.rdfSchema.getIdentifiablePropertyNames();
			
			StringBuffer sb = new StringBuffer();
			sb.append("<id>");
			for (int i = 0; i < propertyNames.size(); i++) {
				String propertyName = propertyNames.get(i);
				sb.append("<");
				sb.append(propertyName);
				sb.append(">");
				sb.append(meshIds[i]);
				sb.append("</");
				sb.append(propertyName);
				sb.append(">");	
			}						
			sb.append("</id>");
			
			return (DefaultElement)XMLHelper.parseElement(sb.toString());
			
		} else {
			return meshId;
		}
	}
}
