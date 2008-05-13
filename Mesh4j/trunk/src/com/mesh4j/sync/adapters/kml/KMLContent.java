package com.mesh4j.sync.adapters.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.IIdentifiableContent;
import com.mesh4j.sync.adapters.feed.ISyndicationFormat;
import com.mesh4j.sync.model.IContent;

public class KMLContent implements IIdentifiableContent{
	
	// MODEL VARIABLESs
	private String id;
	private Element payload;
	private int version;
	
	// BUSINESS METHODS
	public KMLContent(Element payload, String id) {
		super();
		this.payload = payload;
		this.id = id;
		this.version = payload.asXML().hashCode();
	}

	public KMLContent clone(){
		return new KMLContent(payload, id);
	}
	
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj != null)
        {
        	if(obj instanceof KMLContent){
        		KMLContent otherXmlItem = (KMLContent) obj;
        		return
        			this.getType().equals(otherXmlItem.getType())
        			&& this.getId().equals(otherXmlItem.getId())
        			&& this.getVersion() == otherXmlItem.getVersion()
        			&& this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	} else if(obj instanceof IContent){
        		IContent otherXmlItem = (IContent) obj;
        		return this.getPayload().asXML().equals(otherXmlItem.getPayload().asXML());
        	}
        }
        return false;
    }

    public int hashCode()
    {
		String resultingPayload = payload.asXML();
		return this.id.hashCode() + this.version + resultingPayload.hashCode();
    }
	public void refreshVersion() {
		this.version = this.getPayload().asXML().hashCode();		
	}

	public String getType() {
		return "kml";
	}

	public String getId() {
		return id;
	}

	public Element getPayload() {
		return payload;
	}

	public int getVersion() {
		return version;
	}
	
	public static KMLContent normalizeContent(IContent content){
		if(content instanceof KMLContent){
			KMLContent entity = (KMLContent)content;
			entity.refreshVersion();
			return entity;
		}else{
			Element kmlElement = null;
			String elementName = content.getPayload().getName();
			if(KMLContentAdapter.KML_ELEMENT_PLACEMARK.equals(elementName)
					|| KMLContentAdapter.KML_ELEMENT_STYLE.equals(elementName)
					|| KMLContentAdapter.KML_ELEMENT_STYLE_MAP.equals(elementName)){
				kmlElement = content.getPayload();
			}else{
				kmlElement = content.getPayload().element(KMLContentAdapter.KML_ELEMENT_PLACEMARK);
				if(kmlElement == null){
					kmlElement = content.getPayload().element(KMLContentAdapter.KML_ELEMENT_STYLE);
					if(kmlElement == null){
						kmlElement = content.getPayload().element(KMLContentAdapter.KML_ELEMENT_STYLE_MAP);
					}
				}
			}
			if(kmlElement == null){
				return null;
			}else{
				String id = kmlElement.attributeValue(KMLContentAdapter.XML_ID_QNAME);
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
		descriptionElement.setText("Id: " + this.id + " version: " + this.version);
		rootPayload.add(descriptionElement);
		
		rootPayload.add(this.getPayload().createCopy());
	}


}
