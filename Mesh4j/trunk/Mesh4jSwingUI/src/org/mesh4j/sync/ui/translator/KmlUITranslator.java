package org.mesh4j.sync.ui.translator;

import org.mesh4j.sync.translator.MessageTranslator;

public class KmlUITranslator {

	// Internationalization

	public static String getLabelKMLExtensions(){
		return MessageTranslator.translate("Mesh4jUI_LABEL_KML_EXTENSIONS");	
	}
	
	public static String getLabelFileExtensions(){
		return MessageTranslator.translate("Mesh4jUI_LABEL_FILE_EXTENSIONS");	
	}
	
	public static String getLabelFileChooser(){
		return MessageTranslator.translate("Mesh4jUI_LABEL_FILE_CHOOSER");	
	}
	
	public static String getToolTipPrepareToSync(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_PREPARE_TO_SYNC");	
	}
	
	public static String getToolTipCleanSyncInfo(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_CLEAN_SYNC_INFO");	
	}
	
	public static String getToolTipPurge(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_PURGE");	
	}
	
	
	public static String getToolTipKMLFile(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_KML_FILE");	
	}
	
	public static String getToolTipCleanConsole(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_CLEAN_CONSOLE");	
	}
	
	public static String getToolTipSync(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_SYNC");	
	}
	
	public static String getToolTipConsole(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_CONSOLE");	
	}
	
	public static String getToolTipFile(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_FILE");	
	}
	
	public static String getToolTipFileChooser(){
		return MessageTranslator.translate("Mesh4jUI_TOOL_TIP_FILE_CHOOSER");	
	}	
	
