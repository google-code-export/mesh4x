package org.mesh4j.sync.adapters.csv;


public interface IIdentifiableCSV {

	String getId(CSVFile csvFile, CSVRow row);

}
