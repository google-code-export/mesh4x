package org.mesh4j.sync;

import java.util.Vector;

import org.mesh4j.sync.merge.MergeResult;


public class NullPreviewHandler implements IPreviewImportHandler {

	public static final NullPreviewHandler INSTANCE = new NullPreviewHandler();

	public Vector<MergeResult> preview(ISyncAdapter targetRepository,
			Vector<MergeResult> mergedItems) {
		return mergedItems;
	}

}
