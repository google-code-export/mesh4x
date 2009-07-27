package org.mesh4j.sync.payload.schema.rdf;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.SchemaInstanceContentReadWriter;

public class RDFSchemaInstanceContentReadWriter extends SchemaInstanceContentReadWriter{
	
	// MODEL VARIABLES
	private Namespace namespace;
	private QName rdfQName;
	
	// BUSINESS METHODS
	public RDFSchemaInstanceContentReadWriter(IRDFSchema schema, IMapping mapping, boolean mustWriteSync) {
		super(schema, mapping, mustWriteSync);
		
		this.namespace = DocumentHelper.createNamespace(schema.getOntologyNameSpace(), schema.getOntologyBaseRDFUrl());
		this.rdfQName = DocumentHelper.createQName(schema.getOntologyClassName(), this.namespace);
	}

	@Override
	public void addNameSpace(Element nsElement){
		IRDFSchema rdfSchema = getRDFSchema();				
		if(nsElement.getNamespaceForPrefix(rdfSchema.getOntologyNameSpace()) == null){
			nsElement.addNamespace(rdfSchema.getOntologyNameSpace(), rdfSchema.getOntologyBaseRDFUrl());
		}
	}

	protected IRDFSchema getRDFSchema() {
		return (IRDFSchema) this.getSchema();
	}


	@SuppressWarnings("unchecked")
	@Override
	public void readContent(String id, Element payload, Element contentElement) {
		
		if(contentElement == null){
			IRDFSchema rdfSchema = getRDFSchema();
			
			Element rdfElement = DocumentHelper.createElement(this.rdfQName);
			
			List<Element> elements = payload.elements();
			
			for (Element element : elements) {
				if(rdfSchema.getOntologyNameSpace().equals(element.getNamespacePrefix()) && rdfSchema.getOntologyBaseRDFUrl().equals(element.getNamespaceURI())){
					rdfElement.add(element.createCopy());
					payload.remove(element);
				}
			}
			
			String idRDF = getIdFromPayload(rdfSchema, rdfElement, id);
			Element content = rdfSchema.getInstanceFromPlainXML(idRDF, rdfElement, ISchema.EMPTY_FORMATS);
			payload.add(content);
		} else {
			super.readContent(id, payload, contentElement);
		}
	}

	protected String getIdFromPayload(IRDFSchema rdfSchema, Element rdfElement, String defaultId) {
		ArrayList<String> idValues = new ArrayList<String>();
		for (String propertyName : rdfSchema.getIdentifiablePropertyNames()){
			Element idElement = rdfElement.element(propertyName);
			idValues.add(idElement.getText());
		}
		String idRDF = AbstractRDFIdentifiableMapping.makeId(idValues);
		if(idRDF == null || idRDF.length() == 0){
			return defaultId;
		} else {
			return idRDF;
		}
	}

	protected Element readInstanceFromXML(String id, Element contentElement) {
		return this.getSchema().getInstanceFromXML(contentElement);
	}
	
	@Override
	public boolean encapsulateContentInCDATA() {
		return false;
	}

}
