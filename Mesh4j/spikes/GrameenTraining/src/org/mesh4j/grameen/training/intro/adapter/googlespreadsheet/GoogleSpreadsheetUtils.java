package org.mesh4j.grameen.training.intro.adapter.googlespreadsheet;


import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSBaseElement;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSCell;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSRow;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.grameen.training.intro.adapter.googlespreadsheet.model.IGSElement;
import org.mesh4j.sync.adapters.SyncInfo;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IIdGenerator;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.parsers.SyncInfoParser;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
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
 * this is the utility class used by Google spreadsheet adapter
 * @author sharif
 * version 1.0, 29/03/09
 *
 */
public class GoogleSpreadsheetUtils {

	@SuppressWarnings("unchecked")
	public static void flush(SpreadsheetService service, GSSpreadsheet<GSWorksheet> spreadsheet) {

		for (GSWorksheet<GSBaseElement> worksheet : spreadsheet
				.getChildElements().values()) {
			if (worksheet.isDirty()) {
				Map<String, GSBaseElement> updatePool = new LinkedHashMap<String, GSBaseElement>();
				Map<String, GSBaseElement> deletePool = new LinkedHashMap<String, GSBaseElement>();
				// used two pool because update and delete operation are
				// implemented differently

				processElementForFlush(worksheet, updatePool, deletePool);
				
				//process update pool
				if(updatePool.size() > 0 || deletePool.size() > 0){
					
					try {
						CellFeed batchRequest = new CellFeed();
						for (GSBaseElement elementToUpdate : updatePool.values()) {
							
							GSCell cellToUpdate = (GSCell) elementToUpdate;
							BatchUtils.setBatchId(cellToUpdate.getCellEntry(), cellToUpdate
									.getCellEntry().getId());
							BatchUtils.setBatchOperationType(cellToUpdate.getCellEntry(),
									BatchOperationType.UPDATE);
							
							batchRequest.getEntries().add(
									cellToUpdate.getCellEntry());
						} 
						// Submit the batch request.
						CellFeed feed = service.getFeed(worksheet.getWorksheetEntry().getCellFeedUrl(), CellFeed.class);
					    Link batchLink = feed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
						
						CellFeed batchResultFeed = service.batch(new URL(batchLink.getHref()), batchRequest);			
			
						// Make sure all the operations were successful.
						for (CellEntry entry : batchResultFeed.getEntries()) {
						  String batchId = BatchUtils.getBatchId(entry);
						  if (!BatchUtils.isSuccess(entry)) {
						    BatchStatus status = BatchUtils.getBatchStatus(entry);
						    String errorMsg = "Failed entry \t" + batchId + " failed (" + status.getReason() + ") " + status.getContent();
						    System.err.println(errorMsg);
						    throw new MeshException(new Exception(errorMsg));
						    //TODO: Need to enhance the exception handling codes
						    //TODO: Need to think about roll-back mechanism for partial update if such happens
						  }	
						}
						
					}catch (Exception e) {
						throw new MeshException(e);
					}finally{
					}
					
					
					//process delete pool
					for (GSBaseElement elementToDetete : deletePool.values()) {
						
						try {
							elementToDetete.getBaseEntry().delete();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ServiceException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				} // if(updatePool.size() > 0 || deletePool.size() > 0)
				
			}// if (worksheet.isDirty())
		}//end for
	}


	/**
	 * iterate over the whole object graph to identify changed elements of the
	 * spreadsheet file and transfer them to corresponding pool for
	 * update/delete operation
	 * 
	 * @param element
	 * @param deletePool 
	 * @param updatePool 
	 */
	@SuppressWarnings("unchecked")
	private static void processElementForFlush(GSBaseElement<GSBaseElement> element,
			Map<String, GSBaseElement> updatePool,
			Map<String, GSBaseElement> deletePool) {
		for (GSBaseElement subElement : element.getChildElements().values()) {
			if (subElement.isDirty()) {
				if (subElement.isDeleteCandiddate()) {
					// add subElement to delete pool
					deletePool.put(subElement.getId(), subElement);
				} else {
					if (subElement instanceof GSCell) {
						// add subElement to update pool
						updatePool.put(subElement.getId(), subElement);
					} else
						processElementForFlush(subElement, updatePool, deletePool);
				}
			}
		}

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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
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
	@Deprecated
	public static GSRow getGSRow(SpreadsheetService service,
			WorksheetEntry worksheet, int rowIndex)
			throws IOException, ServiceException {

		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setStartIndex(rowIndex-1);
		query.setMaxResults(1);
		ListFeed feed = service.query(query, ListFeed.class);

		GSRow gsListEntry = null;
		
		if (feed.getEntries().size() > 0){
			gsListEntry = new GSRow(feed.getEntries().get(0), rowIndex);
			gsListEntry.populateClild(/*service,*/ worksheet);
		}
		
		return gsListEntry;
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
	@Deprecated
	public static GSCell getGSCell(SpreadsheetService service,
			WorksheetEntry worksheet, int rowIndex, int colIndex)
			throws IOException, ServiceException {

		CellQuery query = new CellQuery(worksheet.getCellFeedUrl());
		query.setMinimumRow(rowIndex);
		query.setMaximumRow(rowIndex);
		query.setMinimumCol(colIndex);
		query.setMaximumCol(colIndex);
		CellFeed feed = service.query(query, CellFeed.class);

		GSCell gsCellEntry = null;
		
		if (feed.getEntries().size() > 0){
			gsCellEntry = new GSCell(feed.getEntries().get(0),null);
			gsCellEntry.populateParent(/*service,*/ worksheet);
		}
		
		return gsCellEntry;
	}	
	
	
/*	public static HSSFWorkbook getOrCreateWorkbookIfAbsent(String fileName) throws FileNotFoundException, IOException{
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

	@SuppressWarnings("unchecked")
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
	}

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
	
*/
	
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
	@Deprecated
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
	@Deprecated
	public static SpreadsheetEntry getSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, int sheetIndex) throws IOException,
			ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		return feed.getEntries().get(sheetIndex);
	}	
	
	/**
	 * get a custom Spreadsheet entry by index
	 * 
	 * @param factory
	 * @param service
	 * @param sheetIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static GSSpreadsheet getGSSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, int sheetIndex) throws IOException,
			ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		GSSpreadsheet<GSWorksheet> gsSpreadsheet = null;
		
		//pickup the specific spreadsheet and build a custom spreadsheet object
		if(feed.getEntries().size() >= sheetIndex)
			gsSpreadsheet = new GSSpreadsheet<GSWorksheet>(feed.getEntries().get(sheetIndex));
		
		return getGSSpreadsheet( factory,
				 service,  gsSpreadsheet);		
	}

	
	/**
	 * get a custom spreadsheet entry by sheetID
	 * 
	 * @param factory
	 * @param service
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static GSSpreadsheet getGSSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, String sheetId) throws IOException,
			ServiceException {
	
		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		GSSpreadsheet<GSWorksheet> gssSpreadsheet = null;
		
		//pickup the specific spreadsheet and build a custom spreadsheet object
		for (SpreadsheetEntry ss : feed.getEntries()) {
			if (ss.getId().substring(ss.getId().lastIndexOf("/") + 1).equals(
					sheetId)) {
				gssSpreadsheet = new GSSpreadsheet<GSWorksheet>(ss);
				break;
			}
		}
		
		return getGSSpreadsheet( factory,
				 service,  gssSpreadsheet);
	}
	
	
	/**
	 * get a spreadsheet entry by sheetID
	 * its takes 3 http request to populate the whole spreadsheet in our custom object structure
	 * 
	 * @param factory
	 * @param service
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static GSSpreadsheet getGSSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, GSSpreadsheet<GSWorksheet> gsSpreadsheet) throws IOException,
			ServiceException {		
		
		//get all worksheets from the spreadsheet
		List<WorksheetEntry> wsList = getAllWorksheet(service, gsSpreadsheet.getSpreadsheet()); //1 http request
		
		for(WorksheetEntry ws: wsList){
			//create a custom worksheet object 
			GSWorksheet<GSRow> gsWorksheet = new GSWorksheet<GSRow>(
					ws, wsList.indexOf(ws) + 1, gsSpreadsheet);
			
			List<ListEntry> rowList = getAllRows(service, ws); //1 http request
			List<CellEntry> cellList = getAllCells(service, ws); //1 http request
			
			if( rowList.size() > 0 && cellList.size() > 0 ){
				//get the header row and put it as the 1st row in the rowlist
				GSRow<GSCell> gsListHeaderEntry = new GSRow(
						new ListEntry(), 1, gsWorksheet);
				gsListHeaderEntry.populateClild(cellList);				
				gsWorksheet.getChildElements().put(Integer.toString(gsListHeaderEntry.getRowIndex()), gsListHeaderEntry);			
				
				for (ListEntry row : rowList){
					//create a custom row object and populate its child
					GSRow<GSCell> gsListEntry = new GSRow(
							row, rowList.indexOf(row) + 2, gsWorksheet); //+2 because #1 position is occupied by list header entry 
					gsListEntry.populateClild(cellList);				
					
					//add a row to the custom worksheet object
					gsWorksheet.getChildElements().put(Integer.toString(gsListEntry.getRowIndex()), gsListEntry);
					//TODO: right now index has been used as key; mjrow.getId() could have used, this need to review
				}
			} // if
			//add a custom worksheet object to the custom spreadsheet object 
			gsSpreadsheet.getChildElements().put(Integer.toString(gsWorksheet.getSheetIndex()),gsWorksheet);
		} //for		
		
		return gsSpreadsheet;
	}
	
	
	/**
	 * get all worksheet form a spreadsheet
	 * 
	 * @param service
	 * @param spreadsheet
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<WorksheetEntry> getAllWorksheet(SpreadsheetService service,
			SpreadsheetEntry spreadsheet) throws IOException,
			ServiceException {

		Guard.argumentNotNull(spreadsheet, "spreadsheet");

		URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
		WorksheetFeed worksheetFeed = service.getFeed(worksheetFeedUrl,
				WorksheetFeed.class);
		return worksheetFeed.getEntries();
	}
	

	/**
	 * get all rows form a worksheet
	 * 
	 * @param service
	 * @param spreadsheet
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<ListEntry> getAllRows(SpreadsheetService service,
			WorksheetEntry worksheet) throws IOException,
			ServiceException {

		Guard.argumentNotNull(worksheet, "worksheet");

		URL listFeedUrl = worksheet.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl,
				ListFeed.class);
		return listFeed.getEntries();
	}

	
	/**
	 * get all cells  form a worksheet
	 * 
	 * @param service
	 * @param spreadsheet
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<CellEntry> getAllCells(SpreadsheetService service,
			WorksheetEntry worksheet) throws IOException,
			ServiceException {

		Guard.argumentNotNull(worksheet, "worksheet");

		URL cellFeedUrl = worksheet.getCellFeedUrl();
		CellFeed cellFeed = service.getFeed(cellFeedUrl,
				CellFeed.class);
		return cellFeed.getEntries();
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
	@Deprecated
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
	@Deprecated
	public static WorksheetEntry getWorksheet(SpreadsheetService service,
			SpreadsheetEntry spreadsheet, int sheetIndex) throws IOException,
			ServiceException {
		URL worksheetFeedUrl = spreadsheet.getWorksheetFeedUrl();
		WorksheetFeed worksheetFeed = service.getFeed(worksheetFeedUrl,
				WorksheetFeed.class);
		return worksheetFeed.getEntries().get(sheetIndex);
	}
	
	public Date getDate(String dateString){
		
		return null;
	}
	
	public static GSRow getRow(GSWorksheet<GSRow<GSCell>> worksheet,int columnIndex,String cellValue){
		GSRow<GSCell> row ;
		for(Map.Entry<String, GSRow<GSCell>> mpRow : worksheet.getGSRows().entrySet()){
			 row = mpRow.getValue();
			if(row.getGSCells().size()>0){
				GSCell cell = row.getGSCell(columnIndex);
				String cellContentAsString = cell.getCellEntry().getCell().getValue();
				if(cellContentAsString != null && !cellContentAsString.equals("")){
					if(cellContentAsString.equals(cellValue)){
						//comparision is successful so, the desired sync info row
						//is current row
						 return row;
					}
				} 
			}
		}
		return null;
	}
	
	
}
