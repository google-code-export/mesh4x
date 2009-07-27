package org.mesh4j.sync;

import de.enough.polish.java5.Enum;

public class PreviewBehavior extends Enum{

	protected PreviewBehavior(String name, int ordinal) {
		super(name, ordinal);
	}

	/// <summary>
	/// Call preview handler when merging items into the left repository.
	/// </summary>
	public static PreviewBehavior Left = new PreviewBehavior("Left", 0);
	
	/// <summary>
	/// Call preview handler when merging items into the right repository.
	/// </summary>
	public static PreviewBehavior Right = new PreviewBehavior("Right", 1);
	
	/// <summary>
	/// Call preview handler when merging items into both repositories.
	/// </summary>
	public static PreviewBehavior Both = new PreviewBehavior("Both", 2);
	
	/// <summary>
	/// Do not apply preview behavior to any repository.
	/// </summary>
	public static PreviewBehavior None = new PreviewBehavior("None", 3);
}
