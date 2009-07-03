package org.mesh4j.sync.adapters.csv;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Element;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;

public class CSVToRDFMapping extends AbstractRDFIdentifiableMapping implements ICSVToXMLMapping {
	
	// BUSINESS METHODs
	public CSVToRDFMapping(IRDFSchema rdfSchema) {
		super(rdfSchema);
	}
	
	@Override
	public void appliesXMLToRow(CSVFile csvFile, CSVRow row, Element rdfElement) {
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromRDFXML(rdfElement.asXML());
		this.appliesRDFToRow(row, rdfInstance);
	}

	@Override
	public Element convertRowToXML(CSVFile csvFile, CSVRow row) {
		RDFInstance rdfInstance = this.converRowToRDF(csvFile, row);
		return XMLHelper.parseElement(rdfInstance.asXML());
	}
	
	public RDFInstance converRowToRDF(CSVFile csvFile, CSVRow row) {

		String cellName;
		String cellValue;
		Object propertyValue;
		String propertyName;
		
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		for (int i = 0; i < row.getCellCount(); i++) {
			cellName = row.getHeader(i);
			cellValue = row.getCellValue(i);
			propertyName = RDFSchema.normalizePropertyName(cellName);
			propertyValue = rdfSchema.cannonicaliseValue(propertyName, cellValue);
			if(propertyValue != null){
				propertyValues.put(propertyName, propertyValue);
			}
		}
		
		// create rdf instance
		String id = getId(csvFile, row);
		
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, propertyValues);
		return rdfInstance;
	}

	@Override
	public String getHeader(CSVFile csvFile) {
		StringBuffer sb = new StringBuffer();
		
		String propertyName;
		String propertyLabel;
		
		int size = this.rdfSchema.getPropertyCount();
		for (int i = 0; i < size; i++) {
			propertyName = this.rdfSchema.getPropertyName(size - i -1);
			propertyLabel = this.rdfSchema.getPropertyLabel(propertyName);
			sb.append(propertyLabel);
			if(i <= size -2){
				sb.append(",");
			}			
		}
		return sb.toString();
	}

	@Override
	public String getId(CSVFile csvFile, CSVRow row) {
		List<String> idColumnNames = this.rdfSchema.getIdentifiablePropertyNames();
		List<String> idValues = new ArrayList<String>();
		String idCellValue;
		for (String idColumnName : idColumnNames) {
			idCellValue = row.getCellValue(idColumnName);
			if(idCellValue == null){
				String label = this.rdfSchema.getPropertyLabel(idColumnName);
				idCellValue = row.getCellValue(label);
			}
			if(idCellValue == null){
				return null;
			} else {
				idValues.add(idCellValue);
			}
		}
		return makeId(idValues);
	}
	
	@Override
	public Date getLastUpdate(CSVFile csvFile, CSVRow row) {
		String cellValue = row.getCellValue(this.rdfSchema.getVersionPropertyName());
		if(cellValue == null){
			String label = this.rdfSchema.getPropertyLabel(this.rdfSchema.getVersionPropertyName());
			cellValue = row.getCellValue(label);
		}
		if(cellValue == null){
			return null;
		} else {
			return (Date) this.rdfSchema.cannonicaliseValue(this.rdfSchema.getVersionPropertyName(), cellValue);
		}
	}

	public void appliesRDFToRow(CSVRow row, RDFInstance rdfInstance) {

		String propertyValue;
	
		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			propertyValue = rdfInstance.getPropertyValueAsLexicalForm(propertyName);
						
			if(propertyValue != null){
				if(row.hashHeader(propertyName)){
					row.setCellValue(propertyName, propertyValue);
				} else {
					String propertyLabel = this.rdfSchema.getPropertyLabel(propertyName);
					row.setCellValue(propertyLabel, propertyValue);
				}
			}			
		}		
	}

	public static IRDFSchema extractRDFSchema(String fileName, String[] identifiablePropertyNames, String versionPropertyName, String rdfBaseURL) {
		CSVFile csvFile = new CSVFile(fileName);
		csvFile.read(new IdentifierCSV(identifiablePropertyNames));
		CSVHeader header = csvFile.getHeader();
		
		String sheetName = csvFile.getSheetName();
		RDFSchema rdfSchema = new RDFSchema(sheetName, rdfBaseURL +"/"+ sheetName +"#", sheetName);
		for (String columnName : header.getColumnNames()) {
			String propertyName = RDFSchema.normalizePropertyName(columnName);
			rdfSchema.addStringProperty(propertyName, columnName, IRDFSchema.DEFAULT_LANGUAGE);
		}
		rdfSchema.setIdentifiablePropertyNames(Arrays.asList(identifiablePropertyNames));
		rdfSchema.setVersionPropertyName(versionPropertyName);
		return rdfSchema;
	}
}
