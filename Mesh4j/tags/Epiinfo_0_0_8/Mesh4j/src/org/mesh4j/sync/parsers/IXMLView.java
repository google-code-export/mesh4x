package org.mesh4j.sync.parsers;

import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

public interface IXMLView {
	
	Element update(Document document, Element currentElement, Element newElement);

	Element normalize(Element element);		

	public Element add(Document document, Element element);

	public void delete(Document document, Element element);

	public List<Element> getAllElements(Document document);

	public Map<String, String> getNameSpaces();

	Element refreshAndNormalize(Document document, Element element);

	boolean isValid(Document document, Element element);

	List<IXMLViewElement> getXMLViewElements();

	void clean(Document document, Element element);

}
