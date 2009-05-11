package org.mesh4j.ektoo.validator;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.mesh4j.ektoo.IValidationStatus;
import org.mesh4j.ektoo.model.AbstractModel;

/**
 * @author Bhuiyan Mohammad Iklash
 * 
 */
public abstract class AbstractValidator {
	protected Object getParent() {
		return parent;
	}

	protected Object getForm() {
		return form;
	}

	protected AbstractModel getModel() {
		return model;
	}

	private Object parent;
	private Object form;
	private AbstractModel model;

	public AbstractValidator(JFrame parent, JComponent form) {
		this.parent = parent;
		this.form = form;
	}

	public AbstractValidator(JComponent form, AbstractModel model) {
		this.form = form;
		this.model = model;
	}

	protected abstract boolean validate();

	public boolean verify() {
		if (!validate()) {
			if (form instanceof IValidationStatus)
				((IValidationStatus) form).validationFailed();
			return false;
		}

		if (form instanceof IValidationStatus)
			((IValidationStatus) form).validationPassed();

		return true;
	}
}
