package org.mesh4j.sync.payload.schema;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.feed.IContentReader;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.validations.Guard;

public class SchemaInstanceContentReadWriter implements IContentWriter, IContentReader{

	// MODEL VARIABLES
	private ISchema schema;
	private IMapping mapping;
	private boolean mustWriteSync;
	
	// BUSINESS METHODS
	
	public SchemaInstanceContentReadWriter(ISchema schema, IMapping mapping, boolean mustWriteSync) {
		Guard.argumentNotNull(schema, "schema");
		Guard.argumentNotNull(mapping, "mapping");
		
		this.schema = schema;
		this.mapping = mapping;
		this.mustWriteSync = mustWriteSync;
	}

	@Override
	public boolean mustWriteSync(Item item){
		return this.mustWriteSync;
	}

	@Override
	public void writeContent(ISyndicationFormat syndicationFormat, Element nsElement, Element itemElement, Item item){

		if(item.isDeleted()){
			String title = "Element was DELETED, content id = " + item.getContent().getId() + ", sync Id = "+ item.getSyncId();
			syndicationFormat.addFeedItemTitleElement(itemElement, title);
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "---DELETED---");
		}else{
			addNameSpace(nsElement);
			
			Element payloadXML = getInstanceAsXML(item);
			Element plainXML = this.schema.asInstancePlainXML(item.getContent().getPayload(), ISchema.EMPTY_FORMATS);  // TODO (JMT) extends IMapping to support ISchema and not generate xml 2 times
			
			String title = this.mapping.getValue(plainXML, ISyndicationFormat.MAPPING_NAME_ITEM_TITLE);
			String desc = this.mapping.getValue(plainXML, ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION);
					
			syndicationFormat.addFeedItemTitleElement(itemElement, title == null || title.length() == 0 ? item.getSyncId() : title);
			syndicationFormat.addFeedItemDescriptionElement(itemElement, desc == null || desc.length() == 0 ? "Id: " + item.getContent().getId() + " Version: " + item.getContent().getVersion() : desc);
			syndicationFormat.addFeedItemPayloadElement(itemElement, payloadXML, encapsulateContentInCDATA());
		}
	}

	protected void addNameSpace(Element nsElement){
		// nothing to do
	}
	
	protected Element getInstanceAsXML(Item item) {
		return this.schema.asInstanceXML(item.getContent().getPayload(), ISchema.EMPTY_FORMATS);
	}

	@Override
	public void readContent(String id, Element payload, Element contentElement) {
		// TODO (JMT) RDF: add aditional xml elements to payloads (see ContentReader>>readContent)
		Element content = readInstanceFromXML(id, contentElement);
		payload.add(content);
	}

	protected Element readInstanceFromXML(String id, Element contentElement) {
		return this.schema.getInstanceFromXML(contentElement);
	}
	
	public ISchema getSchema(){
		return this.schema;
	}

	public IMapping getMapping() {
		return this.mapping;
	}

	@Override
	public boolean encapsulateContentInCDATA() {
		return true;
	}

}
