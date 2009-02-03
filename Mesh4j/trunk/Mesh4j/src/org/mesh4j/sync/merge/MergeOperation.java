package org.mesh4j.sync.merge;

public enum MergeOperation {

	Added,
	Updated, 
	Conflict,
	Removed,
	None;

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
