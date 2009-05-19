package org.mesh4j.sync.adapters.kml.split.content.adapter;

import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.mesh4j.sync.adapters.kml.KmlNames;


@Deprecated
public interface KMLContentAdapterNames extends KmlNames {

	public static final String XML_PREFIX = "xml";
	public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
	public static final Namespace XML_NS = DocumentHelper.createNamespace(XML_PREFIX, XML_URI);
	public static final QName XML_ID_QNAME = DocumentHelper.createQName("id", XML_NS);
		
	public static final String XLINK_PREFIX = "xlink";
	public static final String XLINK_URI = "http://www.w3.org/1999/xlink";
	public static final Namespace XLINK_NS = DocumentHelper.createNamespace(XLINK_PREFIX, XLINK_URI);
	public static final QName PARENT_ID_QNAME = DocumentHelper.createQName("href", XLINK_NS);

}
