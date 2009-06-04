package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.MsExcelModel;
import org.mesh4j.ektoo.ui.MsExcelUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;


/**
 * @author sharif uddin
 *
 */
public class MsExcelUIValidator extends AbstractValidator {
	
	
	public MsExcelUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		// model based validation
		// return validate((MySQLAdapterModel)this.model);
		
		// form based validation
		return validate((MsExcelUI) this.form);
	
	}

	protected boolean validate(MsExcelModel mysqlModel) {
		boolean isValid = true;
		return isValid;
	}

	private boolean validate(JComponent form) {
		boolean isValid = true;

		MsExcelUI ui = (MsExcelUI) form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTxtFile()))
				&& ui.getTxtFile().getText().trim().length() == 0) {
			setError(ui.getTxtFile(), EktooUITranslator
					.getErrorEmptyOrNull(MsExcelUIController.WORKBOOK_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTableList()))
				&& ui.getTableList().getItemCount() == 0) {
			setError(ui.getTableList(), EktooUITranslator
					.getErrorEmptyOrNull(MsExcelUIController.WORKSHEET_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getColumnList()))
				&& ui.getColumnList().getItemCount() == 0) {
			setError(ui.getColumnList(), EktooUITranslator
					.getErrorEmptyOrNull(MsExcelUIController.UNIQUE_COLUMN_NAME_PROPERTY));
			isValid = false;
		}		

		return isValid;
	}

}
