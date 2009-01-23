package org.mesh4j.sync.ui.translator;

import java.text.DateFormat;
import java.util.Date;

import org.mesh4j.sync.message.IEndpoint;
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

	public static String getMessageProcessingReadyToSync(String endpoint, String dataSource) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_PROCESSING_READY_TO_SYNC", endpoint, dataSource);
	}

	public static String getMessageEndpointIsReadyToSync(String endpoint, String dataSource) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ENDPOINT_IS_READY_TO_SYNC", endpoint, dataSource);
	}
	
	public static String getMessageEndpointIsNotReadyToSync(String endpoint, String dataSource) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ENDPOINT_IS_NOT_READY_TO_SYNC", endpoint, dataSource);
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

	public static String getMessageWelcome() {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_WELCOME");
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

	public static String getToolTipTradeMark() {
		return MessageTranslator.translate("MESH_COMPACT_TOOLTIP_TRADEMARK");
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
	
	public static String getMessageErrorBeginSync(String endpointId, String sourceId) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ERROR_BEGIN_SYNC", endpointId, sourceId);
	}

	public static String getMessageCancelSyncErrorSessionNotOpen(IEndpoint endpoint, String sourceId) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_ERROR_CANCEL_SYNC_SESSION_NOT_OPEN", endpoint.getEndpointId(), sourceId);
	}

	public static String getMessageNotifySendMessageError(String endpointId, String message) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_NOTIFY_SEND_MSG_ERROR", endpointId, message);
	}
	
	public static String getMessageNotifyReceiveMessageError(String endpointId, String message) {
		return MessageTranslator.translate("MESH_COMPACT_MESSAGE_NOTIFY_SEND_MSG_ERROR", endpointId, message);
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
}