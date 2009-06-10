package org.mesh4j.ektoo.ui.validator;

import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.CloudUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;


/**
 * @author sharif uddin
 *
 */
public class CloudUIValidator extends AbstractValidator {
	
	// MODEL VARIABLES
	private boolean mustValidateDataset = true;
	
	// BUSINESS METHODS
	public CloudUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
		this.mustValidateDataset = true;
	}
	public CloudUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation, boolean mustValidateDataset) {
		super(form, model, uiFieldListForValidation);
		this.mustValidateDataset = mustValidateDataset;
	}
	
	@Override
	protected boolean validate() {
		boolean isValid = true;

		CloudUI ui = (CloudUI) this.form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getServerURLText()))
				&& ui.getServerURLText().getText().trim().length() == 0) {
			setError(ui.getServerURLText(), EktooUITranslator
					.getErrorEmptyOrNull(CloudUIController.SYNC_SERVER_URI));
			isValid = false;
		}
		
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getMeshText()))
				&& ui.getMeshText().getText().trim().length() == 0) {
			setError(ui.getMeshText(), EktooUITranslator
					.getErrorEmptyOrNull(CloudUIController.MESH_NAME_PROPERTY));
			isValid = false;
		}

		if(this.mustValidateDataset){
			if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getDataSetText()))
					&& ui.getDataSetText().getText().trim().length() == 0) {
				setError(ui.getDataSetText(), EktooUITranslator
						.getErrorEmptyOrNull(CloudUIController.DATASET_NAME_PROPERTY));
				isValid = false;
			}
		}

		return isValid;
	}

}
