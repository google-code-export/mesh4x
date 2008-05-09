package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.compound.IContentAdapter;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;

public class KMLContentAdapter implements IContentAdapter{
	
	private static final Log Logger = LogFactory.getLog(KMLContentAdapter.class);
	
	private static final String KML_PREFIX = "kml";
	private static final String KML_URI = "http://earth.google.com/kml/2.2";
	
	private static final String XML_PREFIX = "xml";
	private static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
	
	private static final String KML_ELEMENT_FOLDER = "Folder";
	private static final String KML_ELEMENT_NAME = "name";
	private static final String SHARED_ITEMS = "Shared Items";
	private static final String KML_ELEMENT_DOCUMENT = "Document";
	private static final String KML_ELEMENT_PLACEMARK = "Placemark";
	private static final String KML_ELEMENT_STYLE_MAP ="StyleMap";
	private static final String KML_ELEMENT_STYLE ="Style";
	
	private static final Namespace XML_NS = DocumentHelper.createNamespace(XML_PREFIX, XML_URI);
	private static final QName XML_ID_QNAME = DocumentHelper.createQName("id", XML_NS);

		
	// MODEL VARIABLES
	private File kmlFile;
	private Document kmlDocument;
	private Element kmlDocumentElement;
	private Element kmlSharedFolderElement;
	
	// BUSINESS METHODS
	public KMLContentAdapter(String kmlFile) throws DocumentException {
		this(new File(kmlFile));
	}
	
	public KMLContentAdapter(File kmlFile) throws DocumentException {
		super();
		this.kmlFile = kmlFile;
		
		SAXReader saxReader = new SAXReader();
		this.kmlDocument = saxReader.read(this.kmlFile);
		
		this.initializeDocumentElement();
		this.initializeFolderElement();
	}

	private void initializeDocumentElement() {
		Element kmlElement = this.kmlDocument.getRootElement();
		Element document = kmlElement.element(KML_ELEMENT_DOCUMENT);
		if(document == null){
			document = kmlElement.addElement(KML_ELEMENT_DOCUMENT, KML_URI);
		}
		this.kmlDocumentElement = document;
	}
	
	private void initializeFolderElement() {
		List<Element> folders = this.selectElements("//kml:Folder");
		for (Element folder : folders) {
			Element folderName = folder.element(KML_ELEMENT_NAME);
			if(folderName != null && SHARED_ITEMS.equals(folderName.getText())){
				this.kmlSharedFolderElement = folder;
				return;
			}
		}
		
		Element folder = this.kmlDocumentElement.addElement(KML_ELEMENT_FOLDER, KML_URI);
		Element elementName = folder.addElement(KML_ELEMENT_NAME, KML_URI);
		elementName.addText(SHARED_ITEMS);
		this.kmlSharedFolderElement = folder;
	}
	
	@Override
	public void save(EntityContent entity) {
		Element newPayload = entity.getPayload().createCopy();
		Element element = this.getElementById(entity.getEntityId());
		if(element == null){
			if(KML_ELEMENT_PLACEMARK.equals(newPayload.getName())){
				this.kmlSharedFolderElement.add(newPayload);	
			} else {
				this.insertBeforeFirst(KML_ELEMENT_NAME, newPayload);
			}
		} else {
			if(KML_ELEMENT_PLACEMARK.equals(newPayload.getName())){
				Element parent = element.getParent();
				parent.remove(element);
				parent.add(newPayload);
			} else {
				this.replaceElement(element, newPayload);
			}
		}
		this.flush();
	}

	@SuppressWarnings("unchecked")
	private void replaceElement(Element originalElement, Element newElement) {
		Element kmlElement = this.kmlDocument.getRootElement();
		Element newDocumentElement = kmlElement.addElement(KML_ELEMENT_DOCUMENT, KML_URI);
		
		List<Element> elements = this.kmlDocumentElement.elements();
		for (Element element : elements) {
			if(element == originalElement){
				newDocumentElement.add(newElement);
			} else {
				newDocumentElement.add(element.createCopy());
			}
		}
		kmlElement.remove(this.kmlDocumentElement);
		this.kmlDocumentElement = newDocumentElement;
	}
	
