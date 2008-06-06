package com.mesh4j.sync.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.mesh4j.sync.validations.Guard;

public class XMLView implements IXMLView{
	
	// MODEL VARIABLES
	private ArrayList<IXMLViewElement> xmlViews = new ArrayList<IXMLViewElement>();
		
	// BUSINESS METHODS
	
	public XMLView(IXMLViewElement... viewElements){
		if(viewElements.length == 0){
			Guard.throwsArgumentException("Arg_XML_VIEW_WITH_OUT_ELEMENT_VIEWS");
		}
		
		for (IXMLViewElement viewElement : viewElements) {
			xmlViews.add(viewElement);
		}
	}
	
	@Override
	public Element normalize(Element element){		
		IXMLViewElement xmlViewElement = this.getViewBy(element);
		if(xmlViewElement == null){
			return null;
		} else {
			return xmlViewElement.normalize(element);
		}
	}
	
	@Override
	public Element refreshAndNormalize(Document document, Element element) {
		IXMLViewElement xmlViewElement = this.getViewBy(element);
		if(xmlViewElement == null){
			return null;
		} else {
			Element refreshElement = xmlViewElement.refresh(document, element);
			return xmlViewElement.normalize(refreshElement);
		}
	}

	@Override
	public Element add(Document document, Element element) {
		IXMLViewElement xmlViewElement = this.getViewBy(element);
		if(xmlViewElement == null){
			return null;
		} else {
			return xmlViewElement.add(document, element);
		}
	}

	@Override
	public Element update(Document document, Element currentElement, Element newElement) {
		IXMLViewElement xmlViewElement = this.getViewBy(currentElement);
		if(xmlViewElement == null){
			return null;
		} else {
			return xmlViewElement.update(document, currentElement, newElement);
		}	
	}
	
	@Override
	public void delete(Document document, Element element) {
		IXMLViewElement xmlViewElement = this.getViewBy(element);
		if(xmlViewElement != null){
			xmlViewElement.delete(document, element);
		}	
	}

	@Override
	public List<Element> getAllElements(Document document) {
		ArrayList<Element> result = new ArrayList<Element>();
		for (IXMLViewElement viewElement : this.xmlViews) {
			List<Element> localResult = viewElement.getAllElements(document);
			result.addAll(localResult);
		}
		return result;
	}

	@Override
	public Map<String, String> getNameSpaces() {
		Map<String, String> result = new HashMap<String, String>();
		for (IXMLViewElement viewElement : this.xmlViews) {
			result.put(viewElement.getQName().getNamespacePrefix(), viewElement.getQName().getNamespaceURI());
		}
		return result;
	}

	@Override
	public boolean isValid(Document document, Element element) {
		IXMLViewElement xmlViewElement = this.getViewBy(element);
		if(xmlViewElement == null){
			return false;
		} else {
			return xmlViewElement.isValid(document, element);
		}
	}

	private IXMLViewElement getViewBy(Element element) {
		if(element == null) {
			return null;
		}
		
		for (IXMLViewElement view : this.xmlViews) {
			if(view.getName().equals(element.getName())){
				return view;
			}
		}
		return null;
	}

	@Override
	public List<IXMLViewElement> getXMLViewElements() {
		return this.xmlViews;
	}

	public void addXMLViewElement(IXMLViewElement xmlElementView) {
		if(xmlElementView != null){
			this.xmlViews.add(xmlElementView);
		}		
	}

	@Override
	public void clean(Document document, Element element) {
		IXMLViewElement viewElement = this.getViewBy(element);
		if(viewElement != null){
			viewElement.clean(document, element);
		}
	}
}
