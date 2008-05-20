package com.mesh4j.sync.adapters.kml;

import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.QName;

public interface KmlNames {
	
	public static final String KML_PREFIX = "kml";
	public static final String KML_URI = "http://earth.google.com/kml/2.2";
	public static final Namespace KML_NS = DocumentHelper.createNamespace(KML_PREFIX, KML_URI);

	public static final String KML_ELEMENT = "kml";
	public static final String KML_ELEMENT_FOLDER = "Folder";
	public static final String KML_ELEMENT_NAME = "name";
	public static final String KML_ELEMENT_DESCRIPTION = "description";
	public static final String KML_ELEMENT_DOCUMENT = "Document";
	public static final String KML_ELEMENT_PLACEMARK = "Placemark";
	public static final String KML_ELEMENT_STYLE = "Style";
	public static final String KML_ELEMENT_STYLE_MAP = "StyleMap";
	public static final QName KML_ATTRIBUTE_ID_QNAME = DocumentHelper.createQName("id", KML_NS);
	public static final String KML_ATTRIBUTE_ID = "id";
	public static final QName FOLDER_QNAME = DocumentHelper.createQName("Folder", KML_NS);
	
	public static final String XML_PREFIX = "xml";
	public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
	public static final Namespace XML_NS = DocumentHelper.createNamespace(XML_PREFIX, XML_URI);
	public static final QName XML_ID_QNAME = DocumentHelper.createQName("id", XML_NS);
		
	public static final String XLINK_PREFIX = "xlink";
	public static final String XLINK_URI = "http://www.w3.org/1999/xlink";
	public static final Namespace XLINK_NS = DocumentHelper.createNamespace(XLINK_PREFIX, XLINK_URI);
	public static final QName PARENT_ID_QNAME = DocumentHelper.createQName("href", XLINK_NS);
	
	public static final String KML_EXTENDED_DATA_ELEMENT = "ExtendedData";
	public static final String MESH_PREFIX = "mesh4x";
	public static final String MESH_URI = "http://mesh4x.org/kml";
	public static final Namespace MESH_NS = DocumentHelper.createNamespace(MESH_PREFIX, MESH_URI);
	public static final QName MESH_QNAME_SYNC_ID = DocumentHelper.createQName("id", MESH_NS);
	public static final QName MESH_QNAME_PARENT_ID = DocumentHelper.createQName("parentId", MESH_NS);
	public static final QName MESH_QNAME_SYNC = DocumentHelper.createQName("sync", MESH_NS);;
	//public static final String MESH_ID = "id";
	//public static final String MESH_PARENT_ID = "parentId";
	public static final String MESH_VERSION = "version";
	
}
