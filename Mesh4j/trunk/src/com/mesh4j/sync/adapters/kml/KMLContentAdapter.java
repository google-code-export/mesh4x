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
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import com.mesh4j.sync.adapters.EntityContent;
import com.mesh4j.sync.adapters.compound.ContentAdapter;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;

public class KMLContentAdapter implements ContentAdapter{
	
	private static final Log Logger = LogFactory.getLog(KMLContentAdapter.class);
	
	private static final String ATTRIBUTE_ID = "ID";
	private static final String ATTRIBUTE_NAME = "name";
	//private static final String KML_ELEMENT_PLACEMARK = "Placemark";
	private static final String KML_ELEMENT_FOLDER = "Folder";
	private static final String KML_ELEMENT_DOCUMENT = "Document";
	private static final String SHARED_FOLDER_NAME = "Shared Items";
	private static final String KML_PREFIX = "kml";
	private static final String KML_URI = "http://earth.google.com/kml/2.2";
		
	// MODEL VARIABLES
	private File kmlFile;
	private Document kmlDocument;
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
		
		this.kmlSharedFolderElement = getSharedFolder();
	}

	@SuppressWarnings("unchecked")
	private Element getSharedFolder() {
		List<Element> folders = this.selectElements("//Folder");
		folders.addAll(this.selectElements("//kml:Folder"));		// FIXME (JMT)
		for (Element folder : folders) {
			Element folderName = folder.element(ATTRIBUTE_NAME);
			if(folderName != null && SHARED_FOLDER_NAME.equals(folderName.getText())){
				return folder;
			}
		}
		
		Element root = this.kmlDocument.getRootElement();
		Element document = root.element(KML_ELEMENT_DOCUMENT);
		if(document == null){
			document = DocumentHelper.createElement(KML_ELEMENT_DOCUMENT);
			Namespace ns = DocumentHelper.createNamespace(KML_PREFIX, KML_URI);
			document.add(ns);
			root.add(document);
		}
		
		Element folder = DocumentHelper.createElement(KML_ELEMENT_FOLDER);
		Namespace ns = DocumentHelper.createNamespace(KML_PREFIX, KML_URI);
		folder.add(ns);
		Element elementName = DocumentHelper.createElement(ATTRIBUTE_NAME);
		elementName.addText(SHARED_FOLDER_NAME);
		folder.add(elementName);
		document.add(folder);
		return folder;
	}
	
	@Override
	public void save(EntityContent entity) {
		Element newPayload = entity.getPayload().createCopy();
		Element element = kmlDocument.elementByID(entity.getEntityId());
		if(element != null){
			Element parent = element.getParent();
			parent.remove(element);
			parent.add(newPayload);
		} else {
			this.kmlSharedFolderElement.add(newPayload);
		}
		this.flush();
	}

	@Override
	public void delete(EntityContent entity) {
		Element element = kmlDocument.elementByID(entity.getEntityId());
		if(element != null){
			element.getParent().remove(element);
		}
		this.flush();
	}

	@Override
	public EntityContent get(String entityId) {
		Element element = kmlDocument.elementByID(entityId);
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
		elements.addAll(this.selectElements("//Placemark"));
		
		for (Element element : elements) {
			String id = element.attributeValue(ATTRIBUTE_ID);
			if(id == null){
				id = makeNewID();
				element.addAttribute(ATTRIBUTE_ID, id);
				dirty = true;
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
			Logger.error(e.getMessage(), e);   // TODO (JMT) throws runtime Exception
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
	public EntityContent normalizeContent(Content content) {
		return EntityContent.normalizeContent(content, this.getEntityName(), ATTRIBUTE_ID);
	}
	
	private void flush() {
		XMLHelper.write(this.kmlDocument, this.kmlFile);
	}	

}
