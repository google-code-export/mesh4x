package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;

import com.mesh4j.sync.adapters.compound.IContentAdapter;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.MeshException;

public class KMLContentAdapter implements IContentAdapter{
	
	private static final Log Logger = LogFactory.getLog(KMLContentAdapter.class);
	private static final String SHARED_ITEMS = "Shared Items";
	
	// MODEL VARIABLES
	private File kmlFile;
	private Document kmlDocument;
	private Element kmlDocumentElement;
	private Element kmlSharedFolderElement;
	
	// BUSINESS METHODS
	public KMLContentAdapter(String kmlFile){
		this(new File(kmlFile));
	}
	
	public KMLContentAdapter(File kmlFile){
		super();
		
		try{
			this.kmlFile = kmlFile;
			
			boolean forceFlush = false;
			if(!kmlFile.exists()){
				this.kmlDocument = DocumentHelper.createDocument();
				this.kmlDocument.addElement(KmlNames.KML_ELEMENT, KmlNames.KML_URI);
				forceFlush = true;
			} else {
				SAXReader saxReader = new SAXReader();
				this.kmlDocument = saxReader.read(this.kmlFile);
			}
			
			forceFlush = this.initializeDocumentElement() || forceFlush;
			forceFlush = this.initializeFolderElement() || forceFlush;
			this.prepareKMLToSync(forceFlush);
		} catch(Exception e){
			throw new MeshException(e);
		}
		
	}

	private boolean initializeDocumentElement() {
		Element kmlElement = this.kmlDocument.getRootElement();
		Element document = kmlElement.element(KmlNames.KML_ELEMENT_DOCUMENT);
		if(document == null){
			this.kmlDocumentElement = kmlElement.addElement(KmlNames.KML_ELEMENT_DOCUMENT, KmlNames.KML_URI);
			return true;
		} else {
			this.kmlDocumentElement = document;
			return false;
		}
	}
	
	private boolean initializeFolderElement() throws JaxenException {
		List<Element> folders = this.selectElements("//kml:Folder");
		for (Element folder : folders) {
			Element folderName = folder.element(KmlNames.KML_ELEMENT_NAME);
			if(folderName != null && SHARED_ITEMS.equals(folderName.getText())){
				this.kmlSharedFolderElement = folder;
				return false;
			}
		}
		
		Element folder = this.kmlDocumentElement.addElement(KmlNames.KML_ELEMENT_FOLDER, KmlNames.KML_URI);
		Element elementName = folder.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
		elementName.addText(SHARED_ITEMS);
		this.kmlSharedFolderElement = folder;
		return true;
	}
	
	@Override
	public void save(IContent content) {
		KMLContent kmlContent = KMLContent.normalizeContent(content);
		
		Element newPayload = kmlContent.getPayload().createCopy();
		Element element = this.getElementById(kmlContent.getId());
		if(element == null){
			Element parent = this.kmlDocumentElement; 
			if(KmlNames.KML_ELEMENT_PLACEMARK.equals(newPayload.getName())){
				parent = this.kmlSharedFolderElement; 
				String parentId = newPayload.attributeValue(KmlNames.PARENT_ID_QNAME);
				if(parentId != null){
					Element newParent = getElementById(parentId);
					if(newParent != null){
						parent = newParent;
					}
				}
			}
			parent.add(newPayload);
		} else {
			if(KmlNames.KML_ELEMENT_FOLDER.equals(element.getName())){
				updateFolderElements(element, newPayload);
			} else {
				Element parent = element.getParent();
				parent.remove(element);
				
				String parentId = newPayload.attributeValue(KmlNames.PARENT_ID_QNAME);
				if(parentId != null){
					Element newParent = getElementById(parentId);
					if(newParent != null){
						parent = newParent;
					}
				}
				parent.add(newPayload);
			}
		}
		this.flush();
	}

	private void updateFolderElements(Element element, Element updatedElement) {
		Element updatedName = updatedElement.element(KmlNames.KML_ELEMENT_NAME);
		if(updatedName == null){
			return;
		}
		
		String folderName = updatedName.getText();
		if(folderName == null){
			return;
		}
		
		Element folderNameElement = element.element(KmlNames.KML_ELEMENT_NAME);
		if(folderNameElement == null){
			folderNameElement = element.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
			folderNameElement.addText(folderName);
		} else {
			folderNameElement.setText(folderName);
		}
	}
 	
