package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.GSSheetUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.GSSheetModel;
import org.mesh4j.ektoo.ui.GSSheetUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;


/**
 * @author sharif uddin
 *
 */
public class GssUIValidator extends AbstractValidator {

	
	public GssUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		// model based validation
		// return validate((MySQLAdapterModel)this.model);
		
		// form based validation
		return validate((GSSheetUI) this.form);
	
	}

	protected boolean validate(GSSheetModel mysqlModel) {
		boolean isValid = true;
		return isValid;
	}

	private boolean validate(JComponent form) {
		boolean isValid = true;

		GSSheetUI ui = (GSSheetUI) form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getUserText()))
				&& ui.getUserText().getText().trim().length() == 0) {
			setError(ui.getUser(), EktooUITranslator
					.getErrorEmptyOrNull(GSSheetUIController.USER_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getPassText()))
				&& new String(ui.getPassText().getPassword()).trim().length() == 0) {
			setError(ui.getPass(), EktooUITranslator
					.getErrorEmptyOrNull(GSSheetUIController.USER_PASSWORD_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getNameList()))
				&& ui.getNameList().getItemCount() == 0) {
			setError(ui.getNameList(), EktooUITranslator
					.getErrorEmptyOrNull(GSSheetUIController.SPREADSHEET_NAME_PROPERTY));
			isValid = false;
		}
		
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTableList()))
				&& ui.getTableList().getItemCount() == 0) {
			setError(ui.getTableList(), EktooUITranslator
					.getErrorEmptyOrNull(GSSheetUIController.WORKSHEET_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getColumnList()))
				&& ui.getColumnList().getItemCount() == 0) {
			setError(ui.getColumnList(), EktooUITranslator
					.getErrorEmptyOrNull(GSSheetUIController.UNIQUE_COLUMN_NAME_PROPERTY));
			isValid = false;
		}

		return isValid;
	}

}
