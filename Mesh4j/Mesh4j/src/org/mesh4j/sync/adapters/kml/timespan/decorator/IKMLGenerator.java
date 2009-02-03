package org.mesh4j.sync.adapters.kml.timespan.decorator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.mesh4j.sync.model.Item;

public interface IKMLGenerator {

	Document makeDocument(String documentName);
	
	void addElement(Document document, Item item);

	Element getElement(Document document, Item item);

	boolean hasItemChanged(Document document, Element itemElement, Item item);

	String getEndTimeSpan(Item item) throws Exception;

}
