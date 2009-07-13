package org.mesh4j.sync.payload.schema.xform;

import java.io.StringReader;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.schema.ISchema;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;

public class XFormRDFSchemaContentWriter implements IContentWriter{
	
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
	public void writeContent(ISyndicationFormat syndicationFormat, Element itemElement, Item item){

		if(item.isDeleted()){
			String title = "Element was DELETED, content id = " + item.getContent().getId() + ", sync Id = "+ item.getSyncId();
			syndicationFormat.addFeedItemTitleElement(itemElement, title);
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "---DELETED---");
		}else{
			
			String rdfXml = getRDFXml(item);
			Element payload = asSchemaXML(rdfXml);
			
			syndicationFormat.addFeedItemTitleElement(itemElement, "Content id = " + item.getContent().getId());
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "Sync Id: " + item.getSyncId() + " Version: " + item.getContent().getVersion());
			syndicationFormat.addFeedItemPayloadElement(itemElement, payload);
		}
	}

	private String getRDFXml(Item item) {
		Element payload = item.getContent().getPayload();
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(payload.getName())){
			payload = payload.element(ISchema.ELEMENT_SCHEMA);
		}
		
		if(ISchema.ELEMENT_SCHEMA.equals(payload.getName())){
			return payload.getText();
		}
		
		if(IRDFSchema.QNAME_RDF.getNamespacePrefix().equals(payload.getNamespacePrefix()) && IRDFSchema.QNAME_RDF.getName().equals(payload.getName())){
			return payload.asXML();
		}
		
		return null;
		
	}

	protected Element asSchemaXML(String rdfXml) {
		RDFSchema rdfSchema = getSchema(rdfXml);
		String xformXML = SchemaToXFormTranslator.translate(rdfSchema);
		Element schemaElement = DocumentHelper.createElement(ISchema.ELEMENT_SCHEMA);
		schemaElement.setText(xformXML);
		return schemaElement;
	}

	protected RDFSchema getSchema(String rdfXml) {
		StringReader reader =  new StringReader(rdfXml);
		RDFSchema rdfSchema = new RDFSchema(reader);
		return rdfSchema;
	}


}
