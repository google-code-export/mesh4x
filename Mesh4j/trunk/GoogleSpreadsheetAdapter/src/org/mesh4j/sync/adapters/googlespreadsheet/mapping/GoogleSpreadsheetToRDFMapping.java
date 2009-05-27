package org.mesh4j.sync.adapters.googlespreadsheet.mapping;

import java.io.File;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.dom4j.Element;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.msexcel.MsExcelUtils;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;

/**
 * @author sharif
 *
 */
public class GoogleSpreadsheetToRDFMapping implements IGoogleSpreadsheetToXMLMapping{

	// MODEL VARIABLES
	private IRDFSchema rdfSchema;
	private DocsService docService;
	private String type;
	private String idColumnName;
	private String lastUpdateColumnName = null;
	
	
	// BUSINESS METHODs
	public GoogleSpreadsheetToRDFMapping(IRDFSchema schema, String type, String idColumnName, String lastUpdateColumnName, DocsService docService) {
		super();
		Guard.argumentNotNull(schema,	"schema");
		Guard.argumentNotNullOrEmptyString(type, "type");
		Guard.argumentNotNullOrEmptyString(idColumnName, "idColumnName");
		Guard.argumentNotNull(docService,	"docService");
		
		this.rdfSchema = schema;
		this.type = type;
		this.idColumnName = idColumnName;
		this.lastUpdateColumnName = lastUpdateColumnName;
		this.docService = docService;
	}
	
	public static RDFSchema extractRDFSchema(IGoogleSpreadSheet gss,
			String workSheetName, String rdfURL) {
		RDFSchema rdfSchema = new RDFSchema(workSheetName, rdfURL+"/"+workSheetName+"#", workSheetName);

		GSWorksheet<GSRow<GSCell>> worksheet = GoogleSpreadsheetUtils
				.getOrCreateWorkSheetIfAbsent(gss.getGSSpreadsheet(), workSheetName);

		int rowCount = worksheet.getChildElements().size();
		
		if(rowCount < 2)
			throw new MeshException("No rata row available in the worksheet: " + workSheetName);
		
		GSRow<GSCell> dataRow = worksheet.getGSRow(worksheet.getChildElements().size());

		int cellType;
		String cellName;
		GSCell gsCell;

		for (String key : dataRow.getChildElements().keySet()) {
			gsCell = dataRow.getChildElements().get(key);
			cellName = gsCell.getColumnTag();
			cellType = gsCell.getCellTypeFromContent();

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
		Workbook workbook = createDataSource();			
		MsExcelUtils.flush(workbook, fileName);
		
		//upload the excel document
		return GoogleSpreadsheetUtils.uploadSpreadsheetDoc(new File(fileName), this.docService);
	}

	public Workbook createDataSource() {
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(this.rdfSchema.getOntologyClassName());
		sheet.setFitToPage(true);
		Row headerRow = sheet.createRow(0);
		Cell headerCell;
		
		int size = this.rdfSchema.getPropertyCount();
		String propertyName;
		for (int j = 0; j < size; j++) {
			propertyName = this.rdfSchema.getPropertyName(size - 1 - j);	
			
			headerCell = headerRow.createCell(j);
			headerCell.setCellValue(MsExcelUtils.getRichTextString(workbook, propertyName));			
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
		return this.type;
	}

	@Override
	public String getSheetName() {
		return rdfSchema.getOntologyNameSpace();
	}

}