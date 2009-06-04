package org.mesh4j.ektoo.validator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.mesh4j.ektoo.model.AbstractModel;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */

public abstract class AbstractValidator implements IValidator {
	
	// MODEL VARIABLES
	private Object parent;
	protected Object form;
	private List<JComponent> uiFieldListForValidation = new ArrayList<JComponent>();

	private Hashtable<Object, String> errorTable = new Hashtable<Object, String>();

	// BUSINESS METHODS
	protected AbstractModel model;

	public AbstractValidator(JFrame parent, JComponent form, List<JComponent> uiFieldListForValidation) {
		this.parent = parent;
		this.form = form;
		this.uiFieldListForValidation = uiFieldListForValidation == null ? new ArrayList<JComponent>()
				: uiFieldListForValidation;
	}

	public AbstractValidator(JComponent form, AbstractModel model, List<JComponent> uiFieldListForValidation) {
		this.form = form;
		this.model = model;
		this.uiFieldListForValidation = uiFieldListForValidation == null ? new ArrayList<JComponent>():uiFieldListForValidation;
	}

	protected abstract boolean validate();

	public boolean verify() {
		if (!validate()) {
			if (form instanceof IValidationStatus)
				((IValidationStatus) form).validationFailed(errorTable);
			return false;
		}

		if (form instanceof IValidationStatus)
			((IValidationStatus) form).validationPassed();

		return true;
	}

	// for model
	public void setError(String attr, String error) {
		this.errorTable.put(attr, error);
	}

	// forform
	public void setError(JComponent c, String error) {
		this.errorTable.put(c, error);
	}

	public Hashtable<Object, String> getErrorTable() {
		return this.errorTable;
	}

	protected Object getLocalParent() {
		return this.parent;
	}

	public List<JComponent> getUiFieldListForValidation() {
		return uiFieldListForValidation;
	}
}
