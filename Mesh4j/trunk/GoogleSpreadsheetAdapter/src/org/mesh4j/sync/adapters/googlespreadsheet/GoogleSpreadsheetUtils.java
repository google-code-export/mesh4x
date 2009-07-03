package org.mesh4j.sync.adapters.googlespreadsheet;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSBaseElement;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSCell;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSRow;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSSpreadsheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.GSWorksheet;
import org.mesh4j.sync.adapters.googlespreadsheet.model.IGSElement;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.payload.schema.rdf.RDFSchema;
import org.mesh4j.sync.utils.FileUtils;
import org.mesh4j.sync.validations.Guard;
import org.mesh4j.sync.validations.MeshException;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.ListQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.BaseEntry;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.DocumentListEntry.MediaType;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.data.spreadsheet.ListEntry;
import com.google.gdata.data.spreadsheet.ListFeed;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.data.spreadsheet.WorksheetFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * this is the utility class used by Google spreadsheet adapter
 * 
 * @author sharif version 1.0, 29/03/09
 * 
 */
public class GoogleSpreadsheetUtils {

	private final static Log log = LogFactory.getLog(GoogleSpreadsheetUtils.class);
	
	public static final String DOC_FEED_URL = "http://docs.google.com/feeds/documents/private/full";
	public static final String TMP_FILE_DIR = "/";
	public static final String DEFAULT_NEW_WORKSHEET_NAME = "New Worksheet";
	public static final String DEFAULT_NEW_SPREADSHEET_NAME = "New Spreadsheet";
	
	static final int SPREADSHEET_STATUS_SPREADSHEET_NONE = -1;
	static final int SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_NO = 0;
	static final int SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_YES = 1;
	static final int SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO = 2;
	static final int SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_YES = 3;

