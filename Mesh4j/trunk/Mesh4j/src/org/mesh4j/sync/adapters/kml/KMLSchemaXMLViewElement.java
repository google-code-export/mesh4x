package org.mesh4j.sync.adapters.kml;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.parsers.XMLViewElement;


public class KMLSchemaXMLViewElement extends XMLViewElement{

	public KMLSchemaXMLViewElement() {
		super(KmlNames.KML_QNAME_SCHEMA, true);
	}

	@Override
	public boolean isValid(Document document, Element element) {
		if(super.isValid(document, element)){
			String syncID = element.attributeValue(MeshNames.MESH_QNAME_SYNC_ID);
			return syncID != null;
		} else {
			return false;
		}
	}
	
	@Override
	protected Element getRootElement(Document document) {
		return document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
	}
	
	@Override
	public void clean(Document document, Element element) {
		super.clean(document, element);
		
		Attribute syncIDAttr = element.attribute(MeshNames.MESH_QNAME_SYNC_ID);
		if(syncIDAttr != null){
			element.remove(syncIDAttr);
		}
	}

}
