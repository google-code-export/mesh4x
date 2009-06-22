package org.mesh4j.sync.adapters.jackcess.msaccess;

import java.util.Date;
import java.util.Map;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.ISchema;

public interface IMsAccessToXMLMapping {

	String getIdColumnName();

	String getLastUpdateColumnName();

	ISchema getSchema();


	Map<String, Object> translateAsRow(Element payload);

	Element translateAsElement(Map<String, Object> row);

	String getMeshIdValue(Map<String, Object> row);

	Date getLastUpdateColumnValue(Map<String, Object> row);

	String normalizeAsMsAccessID(String meshID);
	
	String normalizeAsMeshID(String msAccessId);

}
