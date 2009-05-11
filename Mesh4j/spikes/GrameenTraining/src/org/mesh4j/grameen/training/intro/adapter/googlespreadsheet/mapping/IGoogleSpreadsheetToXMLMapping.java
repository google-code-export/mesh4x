package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping;

import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
/**
 * convert entity(row,item) to xml element and xml element to
 * entity(row,item). 
 * @author Raju
 * @version 1.0,30/3/2009
 */
public interface IGoogleSpreadsheetToXMLMapping {
	
	//in spreadsheet every sheet is known as entity and it holds number of
	//rows where each row known as item and to represent each item with a
	//unique id a column is needed which will hold the id.basically we say
	//this is idcolumnname.
	public String getIdColumnName();
	
	public int getIdColumnPosition();

	//whenever any item in entity is updated or changed its corresponding
	//lasupdatecolumnname is also updated with date.
	@Deprecated
	public String getLastUpdateColumnName();

	//provides the last update column position
	public int getLastUpdateColumnPosition();

	//which is basically entity name
	public String getType();
	
	//for mesh4x manipulation we need convert each row which is item to xml element
	public Element convertRowToXML(GSRow<GSCell> gsRow);

	//before save the manipulated item or row to the spreadsheet we need to convert the
	//mesh4x xml element to spreadsheet listEntry which is row.
	public void applyXMLElementToRow(GSWorksheet<GSRow<GSCell>> workSheet, GSRow<GSCell> rowTobeUPdated, Element element);

	IRDFSchema getSchema();

	//update the changes from payLoad into the provided rowTobeUPdated
//	public GSRow<GSCell> normalizeRow(GSWorksheet<GSRow<GSCell>> workSheet,Element payLoad,GSRow<GSCell> rowTobeUPdated);
	
	 
	
}
