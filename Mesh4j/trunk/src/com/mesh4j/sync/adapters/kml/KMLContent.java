package com.mesh4j.sync.adapters.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.parsers.IXMLViewElement;
import com.mesh4j.sync.validations.Guard;

public class KMLContent extends Content{

	public KMLContent(Element payload, String id) {
		super(payload, id);
	}

	public KMLContent clone(){
		return new KMLContent(this.getPayload(), this.getId());
	}
		
	public static KMLContent normalizeContent(IContent content, IXMLView xmlView){
		Guard.argumentNotNull(content, "content");
		Guard.argumentNotNull(xmlView, "xmlView");
		
		if(content instanceof KMLContent){
			KMLContent entity = (KMLContent)content;
			entity.refreshVersion();
			return entity;
		}else{
			Element kmlElement = getElement(content, xmlView);
			if(kmlElement == null){
				return null;
			}else{
				String id = kmlElement.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
				if(id == null){
					return null;
				} else {
					return new KMLContent(kmlElement, id);
				}
			}
		}
	}

	private static Element getElement(IContent content, IXMLView xmlView) {
		
		Guard.argumentNotNull(xmlView, "xmlView");
		
		Element payload = content.getPayload();
		String elementName = payload.getName();
		
		for (IXMLViewElement viewElement : xmlView.getXMLViewElements()){
			if(viewElement.getName().equals(elementName)){
				return payload;
			}
		}
		
		for (IXMLViewElement viewElement : xmlView.getXMLViewElements()){
			Element element = payload.element(viewElement.getName());
			if(element != null){
				return element;
			}
		}
		return null;
	}
	
	public void addToFeedPayload(Element rootPayload){
		
		Element titleElement = DocumentHelper.createElement(ISyndicationFormat.SX_ATTRIBUTE_ITEM_TITLE);
		titleElement.setText(this.getPayload().getName());
		rootPayload.add(titleElement);
		
		Element descriptionElement = DocumentHelper.createElement(ISyndicationFormat.SX_ATTRIBUTE_ITEM_DESCRIPTION);
		descriptionElement.setText("Id: " + this.getId() + " version: " + this.getVersion());
		rootPayload.add(descriptionElement);
		
		rootPayload.add(this.getPayload().createCopy());
	}


}
