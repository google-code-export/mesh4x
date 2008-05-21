package com.mesh4j.sync.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;

public class XMLView implements IXMLView {

	// MODEL VARIABLES
	private HashMap<String, String> elementNames = new HashMap<String, String>();
	private ArrayList<QName> elementQNames = new ArrayList<QName>();
	private ArrayList<String> attributeNames = new ArrayList<String>();
	private ArrayList<QName> attributeQNames = new ArrayList<QName>();
	
	// BUSINESS METHODS
	
	@Override
	public void addElement(String name) {
		elementNames.put(name, null);
	}
	
	@Override
	public void addElement(String name, String uri) {
		elementNames.put(name, uri);
	}
	
	@Override
	public void addElement(QName qName) {
		elementQNames.add(qName);
	}

	@Override
	public void addAttribute(String name) {
		attributeNames.add(name);		
	}
	
	@Override
	public void addAttribute(QName qName) {
		attributeQNames.add(qName);		
	}

	@Override
	public Element normalize(Element element) {
		if(hasTranslate()){
			Element newElement = DocumentHelper.createElement(element.getQName());			
			this.update(newElement, element);			
			return newElement;
		} else {
			return element;
		}
	}

	@Override
	public void update(Element element, Element elementSource) {
		if(hasTranslate()){
			this.customUpdate(element, elementSource);
		} else {
			this.updateAll(element, elementSource);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void updateAll(Element element, Element elementSource) {
		
		List<Attribute> attributes = elementSource.attributes();
		for (Attribute attribute : attributes) {
			updateAttribute(element, attribute.getQName(), attribute.getValue());
		}		
		
		List<Element> childrenElements = elementSource.elements();
		for (Element current : childrenElements) {
			updateElement(element, current);
		}
		
		ArrayList<Attribute> attributesToRemove = new ArrayList<Attribute>();
		
		attributes = element.attributes();
		for (Attribute attribute : attributes) {
			if(elementSource.attribute(attribute.getQName()) == null){
				attributesToRemove.add(attribute);
			}
		}
		
		for (Attribute attribute : attributesToRemove) {
			element.remove(attribute);
		}
		
		ArrayList<Element> childToRemove = new ArrayList<Element>();
		
		childrenElements = element.elements();
		for (Element current : childrenElements) {
			if(elementSource.element(current.getQName()) == null){
				childToRemove.add(current);
			}
		}
		
		for (Element current : childToRemove) {
			element.remove(current);
		}
	}

	private void updateElement(Element element, Element newChild) {
		Element child = element.element(newChild.getQName());
		if(child != null){
			element.remove(child);
		}
		element.add(newChild.createCopy());
	}

	private void updateAttribute(Element element, QName attrName, String value) {
		Attribute attr = element.attribute(attrName);
		if(attr == null){
			element.add(attrName.getNamespace());
			element.addAttribute(attrName, value);
		} else {
			attr.setValue(value);
		}
	}

	public void customUpdate(Element element, Element elementSource) {

		for (String name : this.elementNames.keySet()) {
			Element child = elementSource.element(name);
			if(child != null){
				updateElement(element, child);
			}
		}
		
		for (QName qName : this.elementQNames) {
			Element child = elementSource.element(qName);
			if(child != null){
				updateElement(element, child);
			}
		}
		
		
		for (String name : this.attributeNames) {
			Attribute attr = elementSource.attribute(name);
			if(attr != null){
				updateAttribute(element, attr.getQName(), attr.getValue());
			}
		}
		
		for (QName qName : this.attributeQNames) {
			Attribute attr = elementSource.attribute(qName);
			if(attr != null){
				updateAttribute(element, attr.getQName(), attr.getValue());
			}
		}		
	}

	private boolean hasTranslate(){
		return !this.elementNames.isEmpty() 
			|| !this.elementQNames.isEmpty()
			|| !this.attributeNames.isEmpty()
			|| !this.attributeQNames.isEmpty();
	}
}
