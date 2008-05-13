package com.mesh4j.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.merge.MergeResult;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.test.utils.MockRepository;

public class SyncEngineTests extends AbstractSyncEngineTest {

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfNullLeftRepo() {
		new SyncEngine(null, new MockRepository());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowIfNullRightRepo() {
		new SyncEngine(new MockRepository(), null);
	}
	
	@Test
	public void ShouldCallMergeIfRepositorySupportsIt() {
		MockMergeRepository left = new MockMergeRepository();
		MockMergeRepository right = new MockMergeRepository();
		SyncEngine engine = new SyncEngine(left, right);

		engine.synchronize();

		Assert.assertTrue(left.mergeCalled());
		Assert.assertTrue(right.mergeCalled());
	}
	
	@Test
	public void ShouldCallImportPreviewHandler() {

		MockPreviewImportHandler previewHandler = new MockPreviewImportHandler();

		SyncEngine engine = new SyncEngine(new MockRepository("left"), new MockRepository("right"));
		
		engine.synchronize(previewHandler, PreviewBehavior.Left);
		Assert.assertTrue(previewHandler.previewWasCalled("left"));
		Assert.assertFalse(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.synchronize(previewHandler, PreviewBehavior.Right);
		Assert.assertFalse(previewHandler.previewWasCalled("left"));
		Assert.assertTrue(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.synchronize(previewHandler, PreviewBehavior.Both);
		Assert.assertTrue(previewHandler.previewWasCalled("left"));
		Assert.assertTrue(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.synchronize(previewHandler, PreviewBehavior.None);
		Assert.assertFalse(previewHandler.previewWasCalled("left"));
		Assert.assertFalse(previewHandler.previewWasCalled("right"));

	}

	private class MockMergeRepository implements IRepositoryAdapter {

		private boolean mergeCalled;

		public String getFriendlyName() {
			return "MockMerge";
		}

		public boolean supportsMerge() {
			return true;
		}

		public Item get(String id) {
			return null;
		}

		public List<Item> getAll() {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since) {
			return new ArrayList<Item>();
		}

		public List<Item> getConflicts() {
			return new ArrayList<Item>();
		}

		public void delete(String id) {
		}

		public void update(Item item) {
		}

		public void update(Item item, boolean resolveConflicts) {
		}

		public List<Item> merge(List<Item> items) {
			mergeCalled = true;
			return new ArrayList<Item>();
		}

		public void add(Item item) {
		}

		public List<Item> getAll(IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public boolean mergeCalled() {
			return mergeCalled;
		}
	}

	@Override
	protected IRepositoryAdapter makeLeftRepository(Item... items) {
		return new MockRepository(items);
	}
	
	@Override
	protected IRepositoryAdapter makeRightRepository(Item... items) {
		return new MockRepository(items);
	}

	private class MockPreviewImportHandler implements IPreviewImportHandler {

		private Set<String> repositories = new HashSet<String>();

		@Override
		public List<MergeResult> preview(IRepositoryAdapter targetRepository,
				List<MergeResult> mergedItems) {

			repositories.add(targetRepository.getFriendlyName());
			return mergedItems;
		}

		public boolean previewWasCalled(String repositoryFriendlyName) {
			return this.repositories.contains(repositoryFriendlyName);
		}

		public void reset() {
			this.repositories.clear();
		}

	}

	@Override
	protected String getUserName(Item item) {
		return item.getContent().getPayload().element("user").element("name").getText();
	}

}
