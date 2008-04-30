package com.mesh4j.sync;

import java.util.List;

import com.mesh4j.sync.merge.MergeResult;

public class NullPreviewHandler implements PreviewImportHandler {

	public static final NullPreviewHandler INSTANCE = new NullPreviewHandler();

	@Override
	public List<MergeResult> preview(Repository targetRepository,
			List<MergeResult> mergedItems) {
		return mergedItems;
	}

}
