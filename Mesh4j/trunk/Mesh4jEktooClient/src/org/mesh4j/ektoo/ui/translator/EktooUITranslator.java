package org.mesh4j.ektoo.ui.translator;

import java.text.SimpleDateFormat;
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
	
	public static String getMessageSchemaViewErrorInAdapterCreation(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_ERROR_ADAPTER_CREATION_MESSAGE");
	}
	
	public static String getTitleSettings(){
		return EktooMessageTranslator.translate("TITLTE_SETTINGS");
	}
	
	public static String getErrorSettingsLoading(){
		return EktooMessageTranslator.translate("ERROR_SETTINGS_LOADING");
	}
	
	
	public static String getMessageSchemaComparisonViewErrorInSourceAdapterCreation(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_COMPARISON_ERROR_SOURCE_ADAPTER_CREATION_MESSAGE");
	}
	public static String getMessageSchemaComparisonViewErrorInTargetAdapterCreation(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_COMPARISON_ERROR_TARGET_ADAPTER_CREATION_MESSAGE");
	}
	public static String getMessageSchemaViewErrorSchemaNotFound(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_ERROR_SCHEMA_NOT_FOUND_MESSAGE");
	}
	public static String getMessageSchemaViewErrorSchemaNotFoundInSource(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_ERROR_SOURCE_SCHEMA_NOT_FOUND_MESSAGE");
	}
	
	public static String getMessageSchemaViewErrorSchemaNotFoundInTarget(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_ERROR_TARGET_SCHEMA_NOT_FOUND_MESSAGE");
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

	public static String getMessageNewSpreadsheetName() {
		return EktooMessageTranslator.translate("EKTOO_MESSAGE_NEW_SPREADSHEETNAME");
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

	public static String getZipFileSelectorTitle() {
		return EktooMessageTranslator.translate("EKTOO_ZIP_FILE_CHOOSER_TITLE");
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
	
	public static String getSchemaComarisonLinkText(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_COMPARISON_LINK_TEXT");
	}

	public static String getSchemaComarisonLinkTooltipText(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_COMPARISON_LINK_TOOLTIP");
	}
	
	public static String getSyncModeText(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_MODE_TEXT");
	}
	
	public static String getSyncFilterTypeText(){
		return EktooMessageTranslator.translate("EKTOO_FILTER_TYPE_TEXT");
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

	public static String getErrorInvalidCredentials() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_INVALID_CREDENTIALS");
	}
	
	public static String getErrorInvalidSyncDate() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_INVALID_SYNC_DATE");
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

	public static String getErrorOnShowingSchemaComparison(){
		return EktooMessageTranslator.translate("EKTOO_ERROR_SCHEMA_COMPARISON_FAILED");
	}
	
	public static String getErrorSpreadsheetNameAlreadyExists(String spreadsheetName) {
		return EktooMessageTranslator.translate("EKTOO_ERROR_SPREADSHEET_NAME_ALREADY_EXISTS", spreadsheetName);
	}
	
	public static String getWarningMessageForUnEqualSourceTargetEntityName(){
		return EktooMessageTranslator.translate("EKTOO_WARNING_SOURCE_TARGET_TABLE_SHEET_NOT_SAME");
	}
	
	public static String getConflictsTextforSchema(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_COMPARISON_CONFLICT_TEXT");
	}
	public static String getNewItemTextforSchema(){
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_COMPARISON_NEW_ITEM_OR_NOT_EXIST");
	}
	
	//google spreadsheet specific tooltip
	
	public static String getTooltipView() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_VIEW_DATA_SOURCE");
	}
	
	public static String getTooltipSchemaView() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_VIEW_SCHEMA");
	}
	
	public static String getTitleOfSchemaViewPopUp() {
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_VIEW_POPUP_TITLE");
	}
	public static String getTitleOfSchemaViewComparisonPopUp() {
		return EktooMessageTranslator.translate("EKTOO_SCHEMA_COMPARISON_VIEW_TITLE");
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
	
	public static String getTooltipSelectSingleTable() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SELECT_SINGLE_TABLE");
	}
	
	public static String getTooltipSelectMultiTable() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SELECT_MULTI_TABLE");
	}
	
	public static String getTooltipSyncModeSingle() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SYNC_MODE_SINGLE");
	}
	
	public static String getTooltipSyncModeMulti() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_SYNC_MODE_MULTI");
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
	
	
	public static String getTextSyncModeSingle(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_MODE_SINGLE");
	}
	public static String getTextSyncModeMulti(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_MODE_MULTI");
	}

	public static String getTooltipDateFilter() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_DATE_FILTER");
	}

	public static String getErrorInvalidMeshName() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_INVALID_MESH_NAME");
	}

	public static String getErrorInvalidDataSetName() {
		return EktooMessageTranslator.translate("EKTOO_ERROR_INVALID_DATA_SET");
	}

	public static String getSyncProcessTitle() {
		return EktooMessageTranslator.translate("EKTOO_SYNC_PROCESS_TITLE");
	}
	
	public static String getMsgAdapterCreatingMysql(){
		return EktooMessageTranslator.translate("EKTOO_ADAPTER_CREATING_MYSQL");
	}
	public static String getMsgAdapterCreatingHttp(){
		return EktooMessageTranslator.translate("EKTOO_ADAPTER_CREATING_HTTP");
	}
	public static String getMsgAdapterCreatingMsAccess(){
		return EktooMessageTranslator.translate("EKTOO_ADAPTER_CREATING_MSACCESS");
	}
	
	public static String getErrorMsgAdapterCreatingMsAccess(){
		return EktooMessageTranslator.translate("EKTOO_ADAPTER_CREATING_ERROR_MSACCESS");
	}
	
	public static String getErrorMsgAdapterCreatingHttp(){
		return EktooMessageTranslator.translate("EKTOO_ADAPTER_CREATING_HTTP");
	}
	
	public static String getErrorMsgAdapterCreatingMysql(){
		return EktooMessageTranslator.translate("EKTOO_ADAPTER_CREATING_ERROR_MYSQL");
	}
	
	public static String getMsgSyncStatusReady(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_STATUS_READY");
	}
	
	public static String getMsgSyncStatusSuccessfuly(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_STATUS_SUCCESSFULLY");
	}
	
	public static String getMsgSyncStatusFailed(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_STATUS_FALILD");
	}
	
	public static String getMsgSyncStatusError(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_STATUS_ERROR");
	}
	
	public static String getMsgSyncStatusSynchronizing(){
		return EktooMessageTranslator.translate("EKTOO_SYNC_STATUS_SYNCHRONIZING");
	}

	public static String getMapConfigurationWindowTitle() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_WINDOW_TITLE");
	}

	public static String getMapConfigurationWindowLabelTitle() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_LABEL_TITLE");
	}

	public static String getMapConfigurationWindowLabelDescription() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_LABEL_DESCRIPTION");
	}

	public static String getMapConfigurationWindowLabelAddress() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_LABEL_ADDRESS");
	}

	public static String getMapConfigurationWindowLabelAdd() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_LABEL_ADD");
	}

	public static String getMapConfigurationWindowLabelSave() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_LABEL_SAVE");
	}

	public static String getTooltipMappingView() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_TOOLTIP_VIEW_MAPPINGS");
	}

	public static String getMapConfigurationWindowLabelView() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_LABEL_VIEW");
	}

	public static String getMessageURLFroMapAvailable(String url) {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_MESSAGE_MAP_URL", url);
	}
	
	public static String getEktooSettingsWindowsTitle() {
		return EktooMessageTranslator.translate("EKTOO_SETTINGS_WINDOWS_TITLE");
	}

	public static String getMapConfigurationMessageKMLFileGeneration() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_MESSAGE_KML_FILE_GENERATION");
	}

	public static String getMapConfigurationMessageOpenKMLFile() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_MESSAGE_KML_FILE_OPEN");
	}

	public static String getMapConfigurationErrorRequiredFields() {
		return EktooMessageTranslator.translate("EKTOO_MAP_CFG_MESSAGE_KML_REQUIRED_FIELDS");
	}

	public static String getConflictWindowTitle() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_TITLE");
	}
	
	public static String getConflictItemLabelChooseWinner() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_CHOOSE_WINNER");
	}

	public static String getConflictItemLabelXML() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_XML");
	}

	public static String getConflictItemMessageLastVersion(int seq) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_LAST_VERSION", seq);
	}
	
	public static String getConflictItemMessageCurrentVersion(int seq) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_CURRENT_VERSION", seq);
	}
	
	public static String getConflictItemMessageByOn(String by, Date date) {		
		if(by == null || date == null){
			return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_BY_ON", "admin", "dd/mm/yyyy hh:mm:ss");
		} else {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/mm/yyyy hh:mm:ss");
			return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_BY_ON", by, dateFormat.format(date));
		}
	}

	public static String getConflictItemMessageDeleted() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_DELETED");
	}

	public static String getConflictItemLabelVAL() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_VAL");
	}

	public static String getConflictItemLabelField() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_FIELD");
	}
	
	public static String getConflictItemLabelValue() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_VALUE");
	}

	public static String getConflictLabelSave() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_SAVE");
	}

	public static String getConflictLabelCancel() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_CANCEL");
	}

	public static String getConflictLabelConflictTable() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_TABLE");
	}

	public static String getConflictLabelConflictDetails() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_TABLE_DETAILS");
	}
	
	public static String getConflictLabelSyncId() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_ItemID");
	}
	
	public static String getConflictLabelNumberOfConflictVersions() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_NUMBER_CONFLICT_VERSIONS");
	}
	
	public static String getConflictLabelUsers() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_USERS");
	}

	public static String getTooltipConflictsView() {
		return EktooMessageTranslator.translate("EKTOO_TOOLTIP_VIEW_CONFLICTS");
	}

	public static String getIemEditorByMessage() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_LABEL_EDIT_MANUALLY");
	}

	public static String getIemEditorLastVersionMessage(int version) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_VERSION", version);
	}

	public static String getConflictsResolutionMessageProcessError(String syncId) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_CONFLICT_ERROR", syncId);
	}

	public static String getConflictsResolutionMessageFailed(int errors) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_FAILED", errors);
	}

	public static String getConflictsResolutionMessageSuccessful() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_SUCCESSFUL");
	}

	public static String getConflictsResolutionMessageProcessEnd() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_END");
	}

	public static String getConflictsResolutionMessageProcessResolving(String syncId, int i, int size) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_RESOLVING_CONFLICT", syncId, i, size);
	}

	public static String getConflictsResolutionMessageProcessStart() {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_START");
	}

	public static String getConflictsResolutionMessageProcessUpdating(String syncId, int i, int size) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_UPDATING_CONFLICT", syncId, i, size);
	}

	public static String getConflictsResolutionMessageProcessUpdating(int size) {
		return EktooMessageTranslator.translate("EKTOO_CONFLICTS_MESSAGE_PROCESS_UPDATING_CONFLICTS");
	}

	public static String getXFormEditorLabelCancel() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_LABEL_CANCEL");
	}

	public static String getXFormEditorLabelUpload() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_LABEL_UPLOAD");
	}

	public static String getXFormEditorTitle() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_TITLE");
	}

	public static String getTooltipXFormView() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_TOOLTIP");
	}

	public static String getXFormEditorLabelDownload() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_LABEL_DOWNLOAD");
	}

	public static String getXFormEditorLabelGenerate() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_LABEL_GENERATE");
	}

	public static String getXFormEditorMessageUploadDone() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_MESSAGE_UPLOAD_DONE");
	}

	public static String getXFormEditorMessageUploadFailed() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_MESSAGE_UPLOAD_FAILED");
	}

	public static String getXFormEditorMessageDownloadDone() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_MESSAGE_DOWNLOAD_DONE");
	}

	public static String getXFormEditorMessageDownloadFailed() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_MESSAGE_DOWNLOAD_FAILED");
	}

	public static String getXFormEditorMessageGenerationDone() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_MESSAGE_GENERATION_DONE");
	}

	public static String getXFormEditorMessageGenerationFailed() {
		return EktooMessageTranslator.translate("EKTOO_XFORM_EDITOR_MESSAGE_GENERATION_FAILED");
	}
}