	@SuppressWarnings("unchecked")
	private void insertBeforeFirst(String elementName, Element newPayload) {
		Element kmlElement = this.kmlDocument.getRootElement();
		Element newDocumentElement = kmlElement.addElement(KML_ELEMENT_DOCUMENT, KML_URI);
		
		List<Element> elements = this.kmlDocumentElement.elements();
		boolean mustAdd = true;
		for (Element element : elements) {
			if(elementName.equals(element.getName())){
				if(mustAdd){
					newDocumentElement.add(newPayload);
					mustAdd = false;
				}
			}
			newDocumentElement.add(element.createCopy());
		}
		kmlElement.remove(this.kmlDocumentElement);
		this.kmlDocumentElement = newDocumentElement;
		
	}

	private Element getElementById(String entityId)  {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(XML_PREFIX, XML_URI);
		map.put(KML_PREFIX, KML_URI);
		
		try {
			Dom4jXPath xpath = new Dom4jXPath("//kml:*[@xml:id='"+entityId+"']");
			xpath.setNamespaceContext(new SimpleNamespaceContext(map));
			return (Element) xpath.selectSingleNode(this.kmlDocument);
		} catch (JaxenException e) {
			Logger.error(e.getMessage(), e); // TODO (JMT) throws runtime exception ?
			return null;
		}
	}

	@Override
	public void delete(EntityContent entity) {
		Element element = this.getElementById(entity.getEntityId());
		if(element != null){
			element.getParent().remove(element);
		}
		this.flush();
	}

	@Override
	public EntityContent get(String entityId) {
		Element element = this.getElementById(entityId);
		if(element == null){
			return null;
		} else {
			return new EntityContent(element, this.getEntityName(), entityId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntityContent> getAll() {
		
		ArrayList<EntityContent> result = new ArrayList<EntityContent>();
				
		boolean dirty = false;
		List<Element> elements = this.selectElements("//kml:Placemark");
		
		for (Element element : elements) {
			String id = element.attributeValue(XML_ID_QNAME);
			if(id == null){
				id = makeNewID();
				element.addNamespace(XML_PREFIX, XML_URI);
				element.addAttribute(XML_ID_QNAME, id);
				dirty = true;
			}
			EntityContent entityContent = new EntityContent(element, this.getEntityName(), id);
			result.add(entityContent);
		}
		
		elements = this.selectElements("//kml:Style");
		elements.addAll(this.selectElements("//kml:StyleMap"));
		
		for (Element element : elements) {
			String id = element.attributeValue(XML_ID_QNAME);
			if(id == null){
				id = element.attributeValue("id");
				element.addNamespace(XML_PREFIX, XML_URI);
				element.addAttribute(XML_ID_QNAME, id);
				dirty = true;
			}else{
				String kmlId = element.attributeValue("id");
				if(!kmlId.equals(id)){
					id = kmlId;
					element.addNamespace(XML_PREFIX, XML_URI);
					element.addAttribute(XML_ID_QNAME, id);
					dirty = true;					
				}
			}
			EntityContent entityContent = new EntityContent(element, this.getEntityName(), id);
			result.add(entityContent);
		}

		if(dirty){
			this.flush();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Element> selectElements(String xpathExpression) {
		List<Element> elements = new ArrayList<Element>();
		try {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KML_PREFIX, KML_URI);
			
			Dom4jXPath xpath = new Dom4jXPath(xpathExpression);
			xpath.setNamespaceContext(new SimpleNamespaceContext(map));
		  
			elements = xpath.selectNodes(this.kmlDocument);
		  
		} catch (JaxenException e) {
			Logger.error(e.getMessage(), e);   // TODO (JMT) throws runtime exception ?
		}
		return elements;
	}

	private String makeNewID() {
		return IdGenerator.newID();
	}

	@Override
	public String getEntityName() {
		return KML_PREFIX;
	}

	@Override
	public EntityContent normalizeContent(IContent content) {
		return EntityContent.normalizeContent(content, this.getEntityName(), "id");
	}
	
	private void flush() {
		XMLHelper.write(this.kmlDocument, this.kmlFile);
	}	

}