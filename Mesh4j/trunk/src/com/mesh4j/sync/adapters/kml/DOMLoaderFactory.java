package com.mesh4j.sync.adapters.kml;

import com.mesh4j.sync.adapters.dom.DOMLoader;
import com.mesh4j.sync.adapters.dom.MeshNames;
import com.mesh4j.sync.adapters.dom.parsers.FileManager;
import com.mesh4j.sync.adapters.dom.parsers.FileXMLViewElement;
import com.mesh4j.sync.adapters.dom.parsers.HierarchyXMLViewElement;
import com.mesh4j.sync.parsers.XMLView;
import com.mesh4j.sync.security.IIdentityProvider;
import com.mesh4j.sync.validations.Guard;

public class DOMLoaderFactory {
	
	public static DOMLoader createDOMLoader(String fileName, IIdentityProvider identityProvider){
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		Guard.argumentNotNull(identityProvider, "identityProvider");

		
		String localFileName = fileName.trim();
		if(localFileName.toUpperCase().endsWith(".KMZ")){
			FileManager fileManager = new FileManager();
			return new KMZDOMLoader(fileName, identityProvider, createKMZView(fileManager), fileManager);
		} else if (localFileName.toUpperCase().endsWith(".KML")){
			return new KMLDOMLoader(fileName, identityProvider, createKMLView());
		} else {
			Guard.throwsArgumentException("Arg_InvalidKMLFileName", fileName);
			return null; // Only for java compilation
		}
	}
	
	public static XMLView createKMLView(){
		HierarchyXMLViewElement hierarchyView = new HierarchyXMLViewElement();
		
		KMLViewElement folderView = new KMLViewElement(KmlNames.KML_QNAME_FOLDER, hierarchyView, false);
		folderView.addAttribute(MeshNames.MESH_QNAME_SYNC_ID);
		folderView.addAttribute(KmlNames.KML_ATTRIBUTE_ID_QNAME);
		folderView.addElement(KmlNames.KML_ELEMENT_NAME);
		folderView.addElement(KmlNames.KML_ELEMENT_DESCRIPTION);
		folderView.addElement(KmlNames.KML_ELEMENT_EXTENDED_DATA);
		
		KMLViewElement placemarkView = new KMLViewElement(KmlNames.KML_QNAME_PLACEMARK, hierarchyView, false);
		KMLViewElement photoView = new KMLViewElement(KmlNames.KML_QNAME_PHOTO_OVERLAY, hierarchyView, false);
		KMLViewElement styleMapView = new KMLViewElement(KmlNames.KML_QNAME_STYLE_MAP, hierarchyView, true);
		KMLViewElement styleView = new KMLViewElement(KmlNames.KML_QNAME_STYLE, hierarchyView, true);
		//return new XMLView(folderView, placemarkView, photoView, styleMapView, styleView, hierarchyView);
		return new XMLView(styleView, styleMapView, folderView, placemarkView, photoView, hierarchyView);
	}
	
	public static XMLView createKMZView(FileManager fileManager){
		XMLView xmlView = createKMLView();
		FileXMLViewElement fileView = new FileXMLViewElement(fileManager);
		xmlView.addXMLViewElement(fileView);
		return xmlView;
	}
}