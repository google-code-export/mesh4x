package org.mesh4j.sync.adapters.kml;

import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.adapters.feed.ISyndicationFormat;
import org.mesh4j.sync.model.Content;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.IXMLView;
import org.mesh4j.sync.parsers.IXMLViewElement;
import org.mesh4j.sync.validations.Guard;


public class KMLContent extends Content{

	public KMLContent(Element payload, String id) {
		super(payload, id);
	}

	public KMLContent clone(){
		return new KMLContent(this.getPayload().createCopy(), this.getId());
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

	@SuppressWarnings("unchecked")
	private static Element getElement(IContent content, IXMLView xmlView) {
		
		Guard.argumentNotNull(xmlView, "xmlView");
		
		Element payload = content.getPayload();
		
		for (IXMLViewElement viewElement : xmlView.getXMLViewElements()){
			if(viewElement.manage(payload)){
				return payload;
			}
		}
		
		for (IXMLViewElement viewElement : xmlView.getXMLViewElements()){
			List<Element> elements = payload.elements();
			for (Element element : elements) {
				if(viewElement.manage(element)){
					return element;
				}
			}
		}
		return null;
	}
	
	@Override
	public void addToFeedPayload(Sync sync, Element itemElement, ISyndicationFormat format){
		format.addFeedItemTitleElement(itemElement, this.getPayload().getName());
		format.addFeedItemDescriptionElement(itemElement, "Id: " + this.getId() + " version: " + this.getVersion());
		format.addFeedItemPayloadElement(itemElement, this.getPayload().createCopy());
	}


}
