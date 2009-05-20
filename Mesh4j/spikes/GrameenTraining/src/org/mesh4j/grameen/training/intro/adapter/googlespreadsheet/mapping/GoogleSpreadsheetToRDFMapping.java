package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.mapping;

import java.io.File;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Element;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;

import com.google.gdata.client.docs.DocsService;

/**
 * @author sharif
 *
 */
public class GoogleSpreadsheetToRDFMapping implements IGoogleSpreadsheetToXMLMapping{

	// MODEL VARIABLES
	private IRDFSchema rdfSchema;
	private DocsService docService;
	private String idColumnName;
	private String lastUpdateColumnName = null;
	
	
	// BUSINESS METHODs
	public GoogleSpreadsheetToRDFMapping(IRDFSchema schema, String idColumnName, String lastUpdateColumnName, DocsService docService) {
		super();

		this.rdfSchema = schema;
		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
		this.docService = docService;
	}
	
	public static RDFSchema extractRDFSchema(IGoogleSpreadSheet gss, String workSheetName, String rdfURL) {
		
		Guard.argumentNotNull(gss, "gss");
		Guard.argumentNotNullOrEmptyString(workSheetName, "workSheetName");
		Guard.argumentNotNullOrEmptyString(rdfURL, "rdfURL");
		
		RDFSchema rdfSchema = new RDFSchema(workSheetName,
				rdfURL + "/" + workSheetName + "#",
				workSheetName);

		GSWorksheet<GSRow<GSCell>> worksheet = GoogleSpreadsheetUtils
				.getOrCreateWorkSheetIfAbsent(gss.getGSSpreadsheet(), workSheetName);

		GSRow<GSCell> dataRow = worksheet.getGSRow(worksheet.getChildElements()
				.size());

		int cellType;
		String cellName;
		GSCell gsCell;

		for (String key : dataRow.getChildElements().keySet()) {
			gsCell = dataRow.getChildElements().get(key);
			cellName = gsCell.getColumnTag();
			cellType = gsCell.getCellType();

			if (GSCell.CELL_TYPE_BOOLEAN == cellType) {
				rdfSchema.addBooleanProperty(cellName, cellName, "en");
			} else if (GSCell.CELL_TYPE_LONG == cellType) {
				rdfSchema.addLongProperty(cellName, cellName, "en");
			} else if (GSCell.CELL_TYPE_DOUBLE == cellType) {
				rdfSchema.addDoubleProperty(cellName, cellName, "en");
			} else if (GSCell.CELL_TYPE_DATE == cellType) {
				rdfSchema.addDateTimeProperty(cellName, cellName, "en");
			} else
				// text and unknown type goes here
				rdfSchema.addStringProperty(cellName, cellName, "en");
		}

		return rdfSchema;
	}

	//done
	public RDFInstance converRowToRDF(GSRow<GSCell> row) {

		String cellName;
		Object cellValue;
		Object propertyValue;

		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		for (GSCell cell : row.getChildElements().values() ) {
			
			cellName = cell.getColumnTag();
			cellValue = cell.getCellValueAsType();

			propertyValue = rdfSchema.cannonicaliseValue(cellName, cellValue);
			if (propertyValue != null) {
				propertyValues.put(cellName, propertyValue);
			}
		}
		
		// create rdf instance
		String id = String.valueOf(propertyValues.get(this.idColumnName));
		
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, propertyValues);
		return rdfInstance;
	}

	public void appliesRDFToRow(GSWorksheet<GSRow<GSCell>> workSheet, GSRow<GSCell> row,
			RDFInstance rdfInstance) {
		GSCell cell;
		Object propertyValue;
		String propertyType;

		int size = rdfInstance.getPropertyCount();
		for (int i = 0; i < size; i++) {
			String propertyName = rdfInstance.getPropertyName(i);
			propertyValue = rdfInstance.getPropertyValue(propertyName);

			if (propertyValue != null) {
				GSRow<GSCell> headerRow = GoogleSpreadsheetUtils
						.getOrCreateHeaderRowIfAbsent(workSheet);
				GSCell headerCell = GoogleSpreadsheetUtils
						.getOrCreateHeaderCellIfAbsent(headerRow, propertyName);

				propertyType = rdfInstance.getPropertyType(propertyName);
				cell = row.getGSCell(propertyName);
				
				if (cell == null) {
					cell = row.createNewCell(headerCell.getElementListIndex(),
							propertyName, "");
				}
				cell.setCellValueAsType(propertyValue,
						translatePropertyTypeToCellType(propertyType));
			}
		}
	}
	
	private int translatePropertyTypeToCellType(String propertyType) {
		if (IRDFSchema.XLS_STRING.equals(propertyType)) {
			return GSCell.CELL_TYPE_TEXT;
		} else if (IRDFSchema.XLS_BOOLEAN.equals(propertyType)) {
			return GSCell.CELL_TYPE_BOOLEAN;
		} else if (IRDFSchema.XLS_INTEGER.equals(propertyType)
				|| IRDFSchema.XLS_LONG.equals(propertyType)) {
			return GSCell.CELL_TYPE_LONG;
		} else if (IRDFSchema.XLS_DOUBLE.equals(propertyType)
				|| IRDFSchema.XLS_DECIMAL.equals(propertyType)) {
			return GSCell.CELL_TYPE_DOUBLE;
		} else if (IRDFSchema.XLS_DATETIME.equals(propertyType)) {
			return GSCell.CELL_TYPE_DATE;
		} else {
			return GSCell.CELL_TYPE_TEXT;
		}
	}

	public String createDataSource(String fileName) throws Exception {
		//create a msexcel document using the rdf schema
		HSSFWorkbook workbook = createDataSource();			
		MsExcelUtils.flush(workbook, fileName);
		
		//upload the excel document
		String spreadsheetId = GoogleSpreadsheetUtils
				.uploadSpreadsheetDoc(new File(fileName), this.docService);
		return spreadsheetId;
	}

	public HSSFWorkbook createDataSource() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(this.rdfSchema.getOntologyNameSpace());
		HSSFRow headerRow = sheet.createRow(0);
		HSSFCell headerCell;
		
		int size = this.rdfSchema.getPropertyCount();
		String propertyName;
		for (int j = 0; j < size; j++) {
			propertyName = this.rdfSchema.getPropertyName(size - 1 - j);	
			
			headerCell = headerRow.createCell(j);
			headerCell.setCellValue(new HSSFRichTextString(propertyName));			
		}
		return workbook;
	}
	
	@Override
	public Element convertRowToXML(GSRow<GSCell> row) {
		RDFInstance rdfInstance = this.converRowToRDF(row);
		return XMLHelper.parseElement(rdfInstance.asXML());		
	}
	
	@Override
	public void applyXMLElementToRow(GSWorksheet<GSRow<GSCell>> workSheet, GSRow<GSCell> row, Element rdfElement) {		
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromRDFXML(rdfElement.asXML());
		this.appliesRDFToRow(workSheet, row, rdfInstance);		
	}


	@Override
	public String getIdColumnName() {
		return this.idColumnName;
	}

	@Override
	public String getLastUpdateColumnName() {
		return this.lastUpdateColumnName;
	}
	
	public void setLastUpdateColumnName(String columnName) {
		this.lastUpdateColumnName = columnName;
	}

	@Override
	public IRDFSchema getSchema() {
		return rdfSchema;
	}

	@Override
	public String getType() {
		return this.getSheetName();
	}

	@Override
	public String getSheetName() {
		return this.rdfSchema.getOntologyClassName();
	}

}