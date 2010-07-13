package org.mesh4j.meshes.ui;

public interface Editable {
	
	boolean isDirty();
	void setEditableListener(EditableListener listener);
	void saveChanges();
	void discardChanges();
	
	public interface EditableListener {
		void dirtyChanged(boolean isDirty);
	}
}
