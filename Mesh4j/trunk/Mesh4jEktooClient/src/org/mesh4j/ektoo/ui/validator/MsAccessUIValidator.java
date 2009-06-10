package org.mesh4j.ektoo.ui.validator;

import java.io.File;
import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.MsAccessUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;


/**
 * @author sharif uddin
 *
 */
public class MsAccessUIValidator extends AbstractValidator {

	
	public MsAccessUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		return validate((MsAccessUI) this.form);
	
	}

	private boolean validate(MsAccessUI ui) {
		boolean isValid = true;		
		
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTxtFile()))
				&& ui.getTxtFile().getText().trim().length() == 0) {
			setError(ui.getTxtFile(), EktooUITranslator
					.getErrorEmptyOrNull(MsAccessUIController.DATABASE_NAME_PROPERTY));
			isValid = false;
		} else {
			File file = new File(ui.getTxtFile().getText());
			if(!file.exists()){
				setError(ui.getTxtFile(), EktooUITranslator.getErrorNotExists(MsAccessUIController.DATABASE_NAME_PROPERTY));
				isValid = false;
			}
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTableList()))
				&& ui.getTableList().getItemCount() == 0) {
			setError(ui.getTableList(), EktooUITranslator
					.getErrorEmptyOrNull(MsAccessUIController.TABLE_NAME_PROPERTY));
			isValid = false;
		}

		return isValid;
	}

}
