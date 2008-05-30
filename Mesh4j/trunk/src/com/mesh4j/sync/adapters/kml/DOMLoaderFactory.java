package com.mesh4j.sync.adapters.kml;

import com.mesh4j.sync.adapters.dom.DOMLoader;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.parsers.IXMLView;
import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public class DOMLoaderFactory {
	
	public static DOMLoader createDOMLoader(String fileName, IIdentityProvider identityProvider){
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");
		
		String localFileName = fileName.trim(); 
		if(localFileName.toUpperCase().endsWith(".KMZ")){
			return new KMZDOMLoader(fileName, identityProvider, createKMLView());
		} else if (localFileName.toUpperCase().endsWith(".KML")){
			return new KMLDOMLoader(fileName, identityProvider, createKMLView());
		} else {
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
			return null; // Only for java compilation
		}
	}
	
	public static IXMLView createKMLView(){
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		
		KMLViewElement folderView = new KMLViewElement(KmlNames.KML_QNAME_FOLDER, hierarchyView);
		folderView.addAttribute(MeshNames.MESH_QNAME_SYNC_ID);
		folderView.addAttribute(KmlNames.KML_ATTRIBUTE_ID_QNAME);
		folderView.addElement(KmlNames.KML_ELEMENT_NAME);
		folderView.addElement(KmlNames.KML_ELEMENT_DESCRIPTION);
		folderView.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		
		KMLViewElement placemarkView = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, hierarchyView);
		KMLViewElement styleMapView = new KMLViewElement(KmlNames.KML_QNAME_STYLE_MAP, hierarchyView);
		KMLViewElement styleView = new KMLViewElement(KmlNames.KML_QNAME_STYLE, hierarchyView);
		
		return new XMLView(folderView, placemarkView, styleMapView, styleView, hierarchyView);
	}
}