	private Element getElementById(String elementId)  {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KmlNames.XML_PREFIX, KmlNames.XML_URI);
		map.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		
		try {
			Dom4jXPath xpath = new Dom4jXPath("//kml:*[@xml:id='"+elementId+"']");
			xpath.setNamespaceContext(new SimpleNamespaceContext(map));
			return (Element) xpath.selectSingleNode(this.kmlDocument);
		} catch (JaxenException e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);
		}
	}

	@Override
	public void delete(IContent content) {
		Element element = this.getElementById(content.getId());
		if(element != null){
			element.getParent().remove(element);
		}
		this.flush();
	}

	@Override
	public KMLContent get(String id) {
		Element element = this.getElementById(id);
		if(element == null){
			return null;
		} else {
			if(KmlNames.KML_ELEMENT_FOLDER.equals(element.getName())){
				Element folderNameElement = element.element(KmlNames.KML_ELEMENT_NAME);
				
				Element newFolder = DocumentHelper.createDocument().addElement(KmlNames.KML_ELEMENT_FOLDER, KmlNames.KML_URI);
				newFolder.addNamespace(KmlNames.XML_PREFIX, KmlNames.XML_URI);
				newFolder.addAttribute(KmlNames.XML_ID_QNAME, id);
				if(folderNameElement != null){
					Element newFolderNameElement = newFolder.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
					newFolderNameElement.addText(folderNameElement.getText());
				}
				return new KMLContent(newFolder, id);
			} else {
				return new KMLContent(element, id);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IContent> getAll() {		
		RefreshResult refreshResult = this.refreshContent();
		if(refreshResult.isDirty()){
			this.flush();
		}
		return refreshResult.getContents();
	}

	@SuppressWarnings("unchecked")
	private List<Element> selectElements(String xpathExpression) throws JaxenException {
		List<Element> elements = new ArrayList<Element>();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
			
		Dom4jXPath xpath = new Dom4jXPath(xpathExpression);
		xpath.setNamespaceContext(new SimpleNamespaceContext(map));
		  
		elements = xpath.selectNodes(this.kmlDocument);
		  
		return elements;
	}

	private String makeNewID() {
		return IdGenerator.newID();
	}

	@Override
	public String getType() {
		return KmlNames.KML_PREFIX;
	}
	
	private void flush() {
		XMLHelper.write(this.kmlDocument, this.kmlFile);
	}

	public static void prepareKMLToSync(String kmlFileToPrepare) {
		new KMLContentAdapter(kmlFileToPrepare);
	}	
	private void prepareKMLToSync(boolean forceFlush) {
		RefreshResult refreshResult = this.refreshContent();
		if(forceFlush || refreshResult.isDirty()){
			this.flush();
		}
	}	
	
	private RefreshResult refreshContent() {
		boolean dirty = false;
		ArrayList<IContent> result = new ArrayList<IContent>();
		try{	
			List<Element> elements = this.selectElements("//kml:Folder");
			for (Element element : elements) {
				Element folderNameElement = element.element(KmlNames.KML_ELEMENT_NAME);
				String folderName = folderNameElement.getText(); 
				if(!SHARED_ITEMS.equals(folderName)){
					String id = element.attributeValue(KmlNames.XML_ID_QNAME);
					if(id == null){
						id = makeNewID();
						element.addNamespace(KmlNames.XML_PREFIX, KmlNames.XML_URI);
						element.addAttribute(KmlNames.XML_ID_QNAME, id);
						dirty = true;
					}
					folderName = element.element(KmlNames.KML_ELEMENT_NAME).getText();
					Element newFolder = DocumentHelper.createDocument().addElement(KmlNames.KML_ELEMENT_FOLDER, KmlNames.KML_URI);
					newFolder.addNamespace(KmlNames.XML_PREFIX, KmlNames.XML_URI);
					newFolder.addAttribute(KmlNames.XML_ID_QNAME, id);
					folderNameElement = newFolder.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
					folderNameElement.addText(folderName);
					KMLContent content = new KMLContent(newFolder, id);
					result.add(content);
				}
			}
			
			elements = this.selectElements("//kml:Style");
			elements.addAll(this.selectElements("//kml:StyleMap"));			
			for (Element element : elements) {
				String id = element.attributeValue(KmlNames.XML_ID_QNAME);
				if(id == null){
					id = makeNewID();
					element.addNamespace(KmlNames.XML_PREFIX, KmlNames.XML_URI);
					element.addAttribute(KmlNames.XML_ID_QNAME, id);
					
					String kmlID = element.attributeValue("id");
					element.addAttribute("id", kmlID+"_"+id);
					
					String kmlIDRef = "#"+kmlID;
					String newKmlIDRef = "#"+kmlID+"_"+id;
					
					List<Element> references = this.selectElements("//kml:styleUrl[text()='"+ kmlIDRef +"']");
					for (Element refElement : references) {
						refElement.setText(newKmlIDRef);
					}
					dirty = true;
				}
				KMLContent content = new KMLContent(element, id);
				result.add(content);
			}
			
			elements = this.selectElements("//kml:Placemark");			
			for (Element element : elements) {
				String id = element.attributeValue(KmlNames.XML_ID_QNAME);
				if(id == null){
					id = makeNewID();
					element.addNamespace(KmlNames.XML_PREFIX, KmlNames.XML_URI);
					element.addAttribute(KmlNames.XML_ID_QNAME, id);
					dirty = true;
				}
				
				String parentId = element.getParent().attributeValue(KmlNames.XML_ID_QNAME);
				if(parentId != null){
					String myParentID = element.attributeValue(KmlNames.PARENT_ID_QNAME);
					if(myParentID == null){
						element.addNamespace(KmlNames.XLINK_PREFIX, KmlNames.XLINK_URI);
						element.addAttribute(KmlNames.PARENT_ID_QNAME, parentId);
						dirty = true;
					} else if(!parentId.equals(myParentID)){
						element.attribute(KmlNames.PARENT_ID_QNAME).setValue(parentId);
						dirty = true;
					}
				}
				
				KMLContent content = new KMLContent(element, id);
				result.add(content);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage(), e);
			throw new MeshException(e);			
		}	
		return new RefreshResult(dirty, result);
	}
	
	private class RefreshResult {
		
		private boolean dirty = false;
		private List<IContent> contents = new ArrayList<IContent>();
		
		protected RefreshResult(boolean dirty, List<IContent> contents){
			this.dirty = dirty;
			this.contents = contents;
		}
		protected boolean isDirty(){
			return dirty;
		}
		protected List<IContent> getContents(){
			return contents;
		}
	}
}
