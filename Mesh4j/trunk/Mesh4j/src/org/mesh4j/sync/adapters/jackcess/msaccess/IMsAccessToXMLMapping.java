package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.Date;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.payload.schema.ISchema;

import com.healthmarketscience.jackcess.Cursor;

public interface IMsAccessToXMLMapping extends IIdentifiableMapping {

	ISchema getSchema();

	Map<String, Object> translateAsRow(Element payload);

	Element translateAsElement(Map<String, Object> row);

	String getId(Map<String, Object> row);

	Date getLastUpdate(Map<String, Object> row);

	boolean findRow(Cursor cursor, String meshid);

}
