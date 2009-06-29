package org.mesh4j.sync.adapters.csv;

import java.io.StringWriter;

import org.mesh4j.sync.validations.Guard;

public class CSVRow {

	// MODEL VARIABLES
	private CSVHeader header;
	private String[] cellValues;
	
	// BUSINESS METHODS
	
	public CSVRow(CSVHeader header) {
		Guard.argumentNotNull(header, "header");
		this.header = header;
		this.cellValues = new String[header.getColumnNames().length];
	}
	
	public CSVRow(CSVHeader header, String csvCellValues) {

		this(header);
		
		Guard.argumentNotNullOrEmptyString(csvCellValues, "csvCellValues");		
		
		this.cellValues = csvCellValues.split(",");
	}

	public void write(StringWriter writer) {
		int size = this.cellValues.length;
		for (int i = 0; i < size; i++) {
			String cellValue = this.cellValues[i];
			writer.append(cellValue);
			
			if(i != size-1){
				writer.append(",");
			}
		}
		writer.append("\n");
	}
	
	public String getCellValue(int cellIndex) {
		if(cellIndex == -1){
			return null;
		}
		String value = this.cellValues[cellIndex];
		return value;
	}

	public boolean setCellValue(String columnName, String value) {
		int index = this.header.getCellIndex(columnName);
		if(index >= 0){
			this.cellValues[index] = value;
			return true;
		}else{
			return false;
		}
		
	}
	
	public void setCellValue(int cellIndex, String value) {
		this.cellValues[cellIndex] = value; 
	}

	public String getCellValue(String columnName) {
		int index = this.header.getCellIndex(columnName);
		return getCellValue(index);
	}

	public int getCellCount() {
		return this.cellValues.length;
	}

	public String getHeader(int cellIndex) {
		return this.header.getColumnName(cellIndex);
	}

}
