package org.mesh4j.ektoo.ui.validator;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.MsAccessMultiTableUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.MsAccessMultiTableUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

public class MsAccessMultiTableUIValidator extends AbstractValidator {

	public MsAccessMultiTableUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		return validate((MsAccessMultiTableUI) this.form);
	
	}

	private boolean validate(MsAccessMultiTableUI ui) {
		boolean isValid = true;		
		
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTxtFile()))
				&& ui.getTxtFile().getText().trim().length() == 0) {
			setError(ui.getTxtFile(), EktooUITranslator
					.getErrorEmptyOrNull(MsAccessMultiTableUIController.DATABASE_NAME_PROPERTY));
			isValid = false;
		} else {
			File file = new File(ui.getTxtFile().getText());
			if(!file.exists()){
				setError(ui.getTxtFile(), EktooUITranslator.getErrorNotExists(MsAccessMultiTableUIController.DATABASE_NAME_PROPERTY));
				isValid = false;
			}
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getListTables()))
				&& ui.getListTables().getSelectedIndices().length == 0) {
			setError(ui.getListTables(), EktooUITranslator
					.getErrorEmptyOrNull(MsAccessMultiTableUIController.TABLE_NAMES_PROPERTY));
			isValid = false;
		}

		return isValid;
	}

}
