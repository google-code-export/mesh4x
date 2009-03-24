package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.ServiceException;

/**
 * @author sharif
 *
 */
public class GoogleSpreadsheetUtils {

	public static void flush(SpreadsheetEntry spreadsheet, String fileName) {
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileName);
		//todo	workbook.write(fos);
		}catch (Exception e) {
			throw new MeshException(e);
		}finally{
			if(fos != null){
				try{
					fos.close();
				}catch (Exception e) {
					throw new MeshException(e);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Element translate(HSSFSheet worksheet, HSSFRow row, String elementName) {
		Element payload = DocumentHelper.createElement(elementName);
		HSSFRow rowHeader = worksheet.getRow(0);
		
		HSSFCell cell;
		HSSFCell cellHeader;
		String columnName;
		String columnValue;
		Element header;
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			cell = iterator.next();
			cellHeader = rowHeader.getCell(cell.getColumnIndex());
			
			columnName = cellHeader.getRichStringCellValue().getString();
			columnValue = cell.getRichStringCellValue().getString();		// TODO (JMT) RDF Schema: data type formatters
			
			header = payload.addElement(columnName);
			header.addText(columnValue);
		}
		return payload;
	}
	
	/**
	 * get a row by specific cell info   
	 * 
	 * @param service
	 * @param worksheet
	 * @param columnTag : Tag name of the column contains the cell
	 * @param value : cell value
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static ListEntry getRow(SpreadsheetService service,
			WorksheetEntry worksheet, String columnTag, String value)
			throws IOException, ServiceException {
		ListFeed feed = service.getFeed(worksheet.getListFeedUrl(),
				ListFeed.class);

		for (ListEntry entry : feed.getEntries()) {
			if (entry.getCustomElements().getValue(columnTag).equals(value))
				return entry;
		}
		return null;
	}

	/**
	 * get a row by row index
	 * 
	 * @param service
	 * @param worksheet
	 * @param rowIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static ListEntry getRow(SpreadsheetService service,
			WorksheetEntry worksheet, int rowIndex)
			throws IOException, ServiceException {
		
	    ListQuery query = new ListQuery(worksheet.getListFeedUrl());
	    query.setStartIndex(rowIndex);
	    query.setMaxResults(1);

	    ListFeed feed = service.query(query, ListFeed.class);
	    
		if(feed.getEntries().size()>0)
			return feed.getEntries().get(0);
		else return null;	
	}	

	
	/**
	 * get a row by rowId
	 * http://spreadsheets.google.com/feeds/list/key/worksheetId/visibility/projection/rowId
	 * 
	 * @param service
	 * @param worksheet
	 * @param rowId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static ListEntry getRow(SpreadsheetService service,
			WorksheetEntry worksheet, String rowId) throws IOException,
			ServiceException {

		ListFeed feed = service.getFeed(worksheet.getListFeedUrl(),
				ListFeed.class);

		for (ListEntry row : feed.getEntries()) {
			if (row.getId().endsWith(rowId))
				return row;
		}
		return null;
	}	
	
	/**
	 * get a cell by row and column index 
	 * 
	 * @param service
	 * @param worksheet
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static CellEntry getCell(SpreadsheetService service,
			WorksheetEntry worksheet, int rowIndex, int colIndex)
			throws IOException, ServiceException {

		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setMinimumRow(rowIndex);
		query.setMaximumRow(rowIndex);
		query.setMinimumCol(colIndex);
		query.setMaximumCol(colIndex);
		CellFeed feed = service.query(query, CellFeed.class);

		if (feed.getEntries().size() > 0)
			return feed.getEntries().get(0);
		else
			return null;
	}	
	
	/**
	 * get cell by cellId
	 * http://spreadsheets.google.com/feeds/cells/key/worksheetId/visibility/projection/cellId
	 * 
	 * @param service
	 * @param worksheet
	 * @param cellId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static CellEntry getCell(SpreadsheetService service,
			WorksheetEntry worksheet, String cellId) throws IOException,
			ServiceException {

		CellFeed feed = service.getFeed(worksheet.getCellFeedUrl(),
				CellFeed.class);

		for (CellEntry cell : feed.getEntries()) {
			if (cell.getId().endsWith(cellId))
				return cell;
		}

		return null;
	}	
	
	
	/**
	 * get a cell from a row by column index
	 * 
	 * @param service
	 * @param worksheet
	 * @param row
	 * @param columnIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static CellEntry getCell(SpreadsheetService service,
			WorksheetEntry worksheet, ListEntry row, int columnIndex)
			throws IOException, ServiceException {
		ListFeed lFeed = service.getFeed(worksheet.getListFeedUrl(),
				ListFeed.class);
		int rowIndex = lFeed.getEntries().indexOf(row);

		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setMinimumRow(rowIndex);
		query.setMaximumRow(rowIndex);
		CellFeed cFeed = service.query(query, CellFeed.class);

		for (CellEntry entry : cFeed.getEntries()) {
			if (entry.getCell().getCol() == columnIndex)
				return entry;
		}

		return null;
	}
	
	
	/**
	 * get a cell by row and column index 
	 * 
	 * @param service
	 * @param worksheet
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static MJListEntry getMJRow(SpreadsheetService service,
			WorksheetEntry worksheet, int rowIndex)
			throws IOException, ServiceException {

		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setStartIndex(rowIndex);
		query.setMaxResults(1);
		ListFeed feed = service.query(query, ListFeed.class);

		MJListEntry mjRow = null;
		
		if (feed.getEntries().size() > 0){
			mjRow = new MJListEntry(feed.getEntries().get(0), rowIndex);
			mjRow.populateClild(service, worksheet);
		}
		
		return mjRow;
	}	

	/**
	 * get a cell by row and column index 
	 * 
	 * @param service
	 * @param worksheet
	 * @param rowIndex
	 * @param colIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static MJCellEntry getMJCell(SpreadsheetService service,
			WorksheetEntry worksheet, int rowIndex, int colIndex)
			throws IOException, ServiceException {

		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setMinimumRow(rowIndex);
		query.setMaximumRow(rowIndex);
		query.setMinimumCol(colIndex);
		query.setMaximumCol(colIndex);
		CellFeed feed = service.query(query, CellFeed.class);

		MJCellEntry mjCell = null;
		
		if (feed.getEntries().size() > 0){
			mjCell = new MJCellEntry(feed.getEntries().get(0),null);
			mjCell.populateParent(service, worksheet);
		}
		
		return mjCell;
	}	
	
	
	public static HSSFWorkbook getOrCreateWorkbookIfAbsent(String fileName) throws FileNotFoundException, IOException{
		HSSFWorkbook workbook = null;
		File file = new File(fileName);
		if(!file.exists()){
			workbook = new HSSFWorkbook();
		} else {
			workbook = new HSSFWorkbook(new FileInputStream(file));
		}
		return workbook;
	}
	
	public static HSSFSheet getOrCreateSheetIfAbsent(HSSFWorkbook workbook, String sheetName){
		HSSFSheet worksheet = null;
		if(workbook.getNumberOfSheets() == 0){
			worksheet = workbook.createSheet(sheetName);
		} else {
			worksheet = workbook.getSheet(sheetName);
			if(worksheet == null){
				worksheet = workbook.createSheet(sheetName);
			}
		}
		return worksheet;
	}
	
	public static HSSFRow getOrCreateRowHeaderIfAbsent(HSSFSheet worksheet){
		HSSFRow row = worksheet.getRow(0);
		if(row == null){
			row = worksheet.createRow(0);
		}
		return row;
	}
	
	@SuppressWarnings("unchecked")
	public static HSSFCell getOrCreateCellStringIfAbsent(HSSFRow row, String value){
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			HSSFCell cell = iterator.next();
			String cellValue = cell.getRichStringCellValue().getString();
			if(cellValue.equals(value)){
				return cell;
			}
		}
		
		HSSFCell cell = row.createCell(row.getPhysicalNumberOfCells(), HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(new HSSFRichTextString(value));
		return cell;
	}

	public static void updateOrCreateCellStringIfAbsent(HSSFRow row, int columnIndex, String value) {
		HSSFCell cell = row.getCell(columnIndex);
		if(cell == null){
			cell = row.createCell(columnIndex, HSSFCell.CELL_TYPE_STRING);
		}
		cell.setCellValue(new HSSFRichTextString(value));
	}

/*	@SuppressWarnings("unchecked")
	public static void updateRow(HSSFSheet worksheet, HSSFRow row, Element payload) {
		
		HSSFRow rowHeader = worksheet.getRow(0);
		HSSFCell cellHeader;
		
		Element child;
		for (Iterator<Element> iterator = payload.elementIterator(); iterator.hasNext();) {
			child = (Element) iterator.next();
			HSSFCell cell = getCell(worksheet, row, child.getName());
			if(cell == null){
				cellHeader = getOrCreateCellStringIfAbsent(rowHeader, child.getName());
				cell = row.createCell(cellHeader.getColumnIndex());
			}
			cell.setCellValue(new HSSFRichTextString(child.getText()));     // TODO (JMT) RDF Schema: data type formatters
		}
	}*/

	@SuppressWarnings("unchecked")
	public static boolean isPhantomRow(HSSFRow row) {
		if(row == null){
			return true;
		}
		
		for (Iterator<HSSFCell> iterator = row.cellIterator(); iterator.hasNext();) {
			HSSFCell cell = iterator.next();
			if(HSSFCell.CELL_TYPE_BLANK != cell.getCellType()){
				return false;
			}
		}
		return true;
	}
		
	/**
	 * get a spreadsheet entry by sheetID
	 * 
	 * @param factory
	 * @param service
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static SpreadsheetEntry getSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, String sheetId) throws IOException,
			ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		for (SpreadsheetEntry spreadsheet : feed.getEntries()) {
			if (spreadsheet.getId().substring(
					spreadsheet.getId().lastIndexOf("/") + 1).equals(sheetId))
				return spreadsheet;
		}
		return null;
	}

	/**
	 * get a Spreadsheet entry by index
	 * 
	 * @param factory
	 * @param service
	 * @param sheetIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static SpreadsheetEntry getSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, int sheetIndex) throws IOException,
			ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		return feed.getEntries().get(sheetIndex);
	}

	/**
	 * get specific worksheet by index form a spreadsheet
	 * 
	 * @param service
	 * @param spreadsheet
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static WorksheetEntry getWorksheet(SpreadsheetService service,
			SpreadsheetEntry spreadsheet, String sheetId) throws IOException,
			ServiceException {

		URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
		WorksheetFeed worksheetFeed = service.getFeed(worksheetFeedUrl,
				WorksheetFeed.class);
		for (WorksheetEntry worksheet : worksheetFeed.getEntries()) {
			if (worksheet.getId().substring(
					worksheet.getId().lastIndexOf("/") + 1).equals(sheetId))
				return worksheet;
		}

		return null;
	}

	/**
	 * get specific worksheet by index form a spreadsheet 
	 * 
	 * @param service
	 * @param spreadsheet
	 * @param sheetIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static WorksheetEntry getWorksheet(SpreadsheetService service,
			SpreadsheetEntry spreadsheet, int sheetIndex) throws IOException,
			ServiceException {
		URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
		WorksheetFeed worksheetFeed = service.getFeed(worksheetFeedUrl,
				WorksheetFeed.class);
		return worksheetFeed.getEntries().get(sheetIndex);
	}	
}
