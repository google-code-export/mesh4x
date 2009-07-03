package org.mesh4j.sync.adapters.googlespreadsheet.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.Element;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.GoogleSpreadsheetUtils;
import org.mesh4j.sync.adapters.googlespreadsheet.IGoogleSpreadSheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.payload.schema.rdf.AbstractRDFIdentifiableMapping;
import org.mesh4j.sync.payload.schema.rdf.IRDFSchema;
import org.mesh4j.sync.payload.schema.rdf.RDFInstance;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.XMLHelper;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;

/**
 * @author sharif
 *
 */
public class GoogleSpreadsheetToRDFMapping extends AbstractRDFIdentifiableMapping implements IGoogleSpreadsheetToXMLMapping{

	// MODEL VARIABLES
	
	// BUSINESS METHODs
	public GoogleSpreadsheetToRDFMapping(IRDFSchema rdfSchema) {		
		super(rdfSchema);
	}
	
	public static RDFSchema extractRDFSchema(IGoogleSpreadSheet gss, String workSheetName, List<String> identifiablePropertyNames, String rdfURL) {
		RDFSchema rdfSchema = new RDFSchema(workSheetName, rdfURL+"/"+workSheetName+"#", workSheetName);

		GSWorksheet<GSRow<GSCell>> worksheet = GoogleSpreadsheetUtils.getOrCreateWorkSheetIfAbsent(gss.getGSSpreadsheet(), workSheetName);

		int rowCount = worksheet.getChildElements().size();
		
		if(rowCount < 2)
			throw new MeshException("No rata row available in the worksheet: " + workSheetName);
		GSRow<GSCell> dataRow = worksheet.getGSRow(worksheet.getChildElements().size());

		int cellType;
		String cellName;
		GSCell gsCell;

		for (String key : dataRow.getChildElements().keySet()) {
			gsCell = dataRow.getChildElements().get(key);
			cellName = key;
			cellType = gsCell.getCellTypeFromContent();

			if (GSCell.CELL_TYPE_BOOLEAN == cellType) {
				rdfSchema.addBooleanProperty(cellName, cellName, "en");
			} else if (GSCell.CELL_TYPE_LONG == cellType) {
				rdfSchema.addLongProperty(cellName, cellName, "en");
			} else if (GSCell.CELL_TYPE_DOUBLE == cellType) {
				rdfSchema.addDoubleProperty(cellName, cellName, "en");
			} else if (GSCell.CELL_TYPE_DATE == cellType) {
				rdfSchema.addDateTimeProperty(cellName, cellName, "en");
			} else {
				// text and unknown type goes here
				rdfSchema.addStringProperty(cellName, cellName, "en");
			}
		}

		rdfSchema.setIdentifiablePropertyNames(identifiablePropertyNames);
		//rdfSchema.setVersionPropertyName(lastUpdateColumnName);
		return rdfSchema;
	}

	public RDFInstance converRowToRDF(GSRow<GSCell> row) {

		String cellName;
		Object cellValue;
		Object propertyValue;

		HashMap<String, Object> propertyValues = new HashMap<String, Object>();
		for (Entry<String, GSCell> cellEntry : row.getChildElements().entrySet()) {
			
			cellName = /*cell.getColumnTag()*/cellEntry.getKey();
			cellValue = /*cell.getCellValueAsType()*/cellEntry.getValue().getCellValueAsType();

			propertyValue = rdfSchema.cannonicaliseValue(cellName, cellValue);
			if (propertyValue != null) {
				propertyValues.put(cellName, propertyValue);
			}
		}
		
		// create rdf instance
		String id = getId(row);
		
		RDFInstance rdfInstance = this.rdfSchema.createNewInstanceFromProperties(id, propertyValues);
		return rdfInstance;
	}

	@Override
	public String getId(GSRow<GSCell> row) {
		List<String> idColumnNames = this.rdfSchema.getIdentifiablePropertyNames();
		List<String> idValues = new ArrayList<String>();
		Object idCellValue;
		for (String idColumnName : idColumnNames) {
			idCellValue = row.getGSCell(idColumnName).getCellValue();
			if(idCellValue == null){
				return null;
			} else {
				idValues.add(String.valueOf(idCellValue));
			}
		}
		return makeId(idValues);
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

	@SuppressWarnings("unchecked")
	public GoogleSpreadsheet createDataSource(String fileName, String username, String password) throws Exception {
		SpreadsheetService service = GoogleSpreadsheetUtils.getSpreadsheetService(username, password);
		DocsService docService = GoogleSpreadsheetUtils.getDocService(username,password);
		FeedURLFactory factory = GoogleSpreadsheetUtils.getSpreadsheetFeedURLFactory();
		
		GSSpreadsheet<GSWorksheet> ss = GoogleSpreadsheetUtils
				.getOrCreateGSSpreadsheetIfAbsent(factory, service, docService,	fileName);

		//return ss.getBaseEntry().getTitle().getPlainText();
		return new GoogleSpreadsheet(docService, service, factory, ss.getBaseEntry().getTitle().getPlainText(), ss);
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
	public Date getLastUpdate(GSRow<GSCell> row) {
		if(this.rdfSchema.getVersionPropertyName() == null || this.rdfSchema.getVersionPropertyName().length() == 0){
			return null;
		}
		
		GSCell cell = row.getGSCell(this.rdfSchema.getVersionPropertyName());
		if(cell == null){
			return null;
		} else {
			String dateTimeAsString = cell.getCellValue();
			Date lasUpdateDateTime = GoogleSpreadsheetUtils.normalizeDate(dateTimeAsString, GSCell.G_SPREADSHEET_DATE_FORMAT);
			return lasUpdateDateTime;
		}
	}

	@Override
	public GSRow<GSCell> getRow(GSWorksheet<GSRow<GSCell>> workSheet, String id) {
		return GoogleSpreadsheetUtils.getRow(workSheet, this.rdfSchema.getIdentifiablePropertyNames().toArray(new String[0]), this.getIds(id));
	}

}