	public static String getLabelPrepareToSync() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_PREPARE_TO_SYNC");
	}

	public static String getLabelKMLFile() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_KML_FILE");
	}

	public static String getLabelClean() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_CLEAN");
	}

	public static String getLabelSyncronize() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_SYNCRONIZE");
	}

	public static String getLabelEndpoint1() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_ENDPOINT1");
	}

	public static String getLabelEndpoint2() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_ENDPOINT2");
	}
	
	public static String getTitle() {
		return MessageTranslator.translate("Mesh4jUI_TITLE");
	}
	
	public static String getMessageSyncStart() {
		return MessageTranslator.translate("Mesh4jUI_SYNC_START");
	}
	
	public static String getMessageSyncCompleted(String result) {
		return MessageTranslator.translate("Mesh4jUI_SYNC_COMPLETED", result);
	}
	
	public static String getMessageSyncFailed() {
		return MessageTranslator.translate("Mesh4jUI_SYNC_FAILED");
	}

	public static String getMessageSyncCompletedWithConflicts(int conflicts) {
		return MessageTranslator.translate("Mesh4jUI_SYNC_COMPLETED_WITH_CONFLICTS", conflicts);
	}

	public static String getMessageSyncSuccessfully() {
		return MessageTranslator.translate("Mesh4jUI_SYNC_SUCCESSFULY");
	}
	
	public static String getErrorSameEndpoints() {
		return MessageTranslator.translate("Mesh4jUI_ERROR_SAME_ENDPOINTS");
	}
	
	public static String getErrorEndpoint(String endpointHeader) {
		return MessageTranslator.translate("Mesh4jUI_ERROR_ENDPOINT", get(endpointHeader));
	}
	private static String get(String key) {
		return MessageTranslator.translate("Mesh4jUI_"+key);
	}

	public static String getErrorInvalidURL(String endpointHeader) {
		return MessageTranslator.translate("Mesh4jUI_ERROR_INVALID_URL", get(endpointHeader));
	}
	public static String getErrorURLConnectionFailed(String endpointHeader) {
		return MessageTranslator.translate("Mesh4jUI_ERROR_URL_CONNECTION", get(endpointHeader));
	}
	public static String getErrorFileType(String endpointHeader) {
		return MessageTranslator.translate("Mesh4jUI_ERROR_FILE_TYPE", get(endpointHeader));
	}
	public static String getErrorKMLType(String endpointHeader) {
		return MessageTranslator.translate("Mesh4jUI_ERROR_KML_TYPE", get(endpointHeader));
	}

	public static String getErrorFileDoesNotExist(String endpointHeader) {
		return MessageTranslator.translate("Mesh4jUI_ERROR_FILE_DOES_NOT_EXIST", get(endpointHeader));
	}
	
	public static String getMessagePrepareToSync(String kmlFile) {
		return MessageTranslator.translate("Mesh4jUI_PREPARE_KML_START", kmlFile);
	}
	
	public static String getMessagePrepareToSyncCompleted(
			String result) {
		return MessageTranslator.translate("Mesh4jUI_PREPARE_KML_COMPLETED", result);
	}
	
	public static String getMessagePrepareToSyncSuccessfuly() {
		return MessageTranslator.translate("Mesh4jUI_PREPARE_KML_SUCCESSFULY");
	}
	
	public static String getMessagePrepareToSyncFailed() {
		return MessageTranslator.translate("Mesh4jUI_PREPARE_KML_FAILED");
	}

	public static String getMessageCleanKML(String kmlFile) {
		return MessageTranslator.translate("Mesh4jUI_CLEAN_KML_START", kmlFile);
	}

	public static String getMessageCleanKMLCompleted(String result) {
		return MessageTranslator.translate("Mesh4jUI_CLEAN_KML_COMPLETED", result);
	}

	public static String getMessageCleanKMLSuccessfuly() {
		return MessageTranslator.translate("Mesh4jUI_CLEAN_KML_SUCCESSFULY");
	}

	public static String getMessageCleanKMLFailed() {
		return MessageTranslator.translate("Mesh4jUI_CLEAN_KML_FAILED");
	}
	
	public static String getMessagePurgueKML(String kmlFile) {
		return MessageTranslator.translate("Mesh4jUI_PURGUE_KML_START", kmlFile);
	}

	public static String getMessagePurgueKMLCompleted(String result) {
		return MessageTranslator.translate("Mesh4jUI_PURGUE_KML_COMPLETED", result);
	}

	public static String getMessagePurgueKMLSuccessfuly() {
		return MessageTranslator.translate("Mesh4jUI_PURGUE_KML_SUCCESSFULY");
	}

	public static String getMessagePurgueKMLFailed() {
		return MessageTranslator.translate("Mesh4jUI_PURGUE_KML_FAILED");
	}

	public static String getLabelPurgue() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_PURGUE");
	}

	public static String getGroupSync() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_GROUP_SYNC");
	}

	public static String getGroupKML() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_GROUP_KML");
	}

	public static String getToolTipTradeMark(String mesh4xURL) {
		return MessageTranslator.translate("Mesh4jUI_TOOLTIP_TRADEMARK", mesh4xURL);
	}

	public static String getTradeMark() {
		return MessageTranslator.translate("Mesh4jUI_TRADEMARK");
	}

	public static String getTooltipView() {
		return MessageTranslator.translate("Mesh4jUI_TOOLTIP_VIEW");
	}

	public static String getMessageWelcome(String userName) {
		return MessageTranslator.translate("Mesh4jUI_MESSAGE_WELCOME", userName);
	}

	public static String getLogWindowTitle() {
		return MessageTranslator.translate("Mesh4jUI_LOG_WINDOW_TITLE");
	}
	
	public static String getLogWindowToolTipConsoleView() {
		return MessageTranslator.translate("Mesh4jUI_LOG_WINDOW_TOOLTIP_CONSOLE_VIEW");
	}
	
	public static String getLogWindowLabelClean() {
		return MessageTranslator.translate("Mesh4jUI_LOG_WINDOW_LABEL_CLEAN");
	}
	
	public static String getLogWindowToolTipClean() {
		return MessageTranslator.translate("Mesh4jUI_LOG_WINDOW_TOOLTIP_CLEAN");
	}
	
	public static String getLogWindowLabelClose() {
		return MessageTranslator.translate("Mesh4jUI_LOG_WINDOW_LABEL_CLOSE");
	}
	
	public static String getLogWindowToolTipClose() {
		return MessageTranslator.translate("Mesh4jUI_LOG_WINDOW_TOOLTIP_CLOSE");
	}

	public static String getKmlManagerWindowTitle() {
		return MessageTranslator.translate("Mesh4jUI_KML_WINDOW_TITLE");
	}

	public static String getErrorImpossibleToOpenFileBecauseFileDoesNotExists() {
		return MessageTranslator.translate("Mesh4jUI_ERROR_IMPOSSIBLE_OPEN_FILE_BECAUSE_FILE_DOES_NOT_EXISTS");
	}
	
	public static String getErrorOpenFileActionNotSupported(){
		return MessageTranslator.translate("Mesh4jUI_ERROR_OPEN_FILE_ACTION_NOT_SUPPORTED");
	}

	public static String getErrorOpenBrowser() {
		return MessageTranslator.translate("Mesh4jUI_ERROR_OPEN_BROWSER");
	}

	public static String getMessageOpenBrowserActionNotSupported() {
		return MessageTranslator.translate("Mesh4jUI_ERROR_OPEN_BROWSER_ACTION_NOT_SUPPORTED");
	}

	public static String getLabelOpenLogWindow() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_OPEN_LOG_WINDOW");
	}


	public static String getToolTipOpenLogWindow() {
		return MessageTranslator.translate("Mesh4jUI_TOOLTIP_OPEN_LOG_WINDOW");
	}

	public static String getLabelOpenKmlManagerWindow() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_OPEN_KML_MANAGER_WINDOW");
	}

	public static String getToolTipOpenKmlManagerWindow() {
		return MessageTranslator.translate("Mesh4jUI_TOOLTIP_OPEN_KML_MANAGER_WINDOW");
	}
}
