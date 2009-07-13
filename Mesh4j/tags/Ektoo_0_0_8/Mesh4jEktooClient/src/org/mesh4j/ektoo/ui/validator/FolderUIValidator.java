package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.FolderUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.FolderUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

public class FolderUIValidator extends AbstractValidator {
		
	public FolderUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		return validate((FolderUI) this.form);
	}

	private boolean validate(FolderUI ui) {
		boolean isValid = true;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getFileNameText()))
				&& ui.getFileNameText().getText().trim().length() == 0) {
			setError(ui.getFileNameText(), EktooUITranslator.getErrorEmptyOrNull(FolderUIController.FOLDER_NAME_PROPERTY));
			isValid = false;
		}
		return isValid;
	}

}
