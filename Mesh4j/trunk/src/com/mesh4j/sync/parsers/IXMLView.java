package com.mesh4j.sync.parsers;

import org.dom4j.Element;
import org.dom4j.QName;

public interface IXMLView {
	
	void addElement(String name);
	void addElement(String name, String uri);
	void addElement(QName qName);
	
	void addAttribute(QName qName);
	void addAttribute(String name);
	
	Element normalize(Element element);
	
	void update(Element currentElement, Element newElement);




}
