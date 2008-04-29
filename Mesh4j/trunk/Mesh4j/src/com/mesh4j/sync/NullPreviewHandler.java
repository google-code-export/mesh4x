package com.mesh4j.sync;

import java.util.List;


public class NullPreviewHandler implements PreviewImportHandler {

	public static final PreviewImportHandler INSTANCE = new NullPreviewHandler();

	@Override
	public List<ItemMergeResult> preview(Repository targetRepository,
			List<ItemMergeResult> mergedItems) {
		return mergedItems;
	}

}