	@SuppressWarnings("unchecked")
	public static void flush(SpreadsheetService service,
			GSSpreadsheet<GSWorksheet> spreadsheet) {

		for (GSWorksheet<GSBaseElement> worksheet : spreadsheet
				.getChildElements().values()) {
			if (worksheet.isDirty()) {
				Map<String, GSBaseElement> insertPool = new LinkedHashMap<String, GSBaseElement>();
				Map<String, GSBaseElement> updatePool = new LinkedHashMap<String, GSBaseElement>();
				Map<String, GSBaseElement> deletePool = new LinkedHashMap<String, GSBaseElement>();
				// used two pool because update and delete operation are
				// implemented differently

				processElementForFlush(worksheet, insertPool, updatePool,
						deletePool);

				if (insertPool.size() > 0 || updatePool.size() > 0
						|| deletePool.size() > 0) {

					// process insert/update pool (row/sheet)
					for (GSBaseElement elementToInsert : insertPool.values()) {

						URL feedUrl = null;

						if (elementToInsert instanceof GSCell) {
							feedUrl = worksheet.getWorksheetEntry()
									.getCellFeedUrl();
						} else if (elementToInsert instanceof GSRow) {
							feedUrl = worksheet.getWorksheetEntry()
									.getListFeedUrl();
						} else if (elementToInsert instanceof GSWorksheet) {
							feedUrl = ((SpreadsheetEntry) spreadsheet
									.getBaseEntry()).getWorksheetFeedUrl();
						}

						// if(elementToInsert instanceof GSRow){
						BaseEntry newEntry = elementToInsert.getBaseEntry();
						try {
							BaseEntry le = null;
							if (newEntry.getId() == null)
								le = service.insert(feedUrl, newEntry);
							else
								le = elementToInsert.getBaseEntry().update();

							elementToInsert.setBaseEntry(le);

							// refresh this but not its childs!
							elementToInsert.unsetDirty(false);
							elementToInsert.refreshMe();

						} catch (Exception e) {
							throw new MeshException(e);
						}
					}

					// process batch update pool
					try {
						CellFeed batchRequest = new CellFeed();
						for (GSBaseElement elementToUpdate : updatePool
								.values()) {

							GSCell cellToUpdate = (GSCell) elementToUpdate;
							BatchUtils.setBatchId(cellToUpdate.getCellEntry(),
									cellToUpdate
									/* .getCellEntry() */.getId());
							BatchUtils.setBatchOperationType(cellToUpdate
									.getCellEntry(), BatchOperationType.UPDATE);

							batchRequest.getEntries().add(
									cellToUpdate.getCellEntry());
						}

						if (updatePool.size() > 0) {
							// Submit the batch request.
							CellFeed feed = service.getFeed(worksheet
									.getWorksheetEntry().getCellFeedUrl(),
									CellFeed.class);
							Link batchLink = feed.getLink(Link.Rel.FEED_BATCH,
									Link.Type.ATOM);

							CellFeed batchResultFeed = service.batch(new URL(
									batchLink.getHref()), batchRequest);

							// Make sure all the operations were successful.
							for (CellEntry entry : batchResultFeed.getEntries()) {
								String batchId = BatchUtils.getBatchId(entry);
								if (!BatchUtils.isSuccess(entry)) {
									BatchStatus status = BatchUtils
											.getBatchStatus(entry);
									String errorMsg = "Failed entry \t"
											+ batchId + " failed ("
											+ status.getReason() + ") "
											+ status.getContent();
									throw new MeshException(new Exception(
											errorMsg));
									// TODO: Need to enhance the exception
									// handling codes
									// TODO: Need to think about roll-back
									// mechanism for partial update if such
									// happens
								}
							}

							// update succeed, so mark the cells not dirty
							for (GSBaseElement elementToUpdate : updatePool
									.values()) {
								elementToUpdate.unsetDirty();
							}
						}

					} catch (Exception e) {
						throw new MeshException(e);
					} finally {
					}

					// process delete pool
					for (GSBaseElement elementToDetete : deletePool.values()) {

						try {
							if (elementToDetete.getBaseEntry().getId() != null) 
								// entry physically exists in the spreadsheet file
								elementToDetete.getBaseEntry().delete();
						} catch (Exception e) {
							throw new MeshException(e);
						} 

					}

				} // if(updatePool.size() > 0 || deletePool.size() > 0)

				// this will remove the deleted childs (if any) in memory and
				// update list index of remaining childs
				worksheet.refreshMe();
				worksheet.unsetDirty();

			}// if (worksheet.isDirty())
		}// end for
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
	private static void processElementForFlush(
			GSBaseElement<GSBaseElement> element,
			Map<String, GSBaseElement> insertPool,
			Map<String, GSBaseElement> updatePool,
			Map<String, GSBaseElement> deletePool) {

		if (element.isDeleteCandidate())
			deletePool.put(element.getId(), element);
		else {

			if (element.isDirty() && element instanceof GSWorksheet)
				insertPool.put(element.getId(), element);

			for (GSBaseElement subElement : element.getChildElements().values()) {
				if (subElement.isDirty()) {
					if (subElement.isDeleteCandidate()) {
						// add subElement to delete pool
						deletePool.put(subElement.getId(), subElement);
					} else {
						if (subElement instanceof GSCell) {
							// add subElement to update pool
							if (subElement.getBaseEntry().getId() != null)
								updatePool.put(subElement.getBaseEntry()
										.getId(), subElement);
							else
								insertPool.put(subElement.getId(), subElement);
						} else if (subElement instanceof GSRow
								&& subElement.getElementListIndex() > 1) {
							if (subElement.getBaseEntry().getId() == null) {
								// only for new rows
								insertPool.put(subElement.getId(), subElement);
							} else {
								// if all childs are new but row has an ID
								// then add it to insert pool ()
								boolean eligible = true;
								for (IGSElement child : ((GSRow<GSCell>) subElement)
										.getChildElements().values()) {
									if (child.isDirty()
											&& child.getBaseEntry().getId() != null) {
										eligible = false;
										break;
									}
								}
								if (eligible)
									insertPool.put(subElement.getElementId(),
											subElement);
								else
									processElementForFlush(subElement,
											insertPool, updatePool, deletePool);
							}
						} else
							processElementForFlush(subElement, insertPool,
									updatePool, deletePool);
					}
				}
			}
		}
	}

	/**
	 * 
	 * get a {@link ListEntry} object from feed by http request
	 * 
	 * @param worksheet
	 * @param rowIndex
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static ListEntry getListEntryFromFeed(WorksheetEntry worksheet,
			int rowIndex) throws IOException, ServiceException {

		ListQuery query = new ListQuery(worksheet.getListFeedUrl());
		query.setStartIndex(rowIndex - 1);
		query.setMaxResults(1);

		ListFeed feed = worksheet.getService().query(query, ListFeed.class);

		if (feed.getEntries().size() > 0)
			return feed.getEntries().get(0);
		else
			return null;
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
	@SuppressWarnings("unchecked")
	public static GSSpreadsheet getGSSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, int sheetIndex) throws IOException,
			ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		GSSpreadsheet<GSWorksheet> gsSpreadsheet = null;

		// pickup the specific spreadsheet and build a custom spreadsheet object
		if (feed.getEntries().size() >= sheetIndex)
			gsSpreadsheet = new GSSpreadsheet<GSWorksheet>(feed.getEntries()
					.get(sheetIndex));

		return getGSSpreadsheet(factory, service, gsSpreadsheet);
	}

	/**
	 * get a custom spreadsheet entry by sheetID
	 * 
	 * @param factory
	 * @param service
	 * @param spreadsheetName
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static GSSpreadsheet getGSSpreadsheet(FeedURLFactory factory,
			SpreadsheetService service, String spreadsheetName) throws IOException,
			ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		GSSpreadsheet<GSWorksheet> gssSpreadsheet = null;

		// pickup the specific spreadsheet and build a custom spreadsheet object
		for (SpreadsheetEntry ss : feed.getEntries()) {
			if (ss.getTitle().getPlainText().equals(
					spreadsheetName)) {
				gssSpreadsheet = new GSSpreadsheet<GSWorksheet>(ss);
				break;
			}
		}

		if (gssSpreadsheet == null)
			throw new MeshException("No spreadsheet available with the name '"
					+ spreadsheetName + "'");
			
		return getGSSpreadsheet(factory, service, gssSpreadsheet);
	}

	/**
	 * get a spreadsheet entry by sheetID its takes 3 http request to populate
	 * the whole spreadsheet in our custom object structure
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
			SpreadsheetService service, GSSpreadsheet<GSWorksheet> gsSpreadsheet)
			throws IOException, ServiceException {

		// get all worksheets from the spreadsheet
		List<WorksheetEntry> wsList = getAllWorksheet(service, gsSpreadsheet
				.getSpreadsheet()); // 1 http request

		for (WorksheetEntry ws : wsList) {
			// create a custom worksheet object
			GSWorksheet<GSRow> gsWorksheet = new GSWorksheet<GSRow>(ws, wsList
					.indexOf(ws) + 1, gsSpreadsheet);

			List<ListEntry> rowList = getAllRows(ws); // 1 http request
			List<CellEntry> cellList = getAllCells(ws); // 1 http request

			if (cellList.size() > 0) {
				// get the header row and put it as the 1st row in the rowlist
				GSRow<GSCell> gsListHeaderEntry = new GSRow(new ListEntry(), 1,
						gsWorksheet);
				gsListHeaderEntry.populateClildWithHeaderTag(cellList, gsWorksheet);
				gsWorksheet.getChildElements().put(
						gsListHeaderEntry.getElementId(), gsListHeaderEntry);

				for (ListEntry row : rowList) {
					// create a custom row object and populate its child
					GSRow<GSCell> gsListEntry = new GSRow(row, rowList
							.indexOf(row) + 2, gsWorksheet); // +2 because #1
																// position is
																// occupied by
																// list header
																// entry
					gsListEntry.populateClildWithHeaderTag(cellList, gsWorksheet);

					// add a row to the custom worksheet object
					gsWorksheet.getChildElements().put(
							gsListEntry.getElementId(), gsListEntry);
					// TODO: right now index has been used as key; mjrow.getId()
					// could have used, this need to review
				}
			} // if
			// add a custom worksheet object to the custom spreadsheet object
			gsSpreadsheet.getChildElements().put(gsWorksheet.getElementId(),
					gsWorksheet);
		} // for

		return gsSpreadsheet;
	}

	
	/**
	 * 
	 * @param factory
	 * @param service
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	public static List<SpreadsheetEntry> getAllSpreadsheet(
			FeedURLFactory factory, SpreadsheetService service)
			throws IOException, ServiceException {

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		return feed.getEntries();
	}
	
	public static List<SpreadsheetEntry> getAllSpreadsheet(String username,
			String password) throws IOException, ServiceException {
		FeedURLFactory factory = getSpreadsheetFeedURLFactory();
		SpreadsheetService service = getSpreadsheetService(username, password);

		SpreadsheetFeed feed = service.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		return feed.getEntries();
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
	public static List<WorksheetEntry> getAllWorksheet(
			SpreadsheetService service, SpreadsheetEntry spreadsheet)
			throws IOException, ServiceException {

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
	 * 
	 *             use getAllRows({@link ListEntry}) instead
	 */
	public static List<ListEntry> getAllRows(WorksheetEntry worksheet)
			throws IOException, ServiceException {

		Guard.argumentNotNull(worksheet, "worksheet");

		URL listFeedUrl = worksheet.getListFeedUrl();
		ListFeed listFeed = worksheet.getService().getFeed(listFeedUrl,
				ListFeed.class);
		return listFeed.getEntries();
	}

	/**
	 * get all cells form a worksheet
	 * 
	 * @param service
	 * @param spreadsheet
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 * 
	 *             use getAllCells({@link WorksheetEntry}) instead
	 */
	public static List<CellEntry> getAllCells(WorksheetEntry worksheet)
			throws IOException, ServiceException {

		Guard.argumentNotNull(worksheet, "worksheet");

		URL cellFeedUrl = worksheet.getCellFeedUrl();
		CellFeed cellFeed = worksheet.getService().getFeed(cellFeedUrl,
				CellFeed.class);
		return cellFeed.getEntries();
	}

	public static GSRow<GSCell> getRow(GSWorksheet<GSRow<GSCell>> worksheet, int columnIndex, String cellValue) {
		GSRow<GSCell> row;
		for (Map.Entry<String, GSRow<GSCell>> mpRow : worksheet.getGSRows().entrySet()) {
			row = mpRow.getValue();
			if (row.getGSCells().size() > 0) {
				GSCell cell = row.getGSCell(columnIndex);
				String cellContentAsString = cell.getCellValue();
				if (cellContentAsString != null
						&& !cellContentAsString.equals("")) {
					if (cellContentAsString.equals(cellValue)) {
						// comparison is successful so, the desired row the is current row
						return row;
					}
				}
			}
		}
		return null;
	}
	
	public static GSRow<GSCell> getRow(GSWorksheet<GSRow<GSCell>> worksheet, String[] columnNames, String[] cellValues) {
		GSRow<GSCell> row;
		for (Map.Entry<String, GSRow<GSCell>> mpRow : worksheet.getGSRows().entrySet()) {
			row = mpRow.getValue();
			if (row.getGSCells().size() > 0) {
				int ok = 0;
				for (int j = 0; j < columnNames.length; j++) {
					String columnName = columnNames[j];
					GSCell cell = row.getGSCell(columnName);
					if(cell != null){
						String cellContentAsString = cell.getCellValue();
						if (cellContentAsString != null && !cellContentAsString.equals("")) {
							if(cellValues[j].equals(cellContentAsString)){
								ok = ok +1;
							}
						}
					} 
				}
				if(ok == columnNames.length){
					return row;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param gsRow
	 * @param cellValue
	 * @return
	 * @author Raju
	 */
	public static GSCell getCell(GSRow<GSCell> gsRow, String ColumName) {
		for (Map.Entry<String, GSCell> mapCell : gsRow.getGSCells().entrySet()) {
			GSCell cell = mapCell.getValue();
			if (cell.getCellValue().equals(ColumName)) {
				return cell;
			}
		}
		return null;
	}

	public static Date normalizeDate(String dateAsString, String format) {
		Guard.argumentNotNull(dateAsString, "dateAsString");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date dateAndTime;
		try {
			dateAndTime = simpleDateFormat.parse(dateAsString);
		} catch (ParseException e) {
			throw new MeshException(e);
		}
		return dateAndTime;
	}

	/**
	 * get sync sheet if available, otherwise create a new sync sheet and return that
	 * 
	 * @param spreadsheet
	 * @param syncWorksheetName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static GSWorksheet<GSRow<GSCell>> getOrCreateSyncSheetIfAbsent(
			GSSpreadsheet<GSWorksheet> spreadsheet, String syncWorksheetName) {

		GSWorksheet<GSRow<GSCell>> syncWorksheet = spreadsheet
				.getGSWorksheetBySheetName(syncWorksheetName);
		if (isValidSyncSheet(syncWorksheet))
			return syncWorksheet;
		else {
			if (syncWorksheet != null)
				spreadsheet.deleteChildElement(String.valueOf(syncWorksheet
						.getElementListIndex()));

			syncWorksheet = getOrCreateWorkSheetIfAbsent(spreadsheet,
					syncWorksheetName);

			GSRow<GSCell> headerRow = new GSRow<GSCell>(new ListEntry(), 1,
					syncWorksheet);
			syncWorksheet.addChildElement(headerRow.getElementId(), headerRow);

			for (SyncColumn sc : SyncColumn.values()) {
				CellEntry newCellEntry = new CellEntry(headerRow.getRowIndex(),
						sc.ordinal() + 1, sc.name());

				GSCell newGsCell = new GSCell(newCellEntry, headerRow, sc
						.toString());
				headerRow.addChildElement(sc.name(), newGsCell);
			}
		}

		return syncWorksheet;
	}	
	
	private static boolean isValidSyncSheet(
			GSWorksheet<GSRow<GSCell>> syncWorksheet) {
		if (syncWorksheet == null || syncWorksheet.getChildElements() == null)
			return false;
		if (syncWorksheet.getChildElements().size() >= 1) {
			GSRow<GSCell> hederRow = syncWorksheet.getGSRow(1);
			if (hederRow.getChildElements().size() == SyncColumn.values().length) {
				for (SyncColumn sc : SyncColumn.values()) {
					GSCell cell = hederRow.getGSCell(/*sc.toString()*/sc.name());
					if (cell == null)
						return false;
				}
			}else
				return false;
		}else
			return false;
		return true;
	}


