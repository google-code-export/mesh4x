package org.mesh4j.meshes.ui.component;

import javax.swing.JComponent;

import org.mesh4j.meshes.ui.Editable;

@SuppressWarnings("serial")
public abstract class EditableComponent extends JComponent implements Editable {

	private EditableListener listener;
		
	@Override
	public void discardChanges() {
		loadModel();
		notifyEditableListener();
	}

	@Override
	public void setEditableListener(EditableListener listener) {
		this.listener = listener;
	}

	protected void notifyEditableListener() {
		if (listener != null)
			listener.dirtyChanged(isDirty());
	}

	protected abstract void loadModel();
}
