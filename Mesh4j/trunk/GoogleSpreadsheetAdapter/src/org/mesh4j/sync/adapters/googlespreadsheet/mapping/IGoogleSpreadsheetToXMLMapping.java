package org.mesh4j.sync.adapters.googlespreadsheet.mapping;

import java.util.Date;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.IIdentifiableMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.payload.schema.ISchema;
/**
 * convert entity(row,item) to xml element and xml element to
 * entity(row,item). 
 * @author Raju
 * @version 1.0,30/3/2009
 */
public interface IGoogleSpreadsheetToXMLMapping extends IIdentifiableMapping{
	
	//for mesh4x manipulation we need convert each row which is item to xml element
	public Element convertRowToXML(GSRow<GSCell> gsRow);

	//before save the manipulated item or row to the spreadsheet we need to convert the
	//mesh4x xml element to spreadsheet listEntry which is row.
	public void applyXMLElementToRow(GSWorksheet<GSRow<GSCell>> workSheet, GSRow<GSCell> rowTobeUPdated, Element element);
	
	public ISchema getSchema();

	public GSRow<GSCell> getRow(GSWorksheet<GSRow<GSCell>> workSheet, String id);

	public Date getLastUpdate(GSRow<GSCell> row);

	public String getId(GSRow<GSCell> gsRow);
}
