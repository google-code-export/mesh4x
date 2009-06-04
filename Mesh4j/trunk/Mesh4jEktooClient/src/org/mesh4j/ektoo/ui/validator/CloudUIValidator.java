package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.CloudUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;


/**
 * @author sharif uddin
 *
 */
public class CloudUIValidator extends AbstractValidator {
	
	
	public CloudUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
	}
	
	@Override
	protected boolean validate() {
		boolean isValid = true;

		CloudUI ui = (CloudUI) this.form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getMashText()))
				&& ui.getMashText().getText().trim().length() == 0) {
			setError(ui.getMashText(), EktooUITranslator
					.getErrorEmptyOrNull(CloudUIController.MESH_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getDataSetText()))
				&& ui.getDataSetText().getText().trim().length() == 0) {
			setError(ui.getDataSetText(), EktooUITranslator
					.getErrorEmptyOrNull(CloudUIController.DATASET_NAME_PROPERTY));
			isValid = false;
		}

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getSyncURIText()))
				&& ui.getSyncURIText().getText().trim().length() == 0) {
			setError(ui.getSyncURIText(), EktooUITranslator
					.getErrorEmptyOrNull(CloudUIController.SYNC_SERVER_URI));
			isValid = false;
		}
		return isValid;
	}

}
