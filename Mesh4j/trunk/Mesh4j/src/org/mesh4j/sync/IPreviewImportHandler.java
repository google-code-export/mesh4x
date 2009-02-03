package org.mesh4j.sync;

import java.util.List;

import org.mesh4j.sync.merge.MergeResult;


// TODO (JMT) JavaDoc
public interface IPreviewImportHandler {

	/// <summary>
	/// Delegate signature for a method that can perform preview/filter behavior 
	/// before merging items into a repository. 
	/// </summary>
	/// <param name="targetRepository">The repository where items will be merged in.</param>
	/// <param name="mergedItems">The merged items as determined by the <see cref="SyncEngine"/> merge 
	/// behavior.</param>
	/// <returns>A list of items that should be merged into the <paramref name="targetRepository"/>.</returns>
	/// <remarks>
	/// Merge preview can only be performed on repositories that do not provide built-in merging capabilities. 
	/// In such a case, there's no way for the <see cref="SyncEngine"/> to perform a preview of the merge operation.
	/// </remarks>
	public List<MergeResult> preview(ISyncAdapter targetRepository, List<MergeResult> mergedItems);

}
