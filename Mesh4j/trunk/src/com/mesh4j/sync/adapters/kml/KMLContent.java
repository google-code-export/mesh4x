package com.mesh4j.sync.adapters.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.IContent;

public class KMLContent extends Content{
		
	public KMLContent(Element payload, String id) {
		super(payload, id);
	}

	public KMLContent clone(){
		return new KMLContent(this.getPayload(), this.getId());
	}
		
	public static KMLContent normalizeContent(IContent content){
		if(content instanceof KMLContent){
			KMLContent entity = (KMLContent)content;
			entity.refreshVersion();
			return entity;
		}else{
			Element kmlElement = null;
			String elementName = content.getPayload().getName();
			if(KmlNames.KML_ELEMENT_PLACEMARK.equals(elementName)
					|| KmlNames.KML_ELEMENT_STYLE.equals(elementName)
					|| KmlNames.KML_ELEMENT_STYLE_MAP.equals(elementName)
					|| KmlNames.KML_ELEMENT_FOLDER.equals(elementName)){
				kmlElement = content.getPayload();
			}else{
				kmlElement = content.getPayload().element(KmlNames.KML_ELEMENT_PLACEMARK);
				if(kmlElement == null){
					kmlElement = content.getPayload().element(KmlNames.KML_ELEMENT_STYLE);
					if(kmlElement == null){
						kmlElement = content.getPayload().element(KmlNames.KML_ELEMENT_STYLE_MAP);
						if(kmlElement == null){
							kmlElement = content.getPayload().element(KmlNames.KML_ELEMENT_FOLDER);
						}
					}
				}
			}
			if(kmlElement == null){
				return null;
			}else{
				String id = kmlElement.attributeValue(KmlNames.XML_ID_QNAME);
				if(id == null){
					return null;
				} else {
					return new KMLContent(kmlElement, id);
				}
			}
		}
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
