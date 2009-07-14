package org.mesh4j.sync.adapters.csv;

import java.util.Date;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.payload.schema.ISchema;

public interface ICSVToXMLMapping extends IIdentifiableMapping, IIdentifiableCSV{

	Element convertRowToXML(CSVFile cvsFile, CSVRow row);
	
	ISchema getSchema();

	String getHeader(CSVFile cvsFile);

	Date getLastUpdate(CSVFile cvsFile, CSVRow row);

	void appliesXMLToRow(CSVFile cvsFile, CSVRow row, Element payload);
}