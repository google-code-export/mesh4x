package com.mesh4j.sync.adapters.dom.parsers;

import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;

import com.mesh4j.sync.adapters.dom.IMeshDOM;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.parsers.IXMLViewElement;
import com.mesh4j.sync.validations.Guard;

public abstract class MeshXMLViewElement implements IXMLViewElement, IDOMRequied {

	// MODEL VARIABLES
	private IMeshDOM dom;
	
	// BUSINESS METHODS
	
	@Override
	public Element normalize(Element element) {
		if(element != null && !this.manage(element)){
			return null;
		} else {
			return element;
		}
	}
	
	@Override
	public void setDOM(IMeshDOM dom) {
		this.dom = dom;		
	}	
	
	public IMeshDOM getDOM() {
		return this.dom;		
	}	
	
	@Override
	public void clean(Document document, Element element) {
		Guard.argumentNotNull(element, "element");
		Guard.argumentNotNull(element.getParent(), "parent");
		
		if(element != null && !this.manage(element)){
			Guard.throwsArgumentException("element type", element);
		}
		element.getParent().remove(element);
	}

	@Override
	public Map<String, String> getNameSpaces() {
		HashMap<String, String> ns = new HashMap<String, String>();
		ns.put(MeshNames.MESH_PREFIX, MeshNames.MESH_URI);
		return ns;
	}
	
	protected Element getContentRepoElement(Document document) {
		Guard.argumentNotNull(this.dom, "dom");
		return this.dom.getContentRepository(document);
	}
	
	protected Element getSyncRepoElement(Document document) {
		Guard.argumentNotNull(this.dom, "dom");
		return this.dom.getSyncRepository(document);
	}
}
