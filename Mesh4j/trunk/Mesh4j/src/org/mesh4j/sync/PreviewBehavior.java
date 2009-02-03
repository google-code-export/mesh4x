package org.mesh4j.sync;

public enum PreviewBehavior {

	/// <summary>
	/// Call preview handler when merging items into the left repository.
	/// </summary>
	Left,
	/// <summary>
	/// Call preview handler when merging items into the right repository.
	/// </summary>
	Right,
	/// <summary>
	/// Call preview handler when merging items into both repositories.
	/// </summary>
	Both,
	/// <summary>
	/// Do not apply preview behavior to any repository.
	/// </summary>
	None,
}
