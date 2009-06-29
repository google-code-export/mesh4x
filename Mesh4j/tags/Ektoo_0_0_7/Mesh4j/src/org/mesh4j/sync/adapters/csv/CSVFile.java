package org.mesh4j.sync.adapters.csv;

import java.io.File;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

public class CSVFile {

	// MODEL VARIABLES
	private File file;
	private CSVHeader header;
	private Map<String, CSVRow> rows = new LinkedHashMap<String, CSVRow>();
	private boolean dirty = false;
	
	// BUSINESS METHODS
	public CSVFile(String fileName){
		Guard.argumentNotNullOrEmptyString(fileName, "fileName");
		if(!fileName.trim().toLowerCase().endsWith(".csv")){
			Guard.throwsArgumentException("fileName", fileName);
		}
		
		this.file = new File(fileName.trim());
	}

	public void createFileIfAbsent(String header) {
		try{
			if(!file.exists()){
				if(!file.getParentFile().exists()){
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			
			if(file.length() == 0){
				this.header = new CSVHeader(header);
				this.dirty = true;
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public void read(IIdentifiableCSV identifierCSV) {
		try{
			if(file.exists() && file.length() > 0){
				this.dirty = false;
				byte[] bytes = FileUtils.read(file);
				String content = new String(bytes);
				String[] csvRows = content.split("\r\n");
				int i = 0;
				for (String csvRow : csvRows) {
					if(i == 0){
						i = i +1;
						this.header = new CSVHeader(csvRow);
					} else {
						CSVRow row = new CSVRow(this.header, csvRow);
						String id = identifierCSV.getId(this, row);
						this.rows.put(id, row);
					}
				}
			}
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public void flush(File csvFile) {
		StringWriter writer = new StringWriter();
		this.header.write(writer);
		for (CSVRow row : this.rows.values()) {
			row.write(writer);
		}

		try{
			FileUtils.write(csvFile, writer.toString().getBytes());
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}
	
	public void flush() {
		if(this.dirty){
			this.flush(this.file);
		}
	}

	public CSVRow getRow(String id) {
		return this.rows.get(id);
	}

	public void setDirty() {
		this.dirty = true;		
	}

	public Collection<CSVRow> getRows() {
		return this.rows.values();
	}

	public void remove(String id) {
		this.rows.put(id, null);		
	}

	public CSVHeader getHeader() {
		return this.header;
	}

	public String getSheetName() {
		return this.file.getName();
	}

	public CSVRow newRow(String id) {
		CSVRow row = new CSVRow(this.getHeader());
		this.rows.put(id, row);
		return row;
	}
}
