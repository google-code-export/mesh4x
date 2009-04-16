package org.mesh4j.ektoo.ui.translator;

import org.mesh4j.translator.EktooMessageTranslator;

public class EktooUITranslator {
	
	public static String getSelectExcel() {
		return EktooMessageTranslator.translate("EKTOO_SELECT_EXCEL");
	}
	
	public static String getReturnExcel() {
		return EktooMessageTranslator.translate("RETURN_EXCEL");
	}
// TODO (JMT) add resource bundle
	public static String getMessageConflicts() {
		return EktooMessageTranslator.translate("EKTOO_CONTROLLER_SYNC_WITH_CONFLICTS");
	}

	public static String getMessageSyncSyccessfuly() {
		return EktooMessageTranslator.translate("EKTOO_CONTROLLER_SYNC_SUCCESSFULY");
	}

	public static String getLabelSource() {
		return "Source";
	}

	public static String getLabelTarget() {
		return "Target";
	}
	
}
