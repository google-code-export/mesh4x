package org.mesh4j.sync.ui.translator;

import org.mesh4j.sync.translator.MessageTranslator;

public class Mesh4jUITranslator {

	// Internationalization
	
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
	
	public static String getMessagePurgeKML(String kmlFile) {
		return MessageTranslator.translate("Mesh4jUI_PURGE_KML_START", kmlFile);
	}

	public static String getMessagePurgeKMLCompleted(String result) {
		return MessageTranslator.translate("Mesh4jUI_PURGE_KML_COMPLETED", result);
	}

	public static String getMessagePurgeKMLSuccessfuly() {
		return MessageTranslator.translate("Mesh4jUI_PURGE_KML_SUCCESSFULY");
	}

	public static String getMessagePurgeKMLFailed() {
		return MessageTranslator.translate("Mesh4jUI_PURGE_KML_FAILED");
	}

	public static String getLabelPurge() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_PURGE");
	}

	public static String getLabelGroupSync() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_SYNC_GROUP");
	}
	public static String getLabelGroupMaintenance() {
		return MessageTranslator.translate("Mesh4jUI_LABEL_MAINTENANCE_GROUP");
	}
}
