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
		
		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		for (int i = 0; i < row.getCellCount(); i++) {
			cellName = row.getHeader(i);
			cellValue = row.getCellValue(i);
			
			propertyValue = rdfSchema.cannonicaliseValue(cellName, cellValue);
			if(propertyValue != null){
				propertyValues.put(cellName, propertyValue);
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
		
		int size = this.rdfSchema.getPropertyCount();
		for (int i = 0; i < size; i++) {
			sb.append(this.rdfSchema.getPropertyName(i));
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
		if(cellValue != null){
			return (Date) this.rdfSchema.cannonicaliseValue(this.rdfSchema.getVersionPropertyName(), cellValue);
		} else {
			return null;
		}
	}

	public void appliesRDFToRow(CSVRow row, RDFInstance rdfInstance) {

		String propertyValue;
	
		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			propertyValue = rdfInstance.getPropertyValueAsLexicalForm(propertyName);
						
			if(propertyValue != null){
				row.setCellValue(propertyName, propertyValue);	
			}			
		}		
	}

	public static IRDFSchema extractRDFSchema(String fileName, String[] identifiablePropertyNames, String versionPropertyName, String rdfBaseURL) {
		CSVFile csvFile = new CSVFile(fileName);
		csvFile.read(new IdentifierCSV(identifiablePropertyNames));
		CSVHeader header = csvFile.getHeader();
		
		String sheetName = csvFile.getSheetName();
		RDFSchema rdfSchema = new RDFSchema(sheetName, rdfBaseURL +"/"+ sheetName +"#", sheetName);
		for (String propertyName : header.getColumnNames()) {
			rdfSchema.addStringProperty(propertyName, propertyName, "en");
		}
		rdfSchema.setIdentifiablePropertyNames(Arrays.asList(identifiablePropertyNames));
		rdfSchema.setVersionPropertyName(versionPropertyName);
		return rdfSchema;
	}
}
