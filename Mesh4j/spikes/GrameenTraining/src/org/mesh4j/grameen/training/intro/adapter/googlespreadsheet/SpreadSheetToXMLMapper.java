package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSListEntry;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.validations.Guard;
/**
 * 
 * @author Raju
 * @version 1.0,30/3/2009
 * @see ISpreadSheetToXMLMapper 
 */
public class SpreadSheetToXMLMapper implements ISpreadSheetToXMLMapper{

	private String idColumnName = "";
	private String lastUpdateColumnName = "";
	
	public SpreadSheetToXMLMapper(String idColumnName,String lastUpdateColumnName){
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNullOrEmptyString(lastUpdateColumnName, "lastUpdateColumnName");
		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
	}
	
	@Override
	public Element convertRowToXML(GSListEntry listEntry, GSWorksheet worksheet) {
		
		return null;
	}

	@Override
	public void convertXMLToRow(Element element, GSListEntry listEntry,
			GSWorksheet worksheet) {
	
		
	}

	@Override
	public String getIdColumnName() {
		return idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return lastUpdateColumnName;
	}

	
}
