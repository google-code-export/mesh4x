package org.mesh4j.sync.adapters.feed;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.payload.mappings.IMapping;
import org.mesh4j.sync.validations.Guard;

public class ContentWriter implements IContentWriter {

	public static final ContentWriter INSTANCE = new ContentWriter();
	
	// MODEL VARIABLES
	private IMapping mapping;

	// BUSINESS METHODS
	private ContentWriter(){
		super();
	}

	public ContentWriter(IMapping mapping){
		Guard.argumentNotNull(mapping, "mapping");
		this.mapping = mapping;
	}
	
	@Override
	public boolean mustWriteSync(Item item){
		return true;
	}

	@Override
	public void writeContent(ISyndicationFormat syndicationFormat, Element itemElement, Item item) {
		
		if(syndicationFormat == null || itemElement == null || item == null){
			return;
		}
		
		if(item.isDeleted()){
			String title = "Element was DELETED, content id = " + item.getContent().getId() + ", sync Id = "+ item.getSyncId();
			syndicationFormat.addFeedItemTitleElement(itemElement, title);
			syndicationFormat.addFeedItemDescriptionElement(itemElement, "---DELETED---");
		}else{

			String title = null;
			String desc = null;
			
			String defaultTitle = item.getSyncId();
			String defaultDescription = "Id: " + item.getContent().getId() + " Version: " + item.getContent().getVersion();
			
			if(this.mapping != null){
				title = this.mapping.getValue(item.getContent().getPayload(), ATTR_ITEM_TITLE);
				desc = this.mapping.getValue(item.getContent().getPayload(), ATTR_ITEM_DESCRIPTION);
			}
			
			if(item.getContent() instanceof XMLContent){
				this.addXMLContentToFeedPayload(syndicationFormat, itemElement, ((XMLContent)item.getContent()), defaultTitle, title, defaultDescription, desc);
			}else {
				syndicationFormat.addFeedItemTitleElement(itemElement, title == null || title.length() == 0 ? defaultTitle : title);
				syndicationFormat.addFeedItemDescriptionElement(itemElement, desc == null || desc.length() == 0 ? defaultDescription : desc);
				syndicationFormat.addFeedItemPayloadElement(itemElement, item.getContent().getPayload().createCopy());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void addXMLContentToFeedPayload(ISyndicationFormat syndicationFormat, Element itemElement, XMLContent xmlContent, String defaultTitle, String title, String defaultDescription, String description){
		
		// set title
		if(title == null){
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
		if(description == null){
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
		
		// set payload
		if(ISyndicationFormat.ELEMENT_PAYLOAD.equals(xmlContent.getPayload().getName())){
			List<Element> payloadElements = xmlContent.getPayload().elements();
			
			if(payloadElements.size() > 1){
				syndicationFormat.addFeedItemPayloadElement(itemElement, xmlContent.getPayload().createCopy());
			} else if(payloadElements.size() == 1){
				syndicationFormat.addFeedItemPayloadElement(itemElement, payloadElements.get(0).createCopy());
			}
		} else {
			syndicationFormat.addFeedItemPayloadElement(itemElement, xmlContent.getPayload().createCopy());
		}
		
	}
}
