package com.mesh4j.sync.adapters.kml;

import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.parsers.XMLViewElement;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public class KMLMeshDOMLoaderFactory {
	
	private static XMLView XML_VIEW;
	
	static {
		XMLViewElement folderView = new XMLViewElement(KmlNames.KML_ELEMENT_FOLDER);
		folderView.addAttribute(KmlNames.MESH_QNAME_SYNC_ID);
		folderView.addAttribute(KmlNames.MESH_QNAME_PARENT_ID);
		folderView.addAttribute(KmlNames.KML_ATTRIBUTE_ID_QNAME);
		folderView.addElement(KmlNames.KML_ELEMENT_NAME);
		folderView.addElement(KmlNames.KML_ELEMENT_DESCRIPTION);
		folderView.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		
		XMLViewElement placemarkView = new XMLViewElement(KmlNames.KML_ELEMENT_PLACEMARK);
		XMLViewElement styleMapView = new XMLViewElement(KmlNames.KML_ELEMENT_STYLE_MAP);
		XMLViewElement styleView = new XMLViewElement(KmlNames.KML_ELEMENT_STYLE);
		
		XML_VIEW = new XMLView(folderView, placemarkView, styleMapView, styleView);
	}
	
	public static KMLMeshDOMLoader createDOMLoader(String fileName, IIdentityProvider identityProvider){
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		String localFileName = fileName.trim(); 
		if(localFileName.toUpperCase().endsWith(".KMZ")){
			return new KMZDOMLoader(fileName, identityProvider, getDefaultXMLView());
		} else if (localFileName.toUpperCase().endsWith(".KML")){
			return new KMLDOMLoader(fileName, identityProvider, getDefaultXMLView());
		} else {
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
			return null; // Only for java compilation
		}
	}
	
	public static XMLView getDefaultXMLView(){
		return XML_VIEW;
	}
}
