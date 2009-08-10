package org.mesh4j.ektoo.ui.validator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.CloudUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.ui.CloudUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

public class CloudUIValidator extends AbstractValidator {
	
	// MODEL VARIABLES
	private boolean mustValidateDatasetAndMeshGroup = true;
	
	// BUSINESS METHODS
	public CloudUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		super(form, model, uiFieldListForValidation);
		this.mustValidateDatasetAndMeshGroup = true;
	}
	
	public CloudUIValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation, boolean mustValidateDataset) {
		super(form, model, uiFieldListForValidation);
		this.mustValidateDatasetAndMeshGroup = mustValidateDataset;
	}
	
	@Override
	protected boolean validate() {
		boolean isValid = true;

		CloudUI ui = (CloudUI) this.form;

		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getServerURLText()))){
			String serverURL = ui.getServerURLText().getText();
			if(serverURL.trim().length() == 0) {
				setError(ui.getServerURLText(), EktooUITranslator.getErrorEmptyOrNull(CloudUIController.SYNC_SERVER_URI));
				isValid = false;
			} else {
				// TODO REMOVE this validation - server subfix
				if(isInvalidServer(serverURL, "mesh4x/feeds") && isInvalidServer(serverURL, "mesh4x_test/feeds")){
					setError(ui.getServerURLText(), EktooUITranslator.getErrorInvalidURL());
					isValid = false;
				}
			}
		}else{
			try {
				new URL(ui.getServerURLText().getText().trim());
			} catch (MalformedURLException e) {
				setError(ui.getServerURLText(), EktooUITranslator.getErrorInvalidURL());
				isValid = false;
			}
		}
		
		String meshName = ui.getMeshText().getText();
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getMeshText()))){
				if(meshName.trim().length() == 0) {
					if(mustValidateDatasetAndMeshGroup){
						setError(ui.getMeshText(), EktooUITranslator.getErrorEmptyOrNull(CloudUIController.MESH_NAME_PROPERTY));
						isValid = false;
					}
				}else if(meshName.contains(" ") || meshName.contains("/") || meshName.contains("\\")){
					setError(ui.getMeshText(), EktooUITranslator.getErrorInvalidMeshName());
					isValid = false;
				}
		}

		String dataSetName = ui.getDataSetText().getText();
		if ((getUiFieldListForValidation().isEmpty() || getUiFieldListForValidation().contains(ui.getDataSetText()))){
			if(dataSetName.trim().length() == 0) {
				if(mustValidateDatasetAndMeshGroup){
					setError(ui.getDataSetText(), EktooUITranslator.getErrorEmptyOrNull(CloudUIController.DATASET_NAME_PROPERTY));
					isValid = false;
				}
			}else if(dataSetName.contains(" ") || dataSetName.contains("/") || dataSetName.contains("\\")){
				setError(ui.getDataSetText(), EktooUITranslator.getErrorInvalidDataSetName());
				isValid = false;
			}
		}
		
		if(isValid){
			if(meshName.trim().length() == 0 && dataSetName.trim().length() > 0){
				setError(ui.getMeshText(), EktooUITranslator.getErrorEmptyOrNull(CloudUIController.MESH_NAME_PROPERTY));
				isValid = false;
			}
		}

		return isValid;
	}

	private boolean isInvalidServer(String serverURL, String serverSubfix) {
		return serverURL.contains(" ") || !serverURL.trim().endsWith(serverSubfix) || serverURL.indexOf(serverSubfix) != serverURL.lastIndexOf(serverSubfix);
	}

}
