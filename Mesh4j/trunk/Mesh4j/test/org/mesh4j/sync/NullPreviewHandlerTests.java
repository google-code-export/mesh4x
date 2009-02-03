package org.mesh4j.sync;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.merge.MergeResult;


public class NullPreviewHandlerTests {

	@Test
	public void shouldMerge(){
		ISyncAdapter targetRepository = null;
		List<MergeResult> mergedItems = new ArrayList<MergeResult>();
		
		Assert.assertSame(mergedItems, NullPreviewHandler.INSTANCE.preview(targetRepository, mergedItems));
	}
}
