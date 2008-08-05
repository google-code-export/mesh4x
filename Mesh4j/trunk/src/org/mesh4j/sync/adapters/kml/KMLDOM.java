package org.mesh4j.sync.adapters.kml;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.mesh4j.sync.adapters.dom.MeshDOM;
import org.mesh4j.sync.adapters.dom.MeshNames;
import org.mesh4j.sync.model.IContent;
import org.mesh4j.sync.parsers.IXMLView;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.translator.MessageTranslator;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;


public class KMLDOM extends MeshDOM {
	
	// Constructor
	public KMLDOM(Document document, IIdentityProvider identityProvider,
			IXMLView xmlView) {
		super(document, identityProvider, xmlView);
	}
	
	public KMLDOM(String name, IIdentityProvider identityProvider, IXMLView xmlView) {
		super(createDocument(name), identityProvider, xmlView);
	}

	@Override
	public String getType(){
		return KmlNames.KML_PREFIX;
	}
		
	@Override
	public Element getSyncRepository(Document document){
		return getContentRepository(document)
			.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);
	}
	
	@Override
	public IContent createContent(Element element, String syncID) {
		return new KMLContent(element, syncID);
	}

	@Override
	public void updateMeshStatus(){
		validateDocument(this.getDocument());
		prepateSyncRepository(this.getDocument());
		super.updateMeshStatus();
	}
	
	private static void prepateSyncRepository(Document kmlDocument) {
		
		Element syncRepositoryRoot = kmlDocument.getRootElement().element(KmlNames.KML_ELEMENT_DOCUMENT);
		
		Element extendedDataElement = syncRepositoryRoot.element(KmlNames.KML_ELEMENT_EXTENDED_DATA);;
		if(extendedDataElement == null){
			extendedDataElement = syncRepositoryRoot.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		}
		
		Namespace meshNS = extendedDataElement.getNamespaceForPrefix(MeshNames.MESH_PREFIX);
		if(meshNS == null){
			extendedDataElement.add(MeshNames.MESH_NS);
		}
	}

	private static Document createDocument(String name) {
		Guard.argumentNotNullOrEmptyString(name, "name");
		
		Document kmlDocument = DocumentHelper.createDocument();
		Element kmlElement = kmlDocument.addElement(KmlNames.KML_ELEMENT, KmlNames.KML_URI);
		Element documentElement = kmlElement.addElement(KmlNames.KML_ELEMENT_DOCUMENT, KmlNames.KML_URI);
		Element elementName = documentElement.addElement(KmlNames.KML_ELEMENT_NAME, KmlNames.KML_URI);
		elementName.addText(name);
		
		prepateSyncRepository(kmlDocument);
		return kmlDocument;
	}

	private void validateDocument(Document kmlDocument) {
		Element kmlElement = kmlDocument.getRootElement();
		if(kmlElement == null || !KmlNames.KML_ELEMENT.equals(kmlElement.getName())){
			throw new MeshException(MessageTranslator.translate("KML_DOES_NOT_CONTAINS_ROOT_KML_ELEMENT"));
		}
		
		Element documentElement = kmlElement.element(KmlNames.KML_ELEMENT_DOCUMENT);
		if(documentElement == null){
			throw new MeshException(MessageTranslator.translate("KML_DOES_NOT_CONTAINS_DOCUMENT_ELEMENT"));
		}
	}

	@Override
	public IContent normalizeContent(IContent content) {
		return KMLContent.normalizeContent(content, this.getXMLView());
	}

	@Override
	public Element getContentRepository(Document document) {
		return document
			.getRootElement()
			.element(KmlNames.KML_ELEMENT_DOCUMENT);
	}

}
