package org.mesh4j.sync.adapters.csv;

import java.io.StringWriter;

import org.mesh4j.sync.validations.Guard;

public class CSVHeader {

	// MODEL VARIABLES
	private String[] columnNames;
	
	// BUSINESS METHODS
	public CSVHeader(String csvHeader) {
		Guard.argumentNotNullOrEmptyString(csvHeader, "csvHeader");
		
		this.columnNames = csvHeader.split(",");
	}

	public void write(StringWriter writer) {
		int size = this.columnNames.length;
		for (int i = 0; i < size; i++) {
			String columnName = this.columnNames[i];
			writer.append(columnName);
			if(i <= size-2){
				writer.append(",");
			}
		}
		
	}

	public int getCellIndex(String columnName) {
		int i = 0;
		for (String colName : this.columnNames) {
			if(colName.equals(columnName)){
				return i;
			} else {
				i = i +1;
			}
		}
		return -1;
	}

	public String getColumnName(int cellIndex) {
		return this.columnNames[cellIndex];
	}

	public String[] getColumnNames(){
		return this.columnNames;
	}
}
