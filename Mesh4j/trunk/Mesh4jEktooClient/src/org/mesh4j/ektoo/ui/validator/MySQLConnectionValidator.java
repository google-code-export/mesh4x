package org.mesh4j.ektoo.ui.validator;

import javax.swing.JComponent;

import org.mesh4j.ektoo.controller.MySQLUIController;
import org.mesh4j.ektoo.model.AbstractModel;
import org.mesh4j.ektoo.model.MySQLAdapterModel;
import org.mesh4j.ektoo.ui.MySQLUI;
import org.mesh4j.ektoo.ui.translator.EktooUITranslator;
import org.mesh4j.ektoo.validator.AbstractValidator;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public class MySQLConnectionValidator extends AbstractValidator {
	
	private boolean mustValidateTable = false;
	// BUSINESS METHODS
//	public MySQLConnectionValidator(JComponent form, AbstractModel model) {
//		super(form, model);
//	}
	
	public MySQLConnectionValidator(JComponent form, AbstractModel model,boolean mustValidateTable) {
		super(form, model);
		this.mustValidateTable = mustValidateTable;
	}
	
	@Override
	protected boolean validate() {
		// model based validation
		// return validate((MySQLAdapterModel)this.model);
		
		// form based validation
		return validate((MySQLUI) this.form, this.mustValidateTable);
	
	}

	protected boolean validate(MySQLAdapterModel mysqlModel) {
		boolean isValid = true;
		if (mysqlModel == null) {
			setError(MySQLAdapterModel.class.getName(), EktooUITranslator
					.getErrorEmptyOrNull(MySQLAdapterModel.class.getName()));
			isValid = false;
		}

		String userName = mysqlModel.getUserName();
		if (userName == null || userName.trim().length() == 0) {
			setError(MySQLUIController.USER_NAME_PROPERTY, EktooUITranslator
					.getErrorEmptyOrNull(MySQLUIController.USER_NAME_PROPERTY));
			isValid = false;
		}

		String hostName = mysqlModel.getHostName();
		if (hostName == null || hostName.trim().length() == 0) {
			setError(MySQLUIController.HOST_NAME_PROPERTY, EktooUITranslator
					.getErrorEmptyOrNull(MySQLUIController.HOST_NAME_PROPERTY));
			isValid = false;
		}

		int portNo = mysqlModel.getPortNo();
		if (portNo < 0) {
			setError(MySQLUIController.PORT_NO_PROPERTY, EktooUITranslator
					.getErrorInvalid(MySQLUIController.PORT_NO_PROPERTY));
			isValid = false;
		}

		String databaseName = mysqlModel.getDatabaseName();
		if (databaseName == null || databaseName.trim().length() == 0) {
			setError(
					MySQLUIController.DATABASE_NAME_PROPERTY,
					EktooUITranslator
							.getErrorEmptyOrNull(MySQLUIController.DATABASE_NAME_PROPERTY));
			isValid = false;
		}
		return isValid;
	}

	private boolean validate(JComponent form, boolean mustValidateTable) {
		boolean isValid = true;

		MySQLUI ui = (MySQLUI) form;

		if (ui.getUserText().getText().trim().length() == 0) {
			setError(ui.getUserText(), EktooUITranslator
					.getErrorEmptyOrNull(MySQLUIController.USER_NAME_PROPERTY));
			isValid = false;
		}

		if (new String(ui.getPassText().getPassword()).trim().length() == 0) {
			setError(
					ui.getPassText(),
					EktooUITranslator
							.getErrorEmptyOrNull(MySQLUIController.USER_PASSWORD_PROPERTY));
			isValid = false;
		}

		if (ui.getHostText().getText().trim().length() == 0) {
			setError(ui.getHostText(), EktooUITranslator
					.getErrorEmptyOrNull(MySQLUIController.HOST_NAME_PROPERTY));
			isValid = false;
		}

		if (ui.getPortText().getText().trim().length() == 0) {
			setError(ui.getPortText(), EktooUITranslator
					.getErrorEmptyOrNull(MySQLUIController.PORT_NO_PROPERTY));
			isValid = false;
		}

		if (ui.getDatabaseText().getText().trim().length() == 0) {
			setError(
					ui.getDatabaseText(),
					EktooUITranslator
							.getErrorEmptyOrNull(MySQLUIController.DATABASE_NAME_PROPERTY));
			isValid = false;
		}

		//validating the table 
		if(mustValidateTable){
			if (ui.getTableList().getItemCount() == 0) {
				setError(ui.getTableList(), EktooUITranslator
						.getErrorEmptyOrNull(MySQLUIController.TABLE_NAME_PROPERTY));
				isValid = false;
			}
		} 

		return isValid;
	}

	public boolean validateToConnect() {
		return validate((MySQLUI) this.form, false); 
	}
}
