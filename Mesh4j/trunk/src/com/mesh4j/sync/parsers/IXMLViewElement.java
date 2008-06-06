package com.mesh4j.sync.parsers;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;

public interface IXMLViewElement {

	QName getQName();
	
	Element normalize(Element element);
	
	Element update(Document document, Element element, Element newElement);
	
	Element add(Document document, Element newElement);

	void delete(Document document, Element element);

	List<Element> getAllElements(Document document);

	String getName();

	Element refresh(Document document, Element element);

	boolean isValid(Document document, Element element);

	void clean(Document document, Element element);
}
