package com.mesh4j.sync.parsers;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

public interface IXMLViewElement {

	Element normalize(Element element);
	
	Element update(Document document, Element element, Element newElement);
	
	Element add(Document document, Element newElement);

	void delete(Document document, Element element);

	List<Element> getAllElements(Document document);

	Element refresh(Document document, Element element);

	boolean isValid(Document document, Element element);

	void clean(Document document, Element element);

	boolean manage(Element element);

	Map<String, String> getNameSpaces();
}
