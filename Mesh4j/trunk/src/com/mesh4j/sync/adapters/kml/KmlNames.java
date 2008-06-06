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
	public static final String KML_ELEMENT_PHOTO_OVERLAY = "PhotoOverlay";
	public static final String KML_ELEMENT_GROUND_OVERLAY = "GroundOverlay";
	public static final String KML_ELEMENT_SCHEMA = "Schema";
	public static final String KML_ELEMENT_SCHEMA_DATA = "SchemaData";
	public static final String KML_ELEMENT_DATA = "Data";
	
	public static final QName KML_ATTRIBUTE_ID_QNAME = DocumentHelper.createQName("id", KML_NS);
	public static final String KML_ATTRIBUTE_ID = "id";
	public static final QName KML_QNAME_FOLDER = DocumentHelper.createQName("Folder", KML_NS);
	public static final QName KML_QNAME_PLACEMARK = DocumentHelper.createQName("Placemark", KML_NS);
	public static final QName KML_QNAME_STYLE = DocumentHelper.createQName("Style", KML_NS);
	public static final QName KML_QNAME_STYLE_MAP = DocumentHelper.createQName("StyleMap", KML_NS);
	public static final QName KML_QNAME_PHOTO_OVERLAY = DocumentHelper.createQName("PhotoOverlay", KML_NS);
	public static final QName KML_QNAME_GROUND_OVERLAY = DocumentHelper.createQName("GroundOverlay", KML_NS);
	public static final QName KML_QNAME_SCHEMA = DocumentHelper.createQName("Schema", KML_NS);
	
	public static final String KML_ELEMENT_EXTENDED_DATA = "ExtendedData";
	
	public static final String KMZ_DEFAULT_ENTRY_NAME_TO_KML = "doc.kml";

}
