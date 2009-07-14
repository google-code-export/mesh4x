package org.mesh4j.sync.adapters.csv;

import java.util.ArrayList;
import java.util.List;

import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;

public class IdentifierCSV implements IIdentifiableCSV{
	
	// MODEL VARIABLES
	private String[] idColumnNames;
	
	// BUSINESS METHODS
	public IdentifierCSV(String[] idColumnNames){
		this.idColumnNames = idColumnNames;
	}
	
	@Override
	public String getId(CSVFile csvFile, CSVRow row) {
		List<String> idValues = new ArrayList<String>();
		String idCellValue;
		for (String idColumnName : this.idColumnNames) {
			idCellValue = row.getCellValue(idColumnName);
			if(idCellValue == null){
				return null;
			} else {
				idValues.add(idCellValue);
			}
		}
		return AbstractRDFIdentifiableMapping.makeId(idValues);
	}
}
