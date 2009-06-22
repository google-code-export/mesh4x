package org.mesh4j.ektoo.ui.translator;

import java.util.Date;

import org.mesh4j.translator.EktooMessageTranslator;

public class EktooUITranslator {

	public static String getSyncViaLabel() {
		return EktooMessageTranslator.translate("EKTOO_SYNC_CHANNEL_LABEL");
	}

	public static String getSyncViaSMSLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_CHANNEL_SMS_FIELD_LABEL");
	}

	public static String getSyncViaWebLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_CHANNEL_WEB_FIELD_LABEL");
	}

	public static String getSyncViaFileLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_CHANNEL_FILE_FIELD_LABEL");
	}
	public static String getSyncURILabel(){
		return EktooMessageTranslator
		.translate("EKTOO_SYNC_CHANNEL_URI_FIELD_LABEL");
	}

	public static String getSyncTypeSendLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_TYPE_SEND_FIELD_LABEL");
	}

	public static String getSyncTypeReceiveLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_TYPE_RECEIVE_FIELD_LABEL");
	}

	public static String getMessageNewFolder() {
		return EktooMessageTranslator.translate("EKTOO_MESSAGE_NEW_FOLDER");
	}

	public static String getMessageUpdateFolder() {
		return EktooMessageTranslator.translate("EKTOO_MESSAGE_UPDATE_FOLDER");
	}	
	
	public static String getMessageNewFile() {
		return EktooMessageTranslator.translate("EKTOO_MESSAGE_NEW_FILE");
	}

	public static String getMessageUpdateFile() {
		return EktooMessageTranslator.translate("EKTOO_MESSAGE_UPDATE_FILE");
	}	
	
	public static String getSyncTypeSendAndReceiveLabel() {
		// return "Send & Receive";
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_TYPE_SEND_AND_RECEIVE_FIELD_LABEL");
	}

	public static String getMessageSyncErrorInAdapterCreation(String source,
			String target, Date date) {
		return EktooMessageTranslator.translate(
				"EKTOO_SYNC_PROCESS_ERROR_ADAPTER_CREATION_MESSAGE", source, target, date);
	}
	
	public static String getMessageSyncSyccessfuly(String source,
			String target, Date date) {
		return EktooMessageTranslator.translate(
				"EKTOO_SYNC_PROCESS_SUCCESS_MESSAGE", source, target, date);
	}

	public static String getMessageSyncConflicts(String source, String target,
			Date date) {
		return EktooMessageTranslator.translate(
				"EKTOO_SYNC_PROCESS_CONFLICT_MESSAGE", source, target, date);
	}

	public static String getMessageStartSync(String source, String target,
			Date date) {
		return EktooMessageTranslator.translate(
				"EKTOO_SYNC_PROCESS_START_MESSAGE", source, target, date);
	}

	public static String getMessageEndSync(String source, String target,
			Date date) {
		return EktooMessageTranslator.translate(
				"EKTOO_SYNC_PROCESS_END_MESSAGE", source, target, date);
	}

	public static String getMessageSyncFailed(String source, String target,
			Date date) {
		return EktooMessageTranslator.translate(
				"EKTOO_SYNC_PROCESS_FAILED_MESSAGE", source, target, date);
	}

	public static String getExcelFileDescription() {
		// return "Microsoft Excel File(s)";
		return EktooMessageTranslator
				.translate("EKTOO_EXCEL_FILE_TYPE_DESCRIPTION");
	}

	public static String getExcelFileSelectorTitle() {
		return EktooMessageTranslator
				.translate("EKTOO_EXCEL_FILE_CHOOSER_TITLE");
	}

	public static String getKMLFileSelectorTitle() {
		return EktooMessageTranslator.translate("EKTOO_KML_FILE_CHOOSER_TITLE");
	}

	public static String getXMLFileSelectorTitle() {
		return EktooMessageTranslator.translate("EKTOO_XML_FILE_CHOOSER_TITLE");
	}

	public static String getMSAccessFileSelectorTitle() {
		return EktooMessageTranslator
				.translate("EKTOO_ACCESS_FILE_CHOOSER_TITLE");
	}

	public static String getExcelWorksheetLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_EXCEL_WORKSHEET_FIELD_LABEL");
	}

	public static String getExcelUniqueColumnLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_EXCEL_UNIQUE_COLUMN_FIELD_LABEL");
	}

	public static String getExcelFileLabel() {
		return EktooMessageTranslator.translate("EKTOO_EXCEL_FILE_FIELD_LABEL");
	}

	public static String getGooglePasswordLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_GOOGLE_PASSWORD_FIELD_LABEL");
	}

	public static String getGoogleUserLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_GOOGLE_USER_FIELD_LABEL");
	}

	public static String getGoogleWorksheetLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_GOOGLE_WORKSHEET_FIELD_LABEL");
	}