	/**
	 * get content sheet if available, otherwise create a new sync sheet using supplied mapping and return that
	 * 
	 * @param spreadsheet
	 * @param syncWorksheetName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static GSWorksheet<GSRow<GSCell>> getOrCreateContentSheetIfAbsent(
			GSSpreadsheet<GSWorksheet> spreadsheet,	IGoogleSpreadsheetToXMLMapping mapper) {

		GSWorksheet<GSRow<GSCell>> contentWorksheet = spreadsheet
				.getGSWorksheetBySheetName(mapper.getType());
		
		//TODO: check for and empty worksheet that can be use for this purpose by renaming
		if(contentWorksheet == null){
			for(Map.Entry<String, GSWorksheet> wsMap : spreadsheet.getGSWorksheets().entrySet()){
				GSWorksheet gsw = wsMap.getValue();
				if (gsw.getChildElements().size() == 0 && gsw.getBaseEntry().getTitle().getPlainText().equalsIgnoreCase("Sheet1")){
					gsw.getBaseEntry().setTitle(new PlainTextConstruct(mapper.getType()));
					contentWorksheet = gsw;
					break;
				}
			}
		}
	
		if (mapper.getSchema() == null) {
			if(contentWorksheet == null)
				throw new MeshException(new Exception("Unable to create content sheet because no schema available"));
			else
				return contentWorksheet;
		}
		
		if (isValidContentSheet(contentWorksheet, (RDFSchema) mapper.getSchema()))
			return contentWorksheet;
		else {
			if (contentWorksheet != null){
				//TODO:(Sharif) need to review, 
				//if the worksheet is empty rename to mapper.getType() and use it
				//else rename the worksheet to a different name or get confirmation from user to delete it 
//				spreadsheet.deleteChildElement(String.valueOf(contentWorksheet
//						.getElementListIndex()));
				if(contentWorksheet.getChildElements().size() > 0){
					contentWorksheet.getBaseEntry().setTitle(new PlainTextConstruct(contentWorksheet.getBaseEntry().getTitle().getPlainText()+"_bak"));
					contentWorksheet.setDirty();
				}	
			
			}

			contentWorksheet = getOrCreateWorkSheetIfAbsent(spreadsheet, mapper.getType());

			RDFSchema rdfSchema = (RDFSchema) mapper.getSchema();

			GSRow<GSCell> headerRow = GoogleSpreadsheetUtils.getOrCreateHeaderRowIfAbsent(contentWorksheet);

			int size = rdfSchema.getPropertyCount();
			for (int i = size-1; i >= 0; i--) {
				String propertyName = rdfSchema.getPropertyName(i);
				if (propertyName != null && !propertyName.isEmpty()) {
					GoogleSpreadsheetUtils.getOrCreateHeaderCellIfAbsent(headerRow, propertyName);
				}
			}
		}
		return contentWorksheet;
	}		
	
	public static boolean isValidContentSheet(
			GSWorksheet<GSRow<GSCell>> contentWorksheet, RDFSchema rdfSchema) {
		if (contentWorksheet == null || contentWorksheet.getChildElements() == null)
			return false;
		
		if (contentWorksheet.getChildElements().size() >= 1) {
			GSRow<GSCell> hederRow = contentWorksheet.getGSRow(1);
			int propertyCount = rdfSchema.getPropertyCount();
			
			if (hederRow.getChildElements().size() == propertyCount) {
				for (int i = 0; i < propertyCount; i++) {
					String propertyName = rdfSchema.getPropertyName(i);
					if(propertyName!=null && !propertyName.isEmpty()){
						GSCell cell = hederRow.getGSCell(propertyName);
						if (cell == null) return false;
					}
				}
			}else
				return false;
		}else
			return false;
		return true;
	}	

	/**
	 * this will return a spreadsheet with id sheetId, if no such spreadsheet is
	 * available, it will create a sample spreadsheet and then upload it
	 * 
	 * @param factory
	 * @param service
	 * @param docService
	 * @param sheetId
	 * @return
	 * @throws IOException
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static GSSpreadsheet getOrCreateGSSpreadsheetIfAbsent(
			FeedURLFactory factory, SpreadsheetService spreadsheetService,
			DocsService docsService, String spreadsheetName) throws IOException,
			ServiceException {

		Guard.argumentNotNull(docsService,	"docsService");
		Guard.argumentNotNull(spreadsheetService, "spreadsheetService");
		Guard.argumentNotNull(factory, "factory");
		Guard.argumentNotNullOrEmptyString(spreadsheetName, "spreadsheetName");
		
		SpreadsheetFeed feed = spreadsheetService.getFeed(
				factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);

		GSSpreadsheet<GSWorksheet> gssSpreadsheet = null;

		// pickup the specific spreadsheet and build a custom spreadsheet object
		for (SpreadsheetEntry ss : feed.getEntries()) {
			if (ss.getTitle().getPlainText().equals(spreadsheetName)) {
				gssSpreadsheet = new GSSpreadsheet<GSWorksheet>(ss);
				break;
			}
		}

		if (gssSpreadsheet == null) {
			// if there is no spreadsheet with the specified id
			// create a blank spreadsheet and upload
			String newSpreadsheetName = createNewSpreadsheetDocAndUpload(spreadsheetName, docsService);

			log.info("New uploaded spreadsheet name: "+ newSpreadsheetName);
			
			int count = 0;
			while(gssSpreadsheet == null && count <= 5){
			
				feed = spreadsheetService.getFeed(factory.getSpreadsheetsFeedUrl(),
						SpreadsheetFeed.class);
				
				log.info("Listing of available spreadsheet...");
				
				for (SpreadsheetEntry ss : feed.getEntries()) {	
					log.info(">> "+ss.getTitle().getPlainText());
					if (ss.getTitle().getPlainText().equals(newSpreadsheetName)) {
						gssSpreadsheet = new GSSpreadsheet<GSWorksheet>(ss);
						break;
					}
				}
				
				if(gssSpreadsheet == null){
					log.info("/n-----------------");
					log.info("cann't access the newly uploaded spreadsheet '"+newSpreadsheetName+"'...trying agin after 5 seconds...");
					log.info("-----------------/n");
					try {
						Thread.sleep(5000);
						count++;
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
						throw new MeshException(e);
					}
				}
			}
			
		}
		
		return getGSSpreadsheet(factory, spreadsheetService, gssSpreadsheet);
	}

	/**
	 * Create a sample spreadsheet and upload it
	 * 
	 * @param spreadsheetFilename
	 * @param docService
	 * @return
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public static String createNewSpreadsheetDocAndUpload(String spreadsheetFilename,
			DocsService docService) throws IOException, ServiceException {
		Guard.argumentNotNullOrEmptyString(spreadsheetFilename, "fileName");
		Guard.argumentNotNull(docService, "docService");
		
		String localFileName = FileUtils.getResourceFileURL("default.xls").getFile();
		File documentFileForUpload = new File(localFileName);
		
		if (!documentFileForUpload.exists()) {
			throw new MeshException("Error in loading default spreadsheet file for upload...");
		}
		
		return uploadSpreadsheetDoc(spreadsheetFilename, documentFileForUpload, docService);
	}

	/**
	 * Upload a document in google docs
	 * 
	 * @param documentFile
	 * @param docService
	 * @return
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	public static String uploadSpreadsheetDoc(String spreadsheetFilename, File documentFile,
			DocsService docService) throws IOException, ServiceException {

		URL documentListFeedUrl = new URL(DOC_FEED_URL);
		DocumentEntry newDocument = new DocumentEntry();
		newDocument.setFile(documentFile, MediaType.XLS.getMimeType());
		
		//if the document with same title already exists add a number prefix to make it unique
		DocumentListFeed feed = docService.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
        	if(entry.getTitle().getPlainText().equals(spreadsheetFilename)){
        		spreadsheetFilename = spreadsheetFilename + "_" +IdGenerator.INSTANCE.newID().substring(0,8);
        	}	
        }
        
		newDocument.setTitle(new PlainTextConstruct(spreadsheetFilename));
		
		DocumentListEntry uploadedDocument = null;
		try {
			uploadedDocument = docService.insert(documentListFeedUrl, newDocument);
		} catch (Exception e) {
			//throw new MeshException(e);
		} 
		
	
		if(uploadedDocument == null){
			// TODO (SHARIf/RAJU) wait .5 sec! otherwise the currently uploaded document night not
			// be available in the following query!
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		    feed = docService.getFeed(documentListFeedUrl, DocumentListFeed.class);
	        for (DocumentListEntry entry : feed.getEntries()) {
	        	if(entry.getTitle().getPlainText().equals(spreadsheetFilename))
	        		return entry.getTitle().getPlainText();
	        		//return entry.getId().substring(entry.getId().lastIndexOf("%3A") + 3);
	        }	
	        return null;
		}
		else
			return uploadedDocument.getTitle().getPlainText();
			//return uploadedDocument.getId().substring(uploadedDocument.getId().lastIndexOf("%3A") + 3);
	}	

	@SuppressWarnings("unchecked")
	public static GSWorksheet<GSRow<GSCell>> getOrCreateWorkSheetIfAbsent(
			GSSpreadsheet<GSWorksheet> spreadsheet, String worksheetName) {
		GSWorksheet<GSRow<GSCell>> gsWorksheet = spreadsheet
				.getGSWorksheetBySheetName(worksheetName);
		if (gsWorksheet == null) {
			gsWorksheet = spreadsheet.createNewWorksheet(worksheetName);

		}
		return gsWorksheet;
	}

	public static GSRow<GSCell> getOrCreateHeaderRowIfAbsent(
			GSWorksheet<GSRow<GSCell>> worksheet) {
		GSRow<GSCell> row = worksheet.getGSRow(1);
		if (row == null || row.isDeleteCandidate()) {
			row = worksheet.createNewRow(1);
		}
		return row;
	}

	public static GSCell getOrCreateHeaderCellIfAbsent(GSRow<GSCell> row,
			String propertyName) {
		GSCell cell = row.getGSCell(propertyName);
		if (cell == null) {
			cell = row.createNewCell(row.getChildElements().size() + 1,
					propertyName, propertyName);
		}
		return cell;
	}
	
	public static int getSpreadsheetStatus(String username, String password,
			String spreadsheetFileName, String sheetName) throws IOException, ServiceException {

		int status = SPREADSHEET_STATUS_SPREADSHEET_NONE;
		
		FeedURLFactory factory = FeedURLFactory.getDefault();
		SpreadsheetService service = GoogleSpreadsheetUtils
				.getSpreadsheetService(username, password);
		SpreadsheetFeed feed;
		try {
			feed = service.getFeed(factory.getSpreadsheetsFeedUrl(),
					SpreadsheetFeed.class);
		} catch (Exception e) {
			throw new MeshException(e);
		}

		if (feed != null) {
			for (SpreadsheetEntry ss : feed.getEntries()) {
//				if (ss.getId().substring(ss.getId().lastIndexOf("/") + 1)
//						.equals(spreadsheetFileId)) {
				if (ss.getTitle().getPlainText().equals(spreadsheetFileName)) {

					status = status
							& SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_NO;

					List<WorksheetEntry> wsList = getAllWorksheet(service, ss);

					for (WorksheetEntry ws : wsList) {
						if (ws.getTitle().getPlainText().equals(sheetName)) {
							status = status	| SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO;
						} else if (ws.getTitle().getPlainText().equals(sheetName + 
								GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX )) {
							status = status | SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_YES;
						}
					}
					break;
				}
			}
		}
		return status;
	}

	public static int getSpreadsheetStatus(IGoogleSpreadSheet gss, String cotentSheetName) {

		int status = SPREADSHEET_STATUS_SPREADSHEET_NONE;
		
		if(gss == null || gss.getGSSpreadsheet() == null) 
			return status;
		else
			status = status	& SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_NO;

		if ( gss.getGSSpreadsheet().getGSWorksheetBySheetName(cotentSheetName) != null )
			status = status	| SPREADSHEET_STATUS_CONTENTSHEET_YES_SYNCSHEET_NO;
		
		if (gss.getGSSpreadsheet().getGSWorksheetBySheetName(cotentSheetName + 
				GoogleSpreadSheetSyncAdapterFactory.DEFAULT_SYNCSHEET_POSTFIX) != null)
			status = status | SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_YES;

		return status;
	}
	
	/**
	 * Service used to upload document in google docs
	 * @param username
	 * @param password
	 * @return
	 */
	public static DocsService getDocService(String username, String password) {
		DocsService docService = new DocsService("Mesh4j Document Service");
		docService.setChunkedMediaUpload(DocsService.NO_CHUNKED_MEDIA_REQUEST);
		try {
			docService.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			throw new MeshException(e);
		}
		return docService;
	}

	/**
	 * Service for manipulating spreadsheet
	 * @param username
	 * @param password
	 * @return
	 */
	public static SpreadsheetService getSpreadsheetService(String username,
			String password) {
		SpreadsheetService service = new SpreadsheetService("Mesh4j Spreadsheet Service");
		service.setProtocolVersion(SpreadsheetService.Versions.V1);
		try {
			service.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			throw new MeshException(e);
		}
		return service;
	}
	
	/**
	 * returns the Spreadsheet FeedURLFactory 
	 * @return
	 */
	public static FeedURLFactory getSpreadsheetFeedURLFactory(){
		return FeedURLFactory.getDefault();
	}

}
