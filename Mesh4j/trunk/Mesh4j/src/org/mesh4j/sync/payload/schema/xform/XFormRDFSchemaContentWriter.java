package org.mesh4j.sync.payload.schema.xform;

import java.io.StringReader;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;

public class XFormRDFSchemaContentWriter implements IContentWriter{
	
	public final static String ELEMENT_XFORM = "xform";
	
	// MODEL VARIABLES
	private boolean plainXML;
	
	// BUSINESS METHODS
	
	public XFormRDFSchemaContentWriter(boolean plainXML) {
		super();
		this.plainXML = plainXML;
	}

	@Override
	public boolean mustWriteSync(Item item){
		return !this.plainXML;
	}

	@Override
	public void writeContent(ISyndicationFormat syndicationFormat, Element nsElement, Element itemElement, Item item){

		if(item.isDeleted()){
			String title = "Element was DELETED, content id = " + item.getContent().getId() + ", sync Id = "+ item.getSyncId();
			syndicationFormat.addFeedItemTitleElement(itemElement, title);
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "---DELETED---");
		}else{
			
			Element payload = getXForm(item);
			
			syndicationFormat.addFeedItemTitleElement(itemElement, "Content id = " + item.getContent().getId());
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "Sync Id: " + item.getSyncId() + " Version: " + item.getContent().getVersion());
			syndicationFormat.addFeedItemPayloadElement(itemElement, payload, encapsulateContentInCDATA());
		}
	}

	public static Element getXForm(Item item) {
		String xformXML = getXFormXML(item);
		if(xformXML.isEmpty()){
			xformXML = getXFormXMLFromRDF(item);
		}
		Element schemaElement = DocumentHelper.createElement(ISchema.ELEMENT_SCHEMA);
		if(!xformXML.isEmpty()){
			schemaElement.setText(xformXML);
		}
		return schemaElement;
	}
	
	
	private static String getXFormXML(Item item) {
		Element payload = item.getContent().getPayload();
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(payload.getName())){
			payload = payload.element(ELEMENT_XFORM);
		}
		
		if(ELEMENT_XFORM.equals(payload.getName())){
			return payload.getText();
		}
		return "";		
	}

	

	protected static String getXFormXMLFromRDF(Item item) {
		String rdfXML = getRDFXml(item);
		if(rdfXML.isEmpty()){
			return rdfXML;
		} else {
			RDFSchema rdfSchema = getSchema(rdfXML);
			return SchemaToXFormTranslator.translate(rdfSchema);
		}
	}
	
	private static String getRDFXml(Item item) {
		Element payload = item.getContent().getPayload();
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(payload.getName())){
			payload = payload.element(ISchema.ELEMENT_SCHEMA);
		}
		
		if(RDFSchema.isRDF(payload)){
			return payload.asXML();
		}
		
		if(ISchema.ELEMENT_SCHEMA.equals(payload.getName())){
			String rdfXml =  payload.getText();
			if(RDFSchema.isRDF(rdfXml)){
				return rdfXml;
			} 
		} 
		
		return "";		
	}

	protected static RDFSchema getSchema(String rdfXml) {
		StringReader reader =  new StringReader(rdfXml);
		RDFSchema rdfSchema = new RDFSchema(reader);
		return rdfSchema;
	}

	@Override
	public boolean encapsulateContentInCDATA() {
		return true;
	}


}