//	public static String getGoogleKeyLabel() {
//		return EktooMessageTranslator.translate("EKTOO_GOOGLE_KEY_FIELD_LABEL");
//	}
	
	public static String getGoogleSpreadsheetNameLabel() {
		return EktooMessageTranslator.translate("EKTOO_GOOGLE_SPREADSHEET_NAME_FIELD_LABEL");
	}
	
	public static String getUniqueColumnNameLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_GOOGLE_UNIQUE_COLUMN_FIELD_LABEL");
	}

	public static String getSyncTypeLabel() {
		return EktooMessageTranslator.translate("EKTOO_SYNC_TYPE_LABEL");
	}

	public static String getSourceSyncItemSelectorTitle() {
		return EktooMessageTranslator.translate("EKTOO_SYNC_SOURCE_LABEL");
	}

	public static String getTargetSyncItemSelectorTitle() {
		return EktooMessageTranslator.translate("EKTOO_SYNC_TARGET_LABEL");
	}

	public static String getAccessFileLabel() {
		// return "Database";
		return EktooMessageTranslator
				.translate("EKTOO_ACCESS_FILE_FIELD_LABEL");
	}

	public static String getAccessTableLabel() {
		// return "Table";
		return EktooMessageTranslator
				.translate("EKTOO_ACCESS_TABLE_FIELD_LABEL");
	}

	public static String getFileLabel() {
		// return "File";
		return EktooMessageTranslator
				.translate("EKTOO_FILE_FIELD_DEFAULT_LABEL");
	}

	public static String getBrowseButtonLabel() {
		// return "...";
		return EktooMessageTranslator
				.translate("EKTOO_FILE_CHOOSER_BUTTON_DEFAULT_LABEL");
	}

	public static String getTableLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_TABLE_FIELD_DEFAULT_LABEL");
	}

	public static String getFieldLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_FIELD_FIELD_DEFAULT_LABEL");
	}

	public static String getGoogleWorksheetColumnLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_GOOGLE_WORKSHEET_COLUMN_FIELD_LABEL");
	}

	public static String getKmlFileNameLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_KML_FILE_NAME_FIELD_LABEL");
	}

	public static String getSyncDataSourceType() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_PROCESS_DATA_SOURCE_LABEL");
	}

	public static String getSyncLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_PROCESS_START_BUTTON_LABEL");
	}

	public static String getSyncToolTip() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_PROCESS_START_BUTTON_TOOLTIP");
	}

	public static String getDataSourceType() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_PROCESS_DATA_SOURCE_LIST_LABEL");
	}

	public static String getMultiModeSyncSupportedDataSourceType() {
		return EktooMessageTranslator
				.translate("EKTOO_SYNC_PROCESS_MULTIMODE_SYNC_SUPPORTED_DATA_SOURCE_LIST_LABEL");
	}
	
	public static String getMySQLHostLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_HOST_NAME_FIELD_LABEL");
	}

	public static String getMySQLPortLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_PORT_NO_FIELD_LABEL");
	}

	public static String getMySQLDatabaseLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_DATABASE_NAME_FIELD_LABEL");
	}

	public static String getMySQLTableLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_TABLE_NAME_FIELD_LABEL");
	}

	public static String getMySQLUserLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_USER_NAME_FIELD_LABEL");
	}

	public static String getMySQLPasswordLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_USER_PASSWORD_FIELD_LABEL");
	}

	public static String getTitle() {
		return EktooMessageTranslator.translate("EKTOO_TITLE");
	}
	
	public static String getSchemaCreationCheckboxLabel() {
		return EktooMessageTranslator.translate("EKTOO_SYNC_PROCESS_CHECK_BUTTON_LABEL");
	}
	
	public static String getHelpText(){
		return EktooMessageTranslator.translate("EKTOO_HELP_TEXT");
	}
	
	public static String getAboutText(){
		return EktooMessageTranslator.translate("EKTOO_ABOUT_TEXT");
	}
	
	public static String getSettingsText(){
		return EktooMessageTranslator.translate("EKTOO_SETTINGS_TEXT");
	}
	
	public static String getMeshNameFieldLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_CLOUD_MESH_NAME_FIELD_LABEL");
	}

	public static String getMeshDataSetFieldLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_CLOUD_MESH_DATASET_FIELD_LABEL");
	}

	public static String getPoweredByLabel() {
		return EktooMessageTranslator.translate("EKTOO_TRADEMARK_LABEL");
	}

	public static String getErrorOpenBrowser() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_OPEN_BROWSER");
	}

	public static String getMessageOpenBrowserActionNotSupported() {
		return EktooMessageTranslator
				.translate("EKTOO_ERROR_OPEN_BROWSER_ACTION_NOT_SUPPORTED");
	}

	public static String getErrorInvalidURL() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_INVALID_URL");
	}

	public static String getPoweredByLabelTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_TRADEMARK_LABEL_TOOLTIP");
	}

	public static String getDatabaseConnectionTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_CONNECT_BUTTON_TOOLTIP");
	}

	public static String getFeedFileNameLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_FEED_FILE_NAME_FIELD_LABEL");
	}
	
	public static String getErrorEmptySelection(String name) {
		return EktooMessageTranslator.translate("EKTOO_ERROR_EMPTY_SELECTION", name);
	}
	
	public static String getErrorEmptyOrNull(String name) {
		return EktooMessageTranslator.translate("EKTOO_ERROR_EMPTY_OR_NULL", name);
	}

	public static String getErrorNotExists(String name) {
		return EktooMessageTranslator.translate("EKTOO_ERROR_NOT_EXISTS", name);
	}
	
	public static String getErrorInvalid(String name) {
		return EktooMessageTranslator.translate("EKTOO_ERROR_INVALID", name);
	}

	public static String getFolderFileNameLabel() {
		return EktooMessageTranslator
				.translate("EKTOO_FOLDER_FILE_NAME_FIELD_LABEL");
	}

	public static String getStatusbarMessage() {
		return EktooMessageTranslator
				.translate("EKTOO_STATUSBAR_DEFAULT_MESSAGE");
	}

	public static String getMySQLUserNameFieldTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_USER_NAME_FIELD_TOOLTIP");
	}

	public static String getMySQLUserPasswordFieldTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_USER_PASSWORD_FIELD_TOOLTIP");
	}

	public static String getMySQLHostFieldTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_HOST_NAME_FIELD_TOOLTIP");
	}

	public static String getMySQLPortFieldTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_PORT_NO_FIELD_TOOLTIP");
	}

	public static String getMySQLDatabaseFieldTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_DATABASE_NAME_FIELD_TOOLTIP");
	}

	public static String getMySQLTableFieldTooltip() {
		return EktooMessageTranslator
				.translate("EKTOO_MYSQL_TABLE_NAME_FIELD_TOOLTIP");
	}

	public static String getErrorImpossibleToOpenFileBecauseFileDoesNotExists() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_IMPOSSIBLE_OPEN_FILE_BECAUSE_FILE_DOES_NOT_EXISTS");
	}

	public static String getErrorOpenFileActionNotSupported() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_OPEN_FILE_ACTION_NOT_SUPPORTED");
	}

	
	//google spreadsheet specific tooltip
	
	public static String getTooltipView() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_VIEW_DATA_SOURCE");
	}
	
	public static String getTooltipFetchSpreadsheets() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_GOOGLE_VIEW_FETCH_SPREADSHEETS");
	}	
	
	public static String getTooltipGoogleDocsUsername() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_GOOGLE_DOCS_USERNAME");
	}
	
	public static String getTooltipGoogleDocsPassword() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_GOOGLE_DOCS_PASSWORD");
	}
	
	public static String getTooltipGoogleSpreadsheetName() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_GOOGLE_SPREADSHEET_NAME");
	}
	
	public static String getTooltipSelectWorksheet() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SELECT_WORKSHEET");
	}
	
	public static String getTooltipSelectTable() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SELECT_TABLE");
	}
	
	public static String getTooltipIdColumnName() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_ID_COLUMN_NAME");
	}

	//cloud specific tooltip
	
	public static String getTooltipCloudMeshname() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_CLOUD_MESHNAME");
	}
	
	public static String getTooltipCloudDatasetname() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_CLOUD_DATASETNAME");
	}	
	
	public static String getTooltipCloudSyncServerURI() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_CLOUD_SYNCSERVER_URI");
	}	
	
	//folder specific tooltip
	
	public static String getTooltipFolderSeleceFile() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_FOLDER_SELECT_FOLDER");
	}

	//Kml/feed/Access/Excel specific tooltip
	
	public static String getTooltipSeleceDataFile(String fileType) {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SELECT_FILE", fileType);
	}
}
