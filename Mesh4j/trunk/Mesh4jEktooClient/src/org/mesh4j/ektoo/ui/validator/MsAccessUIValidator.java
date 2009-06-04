package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.MsAccessUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.MsAccessModel;
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
		// model based validation
		// return validate((MySQLAdapterModel)this.model);
		
		// form based validation
		return validate((MsAccessUI) this.form);
	
	}

	protected boolean validate(MsAccessModel mysqlModel) {
		boolean isValid = true;
		return isValid;
	}

	private boolean validate(JComponent form) {
		boolean isValid = true;

		MsAccessUI ui = (MsAccessUI) form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getTxtFile()))
				&& ui.getTxtFile().getText().trim().length() == 0) {
			setError(ui.getTxtFile(), EktooUITranslator
					.getErrorEmptyOrNull(MsAccessUIController.DATABASE_NAME_PROPERTY));
			isValid = false;
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
