package org.mesh4j.sync.adapters.googlespreadsheet;

import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.adapters.ISyncAdapterFactory;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.GoogleSpreadsheetToPlainXMLMapping;
import org.mesh4j.sync.adapters.googlespreadsheet.mapping.IGoogleSpreadsheetToXMLMapping;
import org.mesh4j.sync.adapters.split.SplitAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.IIdentityProvider;
import org.mesh4j.sync.validations.MeshException;

/**
 * @author sharif
 * @version 1.0, 12/5/2009
 * 
 */
public class GoogleSpreadSheetSyncAdapterFactory implements ISyncAdapterFactory {

	public static final String SOURCE_TYPE = "GoogleSpreadsheet";
	private static final String GOOGLE_SPREADSHEET = SOURCE_TYPE + ":";
	public static final String DEFAULT_SEPARATOR = "@";
	public static final String DEFAULT_SYNCSHEET_POSTFIX = "_sync";

	// BUSINESS METHODS
	public GoogleSpreadSheetSyncAdapterFactory() {
		super();
	}

	// GoogleSpreadsheet:username@password123@spreadsheetFileId@worksheetName@idColumn[@lastUpdateColumnName]
	public static String createSourceDefinition(String username,
			String password, String spreadsheetFileId,
			String contentWorksheetName, String idColumn,
			String lastUpdateColumnName) {
		String sourceDefinition = GOOGLE_SPREADSHEET + username
				+ DEFAULT_SEPARATOR + password + DEFAULT_SEPARATOR
				+ spreadsheetFileId + DEFAULT_SEPARATOR + contentWorksheetName
				+ DEFAULT_SEPARATOR + idColumn + DEFAULT_SEPARATOR
				+ lastUpdateColumnName;
		return sourceDefinition;
	}

	@Override
	public boolean acceptsSource(String sourceId, String sourceDefinition) {
		if (sourceDefinition == null) {
			return false;
		}

		String[] elements = sourceDefinition.split(DEFAULT_SEPARATOR);
		try {
			return sourceDefinition.toUpperCase().startsWith(
					GOOGLE_SPREADSHEET.toUpperCase())
					&& (elements.length == 5 || elements.length == 6) //lastUpdateColumnName may not be present
					&& (GoogleSpreadsheetUtils.getSpreadsheetStatus(elements[0], elements[1], elements[2], null) > GoogleSpreadsheetUtils.SPREADSHEET_STATUS_CONTENTSHEET_NO_SYNCSHEET_NO);
		} catch (Exception e) {
			throw new MeshException(e);
		}
	}

	@Override
	public ISyncAdapter createSyncAdapter(String sourceAlias,
			String sourceDefinition, IIdentityProvider identityProvider)
			throws Exception {

		String[] elements = sourceDefinition.substring(
				GOOGLE_SPREADSHEET.length(), sourceDefinition.length()).split(DEFAULT_SEPARATOR);

		String username = elements[0];
		String password = elements[1];
		String spreadsheetName = elements[2];
		String worksheetName = elements[3];
		String idColumnName = elements[4];
		String lastUpdateColumnName = elements[5];

		return createSyncAdapter(username, password, spreadsheetName,
				worksheetName, idColumnName, lastUpdateColumnName,
				identityProvider, sourceAlias);
	}

	public SplitAdapter createSyncAdapter(String username, String password,
			String spreadsheetName, String contentSheetName,
			String idColumnName, String lastUpdateColumnName,
			IIdentityProvider identityProvider, String sourceAlias) {

		IGoogleSpreadSheet spreadSheet = new GoogleSpreadsheet(
				spreadsheetName, username, password);
		
		//TODO:need to review whether keep it or let it handle by the Guard in adapter constructor
		if(spreadSheet.getGSSpreadsheet() == null ) return null;

		GoogleSpreadSheetSyncRepository syncRepo = createSyncRepository(
				spreadSheet, contentSheetName + DEFAULT_SYNCSHEET_POSTFIX,
				identityProvider);

		GoogleSpreadSheetContentAdapter contentAdapter = createContentAdapter(
				spreadSheet, idColumnName, lastUpdateColumnName,
				contentSheetName, sourceAlias);

		return new SplitAdapter(syncRepo, contentAdapter, identityProvider);
	}

	protected GoogleSpreadSheetContentAdapter createContentAdapter(
			IGoogleSpreadSheet spreadSheet, String idColumnName,
			String lastUpdateColumnName, String sheetName, String type) {

		IGoogleSpreadsheetToXMLMapping mapper = new GoogleSpreadsheetToPlainXMLMapping(
				type, idColumnName, lastUpdateColumnName, sheetName,
				spreadSheet.getDocsService());

		return new GoogleSpreadSheetContentAdapter(spreadSheet, mapper);
	}

	protected GoogleSpreadSheetSyncRepository createSyncRepository(
			IGoogleSpreadSheet spreadSheet, String syncWorksheetName,
			IIdentityProvider identityProvider) {

		return new GoogleSpreadSheetSyncRepository(spreadSheet,
				identityProvider, IdGenerator.INSTANCE, syncWorksheetName);
	}

	@Override
	public String getSourceType() {
		return SOURCE_TYPE;
	}
}
