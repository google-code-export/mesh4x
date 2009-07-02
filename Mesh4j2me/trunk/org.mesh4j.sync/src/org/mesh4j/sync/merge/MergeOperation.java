package org.mesh4j.sync.merge;

import de.enough.polish.java5.Enum;

public class MergeOperation extends Enum {

	protected MergeOperation(String name, int ordinal) {
		super(name, ordinal);
	}
	
	public static MergeOperation Added = new MergeOperation("Added", 0);
	public static MergeOperation Updated = new MergeOperation("Updated", 1);
	public static MergeOperation Conflict = new MergeOperation("Conflict", 2);
	public static MergeOperation Removed = new MergeOperation("Removed", 3);
	public static MergeOperation None = new MergeOperation("None", 4);
	
	public boolean isAdded() {
		return this.equals(Added);
	}
	
	public boolean isUpdated() {
		return this.equals(Updated);
	}
	
	public boolean isConflict() {
		return this.equals(Conflict);
	}
	
	public boolean isRemoved() {
		return this.equals(Removed);
	}
	
	public boolean isNone() {
		return this.equals(None);
	}
}
