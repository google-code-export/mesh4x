package org.mesh4j.sync.ui.translator;

import java.text.DateFormat;
import java.util.Date;

import org.mesh4j.sync.translator.MessageTranslator;

public class MeshCompactUITranslator {

	public static String getTradeMark(){
		return MessageTranslator.translate("MESH_COMPACT_TRADEMARK");
	}
	
	public static String getTitle() {
		return MessageTranslator.translate("MESH_TITLE");
	}

	public static String getConfigurationWindowTitle() {
		return MessageTranslator.translate("MESH_COMPACT_CONFIGURATION_WINDOW_TITLE");
	}

	public static String getConfigurationWindowLabelClose() {
		return MessageTranslator.translate("MESH_COMPACT_CONFIGURATION_WINDOW_LABEL_CLOSE");
	}

	public static String getConfigurationWindowToolTipClose() {
		return MessageTranslator.translate("MESH_COMPACT_CONFIGURATION_WINDOW_TOOLTIP_CLOSE");
	}

	public static String getLogWindowTitle() {
		return MessageTranslator.translate("MESH_COMPACT_LOG_WINDOW_TITLE");
	}

	public static String getLogWindowToolTipConsoleView() {
		return MessageTranslator.translate("MESH_COMPACT_LOG_WINDOW_TOOLTIP_CONSOLE_VIEW");
	}

	public static String getLogWindowLabelClean() {
		return MessageTranslator.translate("MESH_COMPACT_LOG_WINDOW_LABEL_CLEAN");
	}

	public static String getLogWindowToolTipClean() {
		return MessageTranslator.translate("MESH_COMPACT_LOG_WINDOW_TOOLTIP_CLEAN");
	}
	
	public static String getLogWindowLabelClose() {
		return MessageTranslator.translate("MESH_COMPACT_LOG_WINDOW_LABEL_CLOSE");
	}

	public static String getLogWindowToolTipClose() {
		return MessageTranslator.translate("MESH_COMPACT_LOG_WINDOW_TOOLTIP_CLOSE");
	}

