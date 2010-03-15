package org.mesh4j.sync.adapters.feed.pfif;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.mesh4j.sync.adapters.feed.IContentWriter;
import org.mesh4j.sync.adapters.feed.INamespace;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.adapters.feed.pfif.mapping.IPfifToPlainXmlMapping;
import org.mesh4j.sync.adapters.feed.pfif.schema.PFIFSchema;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;

public class PFIFContentWriter implements IContentWriter,INamespace {

	
	private IMapping mapping;
	private boolean mustWriteSync;
	private boolean encapsulateContentInCDATA;
	private IPfifToPlainXmlMapping pfifMapper ;
	
	
	public PFIFContentWriter(IMapping mapping,IPfifToPlainXmlMapping pfifMapper,
			boolean mustWriteSync,
			boolean encapsulateContentInCDATA){
		this.mapping = mapping;
		this.mustWriteSync = mustWriteSync;
		this.encapsulateContentInCDATA = encapsulateContentInCDATA;
		this.pfifMapper = pfifMapper;
	}
	
	public PFIFContentWriter(boolean mustWriteSync,boolean encapsulateContentInCDATA ){
		this.mustWriteSync = mustWriteSync;
		this.encapsulateContentInCDATA = encapsulateContentInCDATA;
	}
	
	
	
	@Override
	public boolean encapsulateContentInCDATA() {
		return encapsulateContentInCDATA;
	}

	@Override
	public boolean mustWriteSync(Item item) {
		return mustWriteSync;
	}

	/**
	 * @param itemElement, which is actually payload of PFIF person or note element
	 */
	@Override
	public void writeContent(ISyndicationFormat syndicationFormat,
			Element nsElement, Element itemElement, Item item) {

		if(syndicationFormat == null || 
				itemElement == null || item == null){
			return;
		}
		
		if(item.isDeleted()){
			String title = "Element was DELETED, content id = " + item.getContent().getId() + ", sync Id = "+ getDefaultTitle(item);
			syndicationFormat.addFeedItemTitleElement(itemElement, title);
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "---DELETED---");
		}else{
			String title = null;
			String desc = null;
			
			String defaultTitle = getDefaultTitle(item);
			String defaultDescription = getDefaultDescription(item);
			
			if(this.mapping != null){
				title = this.mapping.getValue(item.getContent().getPayload(), ISyndicationFormat.MAPPING_NAME_ITEM_TITLE);
				desc = this.mapping.getValue(item.getContent().getPayload(), ISyndicationFormat.MAPPING_NAME_ITEM_DESCRIPTION);
			}
			
			if(item.getContent() instanceof XMLContent){
				this.addXMLContentToFeedPayload(syndicationFormat, itemElement, ((XMLContent)item.getContent()), defaultTitle, title, defaultDescription, desc);
			}else {
				syndicationFormat.addFeedItemTitleElement(itemElement, title == null || title.length() == 0 ? defaultTitle : title);
				syndicationFormat.addFeedItemDescriptionElement(itemElement, desc == null || desc.length() == 0 ? defaultDescription : desc);
				
				Element pfifElement = pfifMapper.convertXMLToPfif(item.getContent().getPayload().createCopy());
				Element rootPayloadElement = null;
				if(!ISyndicationFormat.ELEMENT_PAYLOAD.equals(pfifElement.getName())){
					rootPayloadElement = DocumentHelper.createElement("payload");
					rootPayloadElement.add(pfifElement);
				}
				syndicationFormat.addFeedItemPayloadElement(itemElement, rootPayloadElement, encapsulateContentInCDATA());
			}
		}
	}

	
	@SuppressWarnings("unchecked")
	private void addXMLContentToFeedPayload(ISyndicationFormat syndicationFormat, Element itemElement, XMLContent xmlContent, String defaultTitle, String title, String defaultDescription, String description){
		
		// set title
		if(title == null || title.length() == 0){
			String myTitle = null;
			if(xmlContent.getTitle() == null || xmlContent.getTitle().length() == 0){
				if(defaultTitle != null){
					myTitle = defaultTitle;
				}
			}else{
				myTitle = xmlContent.getTitle();
			}
			
			if(myTitle != null){
				syndicationFormat.addFeedItemTitleElement(itemElement, myTitle);
			}
		} else {
			syndicationFormat.addFeedItemTitleElement(itemElement, title);
		}

		// set description
		if(description == null || description.length() == 0){
			String myDesc = null;
			if(xmlContent.getDescription() == null || xmlContent.getDescription().length() == 0){
				if(defaultDescription != null){
					myDesc = defaultDescription;
				}
			}else{
				myDesc = xmlContent.getDescription();
			}
			
			if(myDesc != null){
				syndicationFormat.addFeedItemDescriptionElement(itemElement, myDesc);
			}
		} else {
			syndicationFormat.addFeedItemDescriptionElement(itemElement, description);
		}
		
		// set link
		if(xmlContent.getLink() != null && xmlContent.getLink().length() > 0){
			syndicationFormat.addFeedItemLinkElement(itemElement, xmlContent.getLink());
		}
		
		Element pfifElement = null;
		if(RDFSchema.isRDF(xmlContent.getPayload())){
			pfifElement = pfifMapper.convertXMLToPfif(xmlContent.getPayload());
		} else {
			pfifElement = xmlContent.getPayload();
		}
		
		
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(pfifElement.getName())){
			syndicationFormat.addFeedItemPayloadElement(itemElement,pfifElement.createCopy(), encapsulateContentInCDATA());
		} else {
			Element rootPayloadElement = DocumentHelper.createElement("payload");
			rootPayloadElement.add(pfifElement.detach());
			syndicationFormat.addFeedItemPayloadElement(itemElement, rootPayloadElement.createCopy(), encapsulateContentInCDATA());
		}
		
	}
	
	public String getDefaultTitle(Item item) {
		return item.getSyncId();
	}
	
	public String getDefaultDescription(Item item) {
		return "Id: " + item.getContent().getId() + " Version: " + item.getContent().getVersion();
	}

	@Override
	public List<Namespace> getNamespaceList(){
		List<Namespace> nsList = new LinkedList<Namespace>();
		nsList.add(PFIFSchema.PFIF_NS_1_2);
		return nsList;
	}
	
	
	
}
