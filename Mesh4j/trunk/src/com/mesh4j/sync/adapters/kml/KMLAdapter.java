package com.mesh4j.sync.adapters.kml;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jaxen.JaxenException;

import com.mesh4j.sync.AbstractRepositoryAdapter;
import com.mesh4j.sync.IFilter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.ISecurity;
import com.mesh4j.sync.translator.MessageTranslator;
import com.mesh4j.sync.utils.IdGenerator;
import com.mesh4j.sync.utils.XMLHelper;
import com.mesh4j.sync.validations.Guard;
import com.mesh4j.sync.validations.MeshException;

public class KMLAdapter extends AbstractRepositoryAdapter {
	
	// MODEL VARIABLES
	private ISecurity security;
	private Document kmlDocument;
	private File kmlFile;
	
	// BUSINESS METHODS
	public KMLAdapter(String fileName, ISecurity security){
		super();
		
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(security, "security");
		
		this.security = security;
		
		initializeDocumentAndFile(fileName);
	}
	
	private void initializeDocumentAndFile(String fileName) {
		try{
			File file = new File(fileName);
			Document document = null;
			if(!file.exists()){
				document = DocumentHelper.createDocument();
				Element kmlElement = document.addElement(KmlNames.KML_ELEMENT, KmlNames.KML_URI);
				Element documentElement = kmlElement.addElement(KmlNames.KML_ELEMENT_DOCUMENT, KmlNames.KML_URI);
				Element elementName = documentElement.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
				elementName.addText(fileName);
			} else {
				SAXReader saxReader = new SAXReader();
				document = saxReader.read(file);
				
				Element kmlElement = document.getRootElement();
				if(kmlElement == null || !KmlNames.KML_ELEMENT.equals(kmlElement.getName())){
					throw new MeshException("invalid kml file, root element is not a kml element.");
				}
				
				Element documentElement = kmlElement.element(KmlNames.KML_ELEMENT_DOCUMENT);
				if(documentElement == null){
					throw new MeshException("invalid kml file, kml element has not contains a document element.");
				}
			}
			this.kmlDocument = document;
			this.kmlFile = file;
			this.prepareKMLToSync();
		} catch(Exception e){
			throw new MeshException(e);
		}
	}	

	@Override
	public void add(Item item) {
		// TODO (JMT) in progress
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void update(Item item) {
		// TODO (JMT) in progress
		throw new UnsupportedOperationException();
	} 

	@Override
	public void delete(String id) {
		// TODO (JMT) in progress
		throw new UnsupportedOperationException();
	}

	@Override
	public Item get(String id) {
		// TODO (JMT) in progress
		throw new UnsupportedOperationException();
	}

	@Override
	protected List<Item> getAll(Date since, IFilter<Item> filter) {
		// TODO (JMT) in progress
		throw new UnsupportedOperationException();
	}
	
	public void prepareKMLToSync() {
		
		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put(KmlNames.KML_PREFIX, KmlNames.KML_URI);
		namespaces.put(KmlNames.MESH_PREFIX, KmlNames.MESH_URI);
		
		List<Element> elements;
		try {
			elements = XMLHelper.selectElements("//kml:Folder", this.kmlDocument, namespaces);
			elements.addAll(XMLHelper.selectElements("//kml:Placemark", this.kmlDocument, namespaces));
		} catch (JaxenException e) {
			throw new MeshException(e);
		}
		
		for (Element element : elements) {
			processMeshData(element);
		}
		
		this.flush();
	}

	private void flush() {
		XMLHelper.write(this.kmlDocument, this.kmlFile);		
	}

	private void processMeshData(Element element) {
		Element meshElement = getMeshElement(element);
		processMeshIdAttribute(meshElement);
		
		Element meshParent = getMeshElement(element.getParent());
		processMeshParentIDAttribute(meshElement, meshParent);
	}

	private Element getMeshElement(Element element) {
		Element extendedDataElement = element.element(KmlNames.KML_EXTENDED_DATA_ELEMENT);
		if(extendedDataElement == null){
			extendedDataElement = element.addElement(KmlNames.KML_EXTENDED_DATA_ELEMENT);
		}
		
		Element meshElement = extendedDataElement.element(KmlNames.MESH_QNAME_SYNC);
		if(meshElement == null){
			extendedDataElement.addNamespace(KmlNames.MESH_PREFIX, KmlNames.MESH_URI);
			meshElement = extendedDataElement.addElement(KmlNames.MESH_QNAME_SYNC);
		}
		return meshElement;
	}
	
	private void processMeshIdAttribute(Element meshElement) {
		String id = meshElement.attributeValue(KmlNames.MESH_ID);
		if(id == null){
			id = makeNewID();
			meshElement.addNamespace(KmlNames.MESH_PREFIX, KmlNames.MESH_URI);
			meshElement.addAttribute(KmlNames.MESH_ID, id);
		}
	}

	private void processMeshParentIDAttribute(Element meshElement, Element meshParent) {
		String parentId = meshParent.attributeValue(KmlNames.MESH_ID);
		
		Attribute parentIDAttr = meshElement.attribute(KmlNames.MESH_PARENT_ID);					
		if(parentId != null){						
			if(parentIDAttr == null){
				meshElement.addAttribute(KmlNames.MESH_PARENT_ID, parentId);
			} else if(!parentIDAttr.getValue().equals(parentId)){
				parentIDAttr.setValue(parentId);
			}
		}else{
			if(parentIDAttr != null){
				meshElement.remove(parentIDAttr);
			}
		}
	}	
	
	protected String makeNewID() {
		return IdGenerator.newID();
	}

	// UNSUPPORTED OPERATIONS
	
	@Override
	public String getFriendlyName() {		
		return MessageTranslator.translate(KMLAdapter.class.getName());
	}

	@Override
	public boolean supportsMerge() {
		return false;
	}
	
	@Override
	public List<Item> merge(List<Item> items) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getAuthenticatedUser() {
		return this.security.getAuthenticatedUser();
	}
}