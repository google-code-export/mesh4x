package org.mesh4j.ektoo.ui.validator;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.MsExcelUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.MsExcelUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

public class MsExcelUIValidator extends AbstractValidator {
	
	public MsExcelUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		return validate((MsExcelUI) this.form);
	
	}

	private boolean validate(MsExcelUI ui) {
		boolean isValid = true;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTxtFile()))
				&& ui.getTxtFile().getText().trim().length() == 0) {
			setError(ui.getTxtFile(), EktooUITranslator
					.getErrorEmptyOrNull(MsExcelUIController.WORKBOOK_NAME_PROPERTY));
			isValid = false;
		} else {
			File file = new File(ui.getTxtFile().getText());
			if(!file.exists() && !ui.getController().acceptsCreateDataset()){
				setError(ui.getTxtFile(), EktooUITranslator.getErrorNotExists(MsExcelUIController.WORKBOOK_NAME_PROPERTY));
				isValid = false;
			}
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTableList()))
				&& !ui.getController().acceptsCreateDataset() && ui.getTableList().getItemCount() == 0) {
			setError(ui.getTableList(), EktooUITranslator
					.getErrorEmptyOrNull(MsExcelUIController.WORKSHEET_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getColumnList()))
				&& !ui.getController().acceptsCreateDataset() && ui.getColumnList().getItemCount() == 0) {
			setError(ui.getColumnList(), EktooUITranslator
					.getErrorEmptyOrNull(MsExcelUIController.UNIQUE_COLUMN_NAME_PROPERTY));
			isValid = false;
		}		

		return isValid;
	}

}
