package org.mesh4j.sync.ui.translator;

import org.mesh4j.sync.mappings.DataSourceMapping;
import org.mesh4j.sync.mappings.EndpointMapping;
import org.mesh4j.sync.translator.MessageTranslator;

public class EpiInfoCompactUITranslator {

	public static String getTradeMark(){
		return MessageTranslator.translate("EPIINFO_COMPACT_TRADEMARK");
	}
	
	public static String getTitle() {
		return MessageTranslator.translate("EPIINFO_TITLE");
	}

	public static String getConfigurationWindowTitle() {
		return MessageTranslator.translate("EPIINFO_COMPACT_CONFIGURATION_WINDOW_TITLE");
	}

	public static String getConfigurationWindowLabelClose() {
		return MessageTranslator.translate("EPIINFO_COMPACT_CONFIGURATION_WINDOW_LABEL_CLOSE");
	}

	public static String getConfigurationWindowToolTipClose() {
		return MessageTranslator.translate("EPIINFO_COMPACT_CONFIGURATION_WINDOW_TOOLTIP_CLOSE");
	}

	public static String getLogWindowTitle() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LOG_WINDOW_TITLE");
	}

	public static String getLogWindowToolTipConsoleView() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LOG_WINDOW_TOOLTIP_CONSOLE_VIEW");
	}

	public static String getLogWindowLabelClean() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LOG_WINDOW_LABEL_CLEAN");
	}

	public static String getLogWindowToolTipClean() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LOG_WINDOW_TOOLTIP_CLEAN");
	}
	
	public static String getLogWindowLabelClose() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LOG_WINDOW_LABEL_CLOSE");
	}

	public static String getLogWindowToolTipClose() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LOG_WINDOW_TOOLTIP_CLOSE");
	}

	public static String getMessageSyncFailed() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_SYNC_FAILED");
	}

	public static String getMessageSyncSuccessfully() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_SYNC_SUCCESS");
	}

	public static String getLabelSync() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_SYNC");
	}

	public static String getLabelNew(int i) {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_NEW", i);
	}

	public static String getLabelDeleted(int i) {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_DELETED", i);
	}

	public static String getLabelUpdated(int i) {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_UPDATED", i);
	}

	public static String getLabelIn(int i) {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_IN", i);
	}

	public static String getLabelOut(int i) {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_OUT", i);
	}

	public static String getMessageSyncStarted(String date) {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_SYNC_STARTED", date);
	}

	public static String getLabelCancelSync() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_CANCEL_SYNC");
	}

	public static String getMessageCancelSyncSuccessfully() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_CANCEL_SYNC_SUCCESSFULLY");
	}

	public static String getMessageStartUpError() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_START_UP_ERROR");
	}

	public static String getMessageTestingPhoneCompatibility() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_TESTING_PHONE");
	}

	public static String getMessageTimeOutPhoneCompatibility() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_TIME_OUT_TESTING_PHONE");
	}

	public static String getMessagePhoneIsCompatible() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_PHONE_IS_COMPATIBLE");
	}

	public static String getMessageProcessingReadyToSync(String endpoint, String dataSource) {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_PROCESSING_READY_TO_SYNC", endpoint, dataSource);
	}

	public static String getMessageEndpointIsReadyToSync(String endpoint, String dataSource) {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_ENDPOINT_IS_READY_TO_SYNC", endpoint, dataSource);
	}
	
	public static String getMessageEndpointIsNotReadyToSync(String endpoint, String dataSource) {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_ENDPOINT_IS_NOT_READY_TO_SYNC", endpoint, dataSource);
	}

	public static String getLabelDays() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_DAYS");
	}

	public static String getLabelDay() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_DAY");
	}

	public static String getLabelHours() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_HOURS");
	}

	public static String getLabelHour() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_HOUR");
	}

	public static String getLabelMinutes() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_MINUTES");
	}

	public static String getLabelMinute() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_MINUTE");
	}

	public static String getLabelAgo() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_AGO");
	}

	public static String getLabelCancelSyncAndCloseWindow() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_CANCEL_SYNC_AND_CLOSE_WINDOW");
	}

	public static String getLabelCancelCloseWindow() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_CANCEL_CLOSE_WINDOW");
	}

	public static String getMessageForPopUpCloseWindows(DataSourceMapping dataSource, EndpointMapping endpoint) {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_POP_UP_CLOSE_WINDOWS", dataSource.getAlias(), endpoint.getAlias());
	}

	public static String getLabelSyncWith() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_SYNC_WITH");
	}

	public static String getLabelTestPhone() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_TEST_PHONE");
	}

	public static String getLabelReadyToSync() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_READY_TO_SYNC");
	}

	public static String getLabelOpenLogWindow() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_OPEN_LOG_WINDOW");
	}

	public static String getLabelOpenConfigurationWindow() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_OPEN_CONFIGURATION_WINDOW");
	}

	public static String getMessageWelcome() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_WELCOME");
	}

	public static String getLabelSendAndReceiveChanges() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_SEND_AND_RECEIVE_CHANGES");
	}

	public static String getLabelSendChangesOnly() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_SEND_CHANGES_ONLY");
	}

	public static String getLabelReceiveChangesOnly() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_RECEIVE_CHANGES_ONLY");
	}

	public static String getQuestionTestPhoneCompatibility() {
		return MessageTranslator.translate("EPIINFO_COMPACT_QUESTION_TEST_PHONE_COMPATIBILITY");
	}

	public static String getQuestionForReadyToSync() {
		return MessageTranslator.translate("EPIINFO_COMPACT_QUESTION_READY_TO_SYNC");
	}

	public static String getAnswerReadyToSync(String dataSourceAlias) {
		return MessageTranslator.translate("EPIINFO_COMPACT_ANSWER_READY_TO_SYNC", dataSourceAlias);
	}

	public static String getAnswerNotReadyToSync(String dataSourceAlias) {
		return MessageTranslator.translate("EPIINFO_COMPACT_ANSWER_NOT_READY_TO_SYNC", dataSourceAlias);
	}

	public static String getQuestionEndSymbol() {
		return MessageTranslator.translate("EPIINFO_COMPACT_QUESTION_END_SYMBOL");
	}

	public static String getLabelMSAccessMDB() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_MDB_FILE_NAME");
	}

	public static String getLabelMSAccessTableName() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_MDB_TABLE_NAME");
	}

	public static String getMessageEditContactRequiredFields() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_EDIT_CONTACT_REQUIRED_FIELDS");
	}

	public static String getLabelSave() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_SAVE");
	}

	public static String getLabelDelete() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_DELETE");
	}

	public static String getMessageEditDataSourceRequiredFields() {
		return MessageTranslator.translate("EPIINFO_COMPACT_MESSAGE_EDIT_DATA_SOURCE_REQUIRED_FIELDS");
	}

	public static String getLabelTabContacts() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_TAB_CONTACTS");
	}

	public static String getLabelTabDataSources() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_TAB_DATA_SOURCES");
	}

	public static String getLabelTabModem() {
		return MessageTranslator.translate("EPIINFO_COMPACT_LABEL_TAB_MODEM");
	}
}
