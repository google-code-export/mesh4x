package com.mesh4j.sync.adapters.kml.spli.content.adapter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

import com.mesh4j.sync.adapters.kml.KmlNames;
import com.mesh4j.sync.parsers.XMLViewElement;

@Deprecated
public class KMLContentViewElement extends XMLViewElement{

	public KMLContentViewElement(QName qname) {
		super(qname, false);
	}
	
	@Override
	protected Element getRootElement(Document document) {
		return document.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
	}
}
