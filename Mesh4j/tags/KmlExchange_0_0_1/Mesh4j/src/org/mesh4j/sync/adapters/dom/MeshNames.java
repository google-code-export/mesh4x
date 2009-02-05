package org.mesh4j.sync.adapters.dom;

import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.QName;

public interface MeshNames {

	public static final String XML_PREFIX = "xml";
	public static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
	public static final Namespace XML_NS = DocumentHelper.createNamespace(XML_PREFIX, XML_URI);
	
	public static final String MESH_PREFIX = "mesh4x";
	public static final String MESH_URI = "http://mesh4x.org/kml";
	public static final Namespace MESH_NS = DocumentHelper.createNamespace(MESH_PREFIX, MESH_URI);
	
	public static final QName MESH_QNAME_SYNC_ID = DocumentHelper.createQName("id", XML_NS);
	public static final QName MESH_QNAME_ORIGINAL_ID = DocumentHelper.createQName("originalId", MESH_NS);
	
	public static final QName MESH_QNAME_SYNC = DocumentHelper.createQName("sync", MESH_NS);
	public static final String MESH_VERSION = "version";
	
	public static QName MESH_QNAME_HIERARCHY =  DocumentHelper.createQName("hierarchy", MESH_NS);
	public static final QName MESH_QNAME_PARENT_ID = DocumentHelper.createQName("parentId", MESH_NS);
	public static final QName MESH_QNAME_CHILD_ID = DocumentHelper.createQName("childId", MESH_NS);

	public static QName MESH_QNAME_FILE =  DocumentHelper.createQName("file", MESH_NS);
	public static final QName MESH_QNAME_FILE_ID = DocumentHelper.createQName("fileId", MESH_NS);
	public static final QName MESH_QNAME_FILE_CONTENT = DocumentHelper.createQName("fileContent", MESH_NS);
}