	public static String getLabelSync() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_SYNC");
	}

	public static String getLabelNew(int i) {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_NEW", i);
	}

	public static String getLabelDeleted(int i) {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_DELETED", i);
	}

	public static String getLabelUpdated(int i) {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_UPDATED", i);
	}

	public static String getLabelIn(int i) {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_IN", i);
	}

	public static String getLabelOut(int i) {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_OUT", i);
	}

	public static String getLabelCancelSync() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_CANCEL_SYNC");
	}
	
	public static String getMessageStartUpError() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_START_UP_ERROR");
	}

	public static String getMessageTestingPhoneCompatibility() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_TESTING_PHONE");
	}

	public static String getMessageTimeOutPhoneCompatibility() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_TIME_OUT_TESTING_PHONE");
	}

	public static String getMessagePhoneIsCompatible() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_PHONE_IS_COMPATIBLE");
	}
	
	public static String getMessageSyncStarted(Date startDate, Date endDate, Date lastSyncDate, DateFormat format) {
		String date = format.format(startDate);
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_SYNC_STARTED", date);
	}

	public static String getMessageSyncFailed(Date startDate, Date endDate, Date lastSyncDate, DateFormat format) {
		String dateStart = format.format(startDate);
		
		String dateEnd = "?";
		if(endDate != null){
			dateEnd = format.format(endDate);
		}
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_SYNC_FAILED", dateStart, dateEnd);
	}

	public static String getMessageSyncSuccessfully(Date startDate, Date endDate, Date lastSyncDate, DateFormat format) {
		String dateStart = format.format(startDate);
		String dateEnd = format.format(endDate);
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_SYNC_SUCCESS", dateStart, dateEnd);
	}
	
	public static String getMessageCancelSyncSuccessfully(Date startDate, Date endDate, Date lastSyncDate, DateFormat format) {
		String dateStart = format.format(startDate);
		String dateEnd = format.format(endDate);
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_CANCEL_SYNC_SUCCESSFULLY", dateStart, dateEnd);
	}

	public static String getMessageProcessingReadyToSync(String endpointAlias, String dataSourceAlias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_PROCESSING_READY_TO_SYNC", endpointAlias, dataSourceAlias);
	}

	public static String getMessageEndpointIsReadyToSync(String endpointAlias, String dataSourceAlias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ENDPOINT_IS_READY_TO_SYNC", endpointAlias, dataSourceAlias);
	}
	
	public static String getMessageEndpointIsNotReadyToSync(String endpointAlias, String dataSourceAlias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ENDPOINT_IS_NOT_READY_TO_SYNC", endpointAlias, dataSourceAlias);
	}

	public static String getLabelDays() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_DAYS");
	}

	public static String getLabelDay() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_DAY");
	}

	public static String getLabelHours() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_HOURS");
	}

	public static String getLabelHour() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_HOUR");
	}

	public static String getLabelMinutes() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_MINUTES");
	}

	public static String getLabelMinute() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_MINUTE");
	}

	public static String getLabelAgo() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_AGO");
	}

	public static String getLabelCancelSyncAndCloseWindow() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_CANCEL_SYNC_AND_CLOSE_WINDOW");
	}

	public static String getLabelCancelCloseWindow() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_CANCEL_CLOSE_WINDOW");
	}

	public static String getMessageForPopUpCloseWindows(int numberOfOpenSyncSessions) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_POP_UP_CLOSE_WINDOWS", numberOfOpenSyncSessions);
	}

	public static String getLabelSyncWith() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_SYNC_WITH");
	}

	public static String getLabelTestPhone() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_TEST_PHONE");
	}

	public static String getLabelReadyToSync() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_READY_TO_SYNC");
	}

	public static String getLabelOpenLogWindow() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_OPEN_LOG_WINDOW");
	}

	public static String getLabelOpenConfigurationWindow() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_OPEN_CONFIGURATION_WINDOW");
	}

	public static String getMessageWelcome(String userName) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_WELCOME", userName);
	}

	public static String getLabelSendAndReceiveChanges() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_SEND_AND_RECEIVE_CHANGES");
	}

	public static String getLabelSendChangesOnly() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_SEND_CHANGES_ONLY");
	}

	public static String getLabelReceiveChangesOnly() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_RECEIVE_CHANGES_ONLY");
	}

	public static String getQuestionTestPhoneCompatibility() {
		return MessageTranslator.translate("MESH_COMPACT_QUESTION_TEST_PHONE_COMPATIBILITY");
	}

	public static String getQuestionForReadyToSync() {
		return MessageTranslator.translate("MESH_COMPACT_QUESTION_READY_TO_SYNC");
	}

	public static String getAnswerReadyToSync(String dataSourceAlias) {
		return MessageTranslator.translate("MESH_COMPACT_ANSWER_READY_TO_SYNC", dataSourceAlias);
	}

	public static String getAnswerNotReadyToSync(String dataSourceAlias) {
		return MessageTranslator.translate("MESH_COMPACT_ANSWER_NOT_READY_TO_SYNC", dataSourceAlias);
	}

	public static String getQuestionEndSymbol() {
		return MessageTranslator.translate("MESH_COMPACT_QUESTION_END_SYMBOL");
	}

	public static String getLabelMSAccessMDB() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_MDB_FILE_NAME");
	}

	public static String getLabelMSAccessTableName() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_MDB_TABLE_NAME");
	}

	public static String getMessageEditContactRequiredFields() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_EDIT_CONTACT_REQUIRED_FIELDS");
	}

	public static String getMessageEditContactOk() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_EDIT_CONTACT_OK");	
	}

	public static String getMessageEditDataSourceOk() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_EDIT_DATA_SOURCE_OK");
	}
	
	public static String getLabelSave() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_SAVE");
	}

	public static String getLabelDelete() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_DELETE");
	}

	public static String getMessageEditDataSourceRequiredFields() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_EDIT_DATA_SOURCE_REQUIRED_FIELDS");
	}

	public static String getLabelTabContacts() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_TAB_CONTACTS");
	}

	public static String getLabelTabDataSources() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_TAB_DATA_SOURCES");
	}

	public static String getLabelTabModem() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_TAB_MODEM");
	}

	public static String getToolTipOpenLogWindow() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_OPEN_LOG_WINDOW");
	}

	public static String getToolTipConfigurationWindow() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_OPEN_CFG_WINDOW");
	}

	public static String getToolTipTradeMark(String url) {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_TRADEMARK", url);
	}

	public static String getToolTipTestPhone() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_TEST_PHONE");
	}

	public static String getToolTipReadyToSync() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_READY_TO_SYNC");
	}

	public static String getToolTipSync() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_SYNC");
	}

	public static String getToolTipEndpoints() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_ENDPOINTS");
	}

	public static String getToolTipDataSources() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_DATA_SOURCES");
	}

	public static String getToolTipSyncMode() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_SYNC_MODE");
	}

	public static String getToolTipEditContactAliasField() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_EDIT_CONTACT_ALIAS_FIELD");
	}

	public static String getToolTipEditContactNumberField() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_EDIT_CONTACT_NUMBER_FIELD");
	}

	public static String getToolTipSave() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_SAVE");
	}

	public static String getToolTipDelete() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_DELETE");
	}

	public static String getToolTipEditDataSourceAliasField() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_EDIT_DATA_SOURCE_ALIAS_FIELD");
	}

	public static String getToolTipEditDataSourceTableNameField() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_EDIT_DATA_SOURCE_TABLE_FIELD");
	}
	
	public static String getLabelCancelationSyncInProgress(String endpoint, String dataSourceAlias) {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_CANCELATION_SYNC_IN_PROCESS", endpoint, dataSourceAlias);
	}

	public static String getLabelTabProperties() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_TAB_PROPERTIES");
	}

	public static String getLabelEditPropertiesPortName() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_EDIT_PROPERTIES_PORT_NAME");
	}

	public static String getLabelEditPropertiesBaudRate() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_EDIT_PROPERTIES_BAUD_RATE");
	}

	public static String getLabelEditPropertiesSendRetryDelay() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_EDIT_PROPERTIES_SEND_RETRY_DELAY");
	}

	public static String getLabelEditPropertiesReceiveRetryDelay() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_EDIT_PROPERTIES_RECEIVE_RETRY_DELAY");
	}

	public static String getLabelEditPropertiesReadyToSyncDelay() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_EDIT_PROPERTIES_READY_TO_SYNC_DELAY");
	}

	public static String getLabelEditPropertiesTestPhoneDelay() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_EDIT_PROPERTIES_TEST_PHONE_DELAY");
	}

	public static Object getMessageEditPropertiesRequiredFields() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_EDIT_PROPERTIES_REQUIRED_FIELDS");
	}

	public static String getLabelCancel() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_CANCEL");
	}

	public static String getMessageConfigurePhone() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_CONFIGURE_PHONE");
	}

	public static String getMessagePhoneConnected() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_PHONE_WAS_CONNECTED");
	}

	public static Object getMessageForPopUpPhoneNotConnected() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_FOR_POPUP_PHONE_NOT_CONNECTED");
	}


	public static String getSyncSessionWindowTitle() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_WINDOW_TITLE");
	}

	public static String getSyncSessionWindowLabelAllSessions() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_LABEL_ALL_SESSIONS");
	}

	public static String getSyncSessionWindowLabelClose() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_LABEL_CLOSE");
	}

	public static String getSyncSessionWindowToolTipClose() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_CLOSE");
	}

	public static String getSyncSessionWindowToolTipAllSessions() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_ALL_SESSIONS_NODE");
	}

	public static String getSyncSessionWindowToolTipDataSource() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_DATA_SOURCE_NODE");
	}

	public static String getSyncSessionWindowToolTipSyncSession() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_SYNC_SESSION_NODE");
	}
	
	public static String getMessageErrorBeginSync(String endpointAlias, String sourceId) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ERROR_BEGIN_SYNC", endpointAlias, sourceId);
	}

	public static String getMessageCancelSyncErrorSessionNotOpen(String endpointAlias, String sourceId) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ERROR_CANCEL_SYNC_SESSION_NOT_OPEN", endpointAlias, sourceId);
	}

	public static String getMessageNotifySendMessageError(String endpointAlias, String message) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_NOTIFY_SEND_MSG_ERROR", endpointAlias, message);
	}
	
	public static String getMessageNotifyReceiveMessageError(String endpointAlias, String message) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_NOTIFY_SEND_MSG_ERROR", endpointAlias, message);
	}
	
	public static String getSyncSessionWindowLabelChooseSync() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_LABEL_CHOOSE_SYNC");
	}
	
	public static String getSyncSessionWindowToolTipChooseSync() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_CHOOSE_SYNC");
	}

	public static String getLabelCancelled() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_CANCELLED");
	}

	public static String getMessageInOutPendings(int numberInPendingToArriveMessages, int numberOutPendingAckMessages) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_IN_OUT_PENDINGS", numberInPendingToArriveMessages, numberOutPendingAckMessages);
	}

	public static String getLabelOpenSyncSessionsWindow() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_LABEL_OPEN_WINDOWS");
	}

	public static String getToolTipOpenSyncSessionsWindow() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_OPEN_WINDOWS");
	}

	public static String getToolTipOpenSyncSessionsWindowNewSyncSessionss() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_NEW_SYNC_SESSIONS");
	}

	public static String getLabelOpenSyncWindow() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_WINDOW_LABEL_OPEN");
	}

	public static String getToolTipOpenSyncWindow() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_WINDOW_TOOLTIP_OPEN");
	}

	public static String getLabelURL() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_WINDOW_LABEL_URL");
	}

	public static String getSyncWindowTitle() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_WINDOW_TITLE");
	}

	public static String getSyncWindowTooltipViewFeed() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_WINDOW_TOOLTIP_VIEW_FEED");
	}

	public static String getMessageEndpointMappingAutomaticallyCreated(String alias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ENDPOINT_AUTOMATICALLY_CREATED", alias);
	}

	public static String getMessageNotAvailableDataSource(String dataSourceAlias,  String dataSourceDescription, String endpointAlias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_DATA_SOURCE_NOT_AVAILABLE", endpointAlias, dataSourceAlias, dataSourceDescription);
	}

	public static String getMessageReadyToSyncAnswerSent(String dataSourceAlias, String endpointAlias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_READY_TO_SYNC_ANSWER_SENT", endpointAlias, dataSourceAlias);
	}

	public static String getMessageErrorSessionCreation(String dataSourceAlias, String endpointAlias) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_DATA_SOURCE_NOT_AVAILABLE", endpointAlias, dataSourceAlias, dataSourceAlias);
	}
	
	public static String getToolTipOpenLogWindowNewMessagesAvailables() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_OPEN_LOG_WINDOW_NEW_MESSAGES");
	}

	public static String getLabelOpenMapsWindow() {
		return MessageTranslator.translate("MESH_COMPACT_LABEL_OPEN_MAPS_WINDOW");
	}

	public static String getToolTipOpenMapsWindow() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_OPEN_MAPS_WINDOW");
	}

	public static String getMapsWindowTooltipDownloadMappings() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_TOOLTIP_DOWNLOAD_MAPPINGS");
	}

	public static String getMapsWindowTooltipViewCloudMap() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_TOOLTIP_OPEN_CLOUD_MAP");
	}

	public static String getMapsWindowTitle() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_TITLE");
	}

	public static String getMapsWindowMessageWelcome(String userName) {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_WELCOME", userName);
	}

	public static String getMapsWindowToolTipCreateMap() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_TOOLTIP_OPEN_LOCAL_MAP");
	}

	public static String getMapsWindowToolTipDataSourcesToCreateMap() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_TOOLTIP_DATA_SOURCES");
	}

	public static String getMapsWindowMessageDownloadMappingsStart() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_DOWNLOAD_MAPPINGS_START");
	}

	public static String getMapsWindowMessageDownloadMappingsEnd() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_DOWNLOAD_MAPPINGS_END");
	}

	public static String getMapsWindowMessageDownloadMappingsFailed() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_DOWNLOAD_MAPPINGS_FAILED");
	}

	public static String getMapsWindowMessageMapCreationStart() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_MAP_CREATION_START");
	}

	public static String getMapsWindowMessageMapCreationEnd() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_MAP_CREATION_END");
	}
	
	public static String getMapsWindowMessageMapCreationFailed() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_MAP_CREATION_FAILED");
	}

	public static String getMapsWindowMessageNetworkMapCreationFailed() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_NET_MAP_CREATION_FAILED");
	}

	public static String getMapsWindowMessageNetworkMapCreationStart() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_NET_MAP_CREATION_START");
	}

	public static String getMapsWindowMessageNetworkMapCreationEnd() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_NET_MAP_CREATION_END");
	}

	public static String getTooltipViewDataSource() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_VIEW_DATA_SOURCE");
	}

	
	public static String getMapsWindowLabelCreateMap() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_LABEL_OPEN_MAP");
	}
	
	
	public static String getSyncSessionWindowLabelOpenDataSource() {
		return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_LABEL_OPEN_DATA_SOURCE");
	}

	public static String getSyncSessionWindowToolTipCloudSyncSession(String url, String start, String end, int conflicts) {
		if(conflicts == 0){
			return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_CLOUD_SYNC_SUMMARY", url, start, end);
		} else {
			return MessageTranslator.translate("MESH_COMPACT_SYNC_SESSION_TOOLTIP_CLOUD_SYNC_SUMMARY_WITH_CONFLICTS", url, start, end, conflicts);
		}
	}

	public static String getErrorInvalidURL() {
		return MeshUITranslator.getErrorInvalidURL();
	}

	public static String getErrorInvalidMSAccessTable() {
		return MeshUITranslator.getErrorInvalidMSAccessTable();
	}

	public static String getMapsWindowMessageMapOpenFailed() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_MAP_OPEN_FAILED");
	}

	public static String getMapsWindowMessageNetworkMapOpenFailed() {
		return MessageTranslator.translate("MESH_COMPACT_MAPS_WINDOW_MESSAGE_NET_MAP_OPEN_FAILED");
	}

	public static String getErrorImpossibleToOpenFileBecauseFileDoesNotExists() {
		return MessageTranslator.translate("MESH_COMPACT_ERROR_IMPOSSIBLE_OPEN_FILE_BECAUSE_FILE_DOES_NOT_EXISTS");
	}
	
	public static String getErrorOpenFileActionNotSupported(){
		return MessageTranslator.translate("MESH_COMPACT_ERROR_OPEN_FILE_ACTION_NOT_SUPPORTED");
	}

	public static String getErrorOpenBrowser() {
		return MessageTranslator.translate("MESH_COMPACT_ERROR_OPEN_BROWSER");
	}

	public static String getMessageOpenBrowserActionNotSupported() {
		return MessageTranslator.translate("MESH_COMPACT_ERROR_OPEN_BROWSER_ACTION_NOT_SUPPORTED");
	}

	public static String getMeshAdminWindowTitle() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_TITLE");
	}

	public static String getLabelOpenMeshAdmin() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_OPEN_WINDOW");
	}

	public static String getToolTipOpenMeshAdmin() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_TOOLTIP_OPEN_WINDOW");
	}

	public static String getErrorSaveMappingsFailed() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_ERROR_SAVE_MAPPINGS"); 
	}

	public static String getErrorSaveMeshCloudFailed() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_ERROR_SAVE_MESH_CLOUD"); 
	}

	public static String getMeshAdminWindowLabelMappings() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_MAPPINGS");
	}

	public static String getMeshAdminWindowLabelTitle() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_TITLE");
	}

	public static String getMeshAdminWindowLabelDescription() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_DESCRIPTION"); 
	}

	public static String getMeshAdminWindowLabelAddress() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_ADDRESS"); 
	}

	public static String getMeshAdminWindowLabelIll() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_ILL");
	}

	public static String getMeshAdminWindowLabelUpdateTimestamp() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_UPDATE_TIMESTAMP"); 
	}

	public static String getMeshAdminWindowLabelAdd() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_ADD"); 
	}

	public static String getMeshAdminWindowLabelSave() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_SAVE"); 
	}

	public static String getMeshAdminWindowLabelCloud() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_CLOUD"); 
	}

	public static String getMeshAdminWindowLabelPublish() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_PUBLISH"); 
	}

	public static String getMeshAdminWindowLabelURL() {
		return MessageTranslator.translate("MESH_COMPACT_ADMIN_WINDOW_LABEL_URL"); 
	}

}
