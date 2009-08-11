package org.mesh4j.ektoo.ui.validator;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.KmlUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.KmlUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

public class KmlUIValidator extends AbstractValidator {
	
	
	public KmlUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		return validate((KmlUI) this.form);
	
	}

	private boolean validate(KmlUI ui) {
		boolean isValid = true;
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getFileNameText()))
				&& ui.getFileNameText().getText().trim().length() == 0) {
			setError(ui.getFileNameText(), EktooUITranslator
					.getErrorEmptyOrNull(KmlUIController.FILE_NAME_PROPERTY));
			isValid = false;
		}else {
			File file = new File(ui.getFileNameText().getText());
			if(!file.exists() && !ui.getController().acceptsCreateDataset()){
				setError(ui.getFileNameText(), EktooUITranslator.getErrorNotExists(KmlUIController.FILE_NAME_PROPERTY));
				isValid = false;
			}
		}

		return isValid;
	}

}
