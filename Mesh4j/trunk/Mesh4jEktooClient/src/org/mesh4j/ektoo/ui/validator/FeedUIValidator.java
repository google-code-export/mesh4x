package org.mesh4j.ektoo.ui.validator;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.FeedUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.FeedUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

public class FeedUIValidator extends AbstractValidator {
	
	
	public FeedUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		return validate((FeedUI) this.form);
	}

	private boolean validate(FeedUI ui) {
		boolean isValid = true;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getFileNameText()))
				&& ui.getFileNameText().getText().trim().length() == 0) {
			setError(ui.getFileNameText(), EktooUITranslator.getErrorEmptyOrNull(FeedUIController.FILE_NAME_PROPERTY));
			isValid = false;
		} else {
			File file = new File(ui.getFileNameText().getText());
			if(!file.exists() && !ui.getController().acceptsCreateDataset()){
				setError(ui.getFileNameText(), EktooUITranslator.getErrorNotExists(FeedUIController.FILE_NAME_PROPERTY));
				isValid = false;
			}
		}

		return isValid;
	}

}
