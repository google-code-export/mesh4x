package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.FeedUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.KmlModel;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.ui.FeedUI;
import org.mesh4j.ektoo.ui.KmlUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class KmlUIValidator extends AbstractValidator {
	
	
	public KmlUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		// model based validation
		// return validate((MySQLAdapterModel)this.model);
		
		// form based validation
		return validate((KmlUI) this.form);
	
	}

	protected boolean validate(KmlModel mysqlModel) {
		boolean isValid = true;
		return isValid;
	}

	private boolean validate(JComponent form) {
		boolean isValid = true;

		KmlUI ui = (KmlUI) form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getFileNameText()))
				&& ui.getFileNameText().getText().trim().length() == 0) {
			setError(ui.getFileNameText(), EktooUITranslator
					.getErrorEmptyOrNull(FeedUIController.FILE_NAME_PROPERTY));
			isValid = false;
		}

		return isValid;
	}

//	public boolean validateToConnect() {
//		return validate((MySQLUI) this.form); 
//	}
}
