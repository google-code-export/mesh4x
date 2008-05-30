package com.mesh4j.sync.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;

import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;

public class XMLViewElement implements IXMLViewElement {

	// MODEL VARIABLES
	private HashMap<String, String> elementNames = new HashMap<String, String>();
	private ArrayList<QName> elementQNames = new ArrayList<QName>();
	private ArrayList<String> attributeNames = new ArrayList<String>();
	private ArrayList<QName> attributeQNames = new ArrayList<QName>();
	private QName qname;
	
	// BUSINESS METHODS
	
	public XMLViewElement(QName qname) {
		Guard.argumentNotNull(qname, "qname");
		this.qname = qname;
	}

	public void addElement(String name) {
		elementNames.put(name, null);
	}
	
	public void addElement(String name, String uri) {
		elementNames.put(name, uri);
	}
	
	public void addElement(QName qName) {
		elementQNames.add(qName);
	}

	public void addAttribute(String name) {
		attributeNames.add(name);		
	}
	
	public void addAttribute(QName qName) {
		attributeQNames.add(qName);		
	}

	@Override
	public String getName() {
		return getQName().getName();
	}
	
	@Override
	public QName getQName() {
		return this.qname;
	}
	
	@Override
	public Element normalize(Element element) {
		
		if(element == null){
			return null;
		}
		
		if(!this.getName().equals(element.getName())){
			return null;
		}
		
		if(hasTranslate()){
			Element newElement = DocumentHelper.createElement(element.getQName());			
			this.update(newElement, element);			
			return newElement;
		} else {
			return element;
		}
	}

	@Override
	public Element update(Document document, Element element, Element elementSource) {
		return update(element, elementSource);
	}
	
	private Element update(Element element, Element elementSource) {
	
		if(element == null){
			return null;
		}
		
		if(elementSource == null){
			return null;
		}
		
		if(!this.getName().equals(element.getName())){
			return null;
		}
		
		if(!this.getName().equals(elementSource.getName())){
			return null;
		}
		
		if(hasTranslate()){
			this.customUpdate(element, elementSource);
			return element;
		} else {
			Element parent = element.getParent();
			parent.remove(element);
			parent.add(elementSource);
			return elementSource;
		}
	}
	
	private void customUpdate(Element element, Element elementSource) {

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

	private boolean hasTranslate(){
		return !this.elementNames.isEmpty() 
			|| !this.elementQNames.isEmpty()
			|| !this.attributeNames.isEmpty()
			|| !this.attributeQNames.isEmpty();
	}

	@Override
	public Element add(Document document, Element element) {
		
		if(document == null){
			return null;
		}
		
		if(element == null){
			return null;
		}
		
		Element normalizedElement = this.normalize(element);
		if(normalizedElement == null){
			return null;
		}
		
		Element root = this.getRootElement(document, element);
		root.add(normalizedElement);
		return normalizedElement;
	}

	protected Element getRootElement(Document document, Element element) {
		return document.getRootElement();
	}

	@Override
	public void delete(Document document, Element element) {

		if(document == null){
			return;
		}
		
		if(element == null){
			return;
		}
		
		Element parent = element.getParent();
		if(parent != null){
			parent.remove(element);
		} else {
			document.remove(element);
		}
		
	}

	@Override
	public List<Element> getAllElements(Document document) {
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(this.getQName().getNamespacePrefix(), this.getQName().getNamespaceURI());
		
		String xpathExp = "//"+this.getQName().getNamespacePrefix()+":"+this.getQName().getName();
		return XMLHelper.selectElements(xpathExp, document.getRootElement(), namespaces);
	}

	@Override
	public Element refresh(Document document, Element element) {
		return element;
	}

	@Override
	public boolean isValid(Document document, Element element) {
		
		if(document == null){
			return false;
		}
		
		if(element == null){
			return false;
		}
		return true;
	}
}
