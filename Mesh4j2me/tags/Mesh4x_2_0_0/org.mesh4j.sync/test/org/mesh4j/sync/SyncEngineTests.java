package org.mesh4j.sync;



public class SyncEngineTests extends AbstractSyncEngineTest {

//	@Test(expected = IllegalArgumentException.class)
//	public void ShouldThrowIfNullLeftRepo() {
//		new SyncEngine(null, new MockRepository());
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void ShouldThrowIfNullRightRepo() {
//		new SyncEngine(new MockRepository(), null);
//	}
//	
//	@Test
//	public void ShouldCallMergeIfRepositorySupportsIt() {
//		MockMergeRepository left = new MockMergeRepository();
//		MockMergeRepository right = new MockMergeRepository();
//		SyncEngine engine = new SyncEngine(left, right);
//
//		engine.synchronize();
//
//		Assert.assertTrue(left.mergeCalled());
//		Assert.assertTrue(right.mergeCalled());
//	}
//	
//	@Test
//	public void ShouldCallSyncAwareIfRepositorySupportsIt() {
//		MockSyncAwareRepository left = new MockSyncAwareRepository();
//		MockSyncAwareRepository right = new MockSyncAwareRepository();
//		SyncEngine engine = new SyncEngine(left, right);
//
//		engine.synchronize();
//
//		Assert.assertTrue(left.beginSyncCalled());
//		Assert.assertTrue(left.endSyncCalled());
//		Assert.assertTrue(right.beginSyncCalled());
//		Assert.assertTrue(right.endSyncCalled());
//	}
//	
//	@Test
//	public void ShouldThrowExceptionIfSycnEngineCallMergeIfRepositoryDoNotSupportsIt() {
//		MockNotSupportMergeRepository left = new MockNotSupportMergeRepository();
//		MockNotSupportMergeRepository right = new MockNotSupportMergeRepository();
//		SyncEngine engine = new SyncEngine(left, right);
//
//		engine.synchronize();
//	}
//	
//	@Test
//	public void ShouldThrowExceptionIfSycnEngineCallSyncAwareIfRepositoryDoNotSupportsIt() {
//		MockNotSupportSyncAwareRepository left = new MockNotSupportSyncAwareRepository();
//		MockNotSupportSyncAwareRepository right = new MockNotSupportSyncAwareRepository();
//		SyncEngine engine = new SyncEngine(left, right);
//
//		engine.synchronize();
//	}
//	
//	
//	@Test
//	public void ShouldCallImportPreviewHandler() {
//
//		MockPreviewImportHandler previewHandler = new MockPreviewImportHandler();
//
//		SyncEngine engine = new SyncEngine(new MockRepository("left"), new MockRepository("right"));
//		
//		engine.synchronize(previewHandler, PreviewBehavior.Left);
//		Assert.assertTrue(previewHandler.previewWasCalled("left"));
//		Assert.assertFalse(previewHandler.previewWasCalled("right"));
//
//		previewHandler.reset();
//		engine.synchronize(previewHandler, PreviewBehavior.Right);
//		Assert.assertFalse(previewHandler.previewWasCalled("left"));
//		Assert.assertTrue(previewHandler.previewWasCalled("right"));
//
//		previewHandler.reset();
//		engine.synchronize(previewHandler, PreviewBehavior.Both);
//		Assert.assertTrue(previewHandler.previewWasCalled("left"));
//		Assert.assertTrue(previewHandler.previewWasCalled("right"));
//
//		previewHandler.reset();
//		engine.synchronize(previewHandler, PreviewBehavior.None);
//		Assert.assertFalse(previewHandler.previewWasCalled("left"));
//		Assert.assertFalse(previewHandler.previewWasCalled("right"));
//
//	}
//
//	private class MockMergeRepository implements ISyncAdapter, ISupportMerge {
//
//		private boolean mergeCalled = false;
//
//		public String getFriendlyName() {
//			return "MockMerge";
//		}
//
//		public Item get(String id) {
//			return null;
//		}
//
//		public List<Item> getAll() {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getConflicts() {
//			return new ArrayList<Item>();
//		}
//
//		public void delete(String id) {
//		}
//
//		public void update(Item item) {
//		}
//
//		public void update(Item item, boolean resolveConflicts) {
//		}
//
//		public List<Item> merge(List<Item> items) {
//			mergeCalled = true;
//			return new ArrayList<Item>();
//		}
//
//		public void add(Item item) {
//		}
//
//		public List<Item> getAll(IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public boolean mergeCalled() {
//			return mergeCalled;
//		}
//	}
//
//	@Override
//	protected ISyncAdapter makeLeftRepository(Item... items) {
//		return new MockRepository(items);
//	}
//	
//	@Override
//	protected ISyncAdapter makeRightRepository(Item... items) {
//		return new MockRepository(items);
//	}
//
//	private class MockPreviewImportHandler implements IPreviewImportHandler {
//
//		private Set<String> repositories = new HashSet<String>();
//
//		@Override
//		public List<MergeResult> preview(ISyncAdapter targetRepository,
//				List<MergeResult> mergedItems) {
//
//			repositories.add(targetRepository.getFriendlyName());
//			return mergedItems;
//		}
//
//		public boolean previewWasCalled(String repositoryFriendlyName) {
//			return this.repositories.contains(repositoryFriendlyName);
//		}
//
//		public void reset() {
//			this.repositories.clear();
//		}
//	}
//
//	@Override
//	protected String getUserName(Item item) {
//		return item.getContent().getPayload().element("user").element("name").getText();
//	}
//	
//	private class MockSyncAwareRepository implements ISyncAdapter, ISyncAware {
//
//		private boolean beginSyncCalled = false;
//		private boolean endSyncCalled = false;
//
//		public String getFriendlyName() {
//			return "MockSyncAware";
//		}
//
//		public Item get(String id) {
//			return null;
//		}
//
//		public List<Item> getAll() {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getConflicts() {
//			return new ArrayList<Item>();
//		}
//
//		public void delete(String id) {
//		}
//
//		public void update(Item item) {
//		}
//
//		public void update(Item item, boolean resolveConflicts) {
//		}
//
//		public void add(Item item) {
//		}
//
//		public List<Item> getAll(IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public boolean beginSyncCalled() {
//			return beginSyncCalled;
//		}
//		
//		public boolean endSyncCalled() {
//			return endSyncCalled;
//		}
//
//		@Override
//		public void beginSync() {
//			beginSyncCalled = true;			
//		}
//
//		@Override
//		public void endSync() {
//			endSyncCalled = true;			
//		}
//	}
//	
//	private class MockNotSupportSyncAwareRepository implements ISyncAdapter{
//
//		public String getFriendlyName() {
//			return "MockNoSyncAware";
//		}
//
//		public Item get(String id) {
//			return null;
//		}
//
//		public List<Item> getAll() {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getConflicts() {
//			return new ArrayList<Item>();
//		}
//
//		public void delete(String id) {
//		}
//
//		public void update(Item item) {
//		}
//
//		public void update(Item item, boolean resolveConflicts) {
//		}
//
//		public void add(Item item) {
//		}
//
//		public List<Item> getAll(IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public void beginSync() {
//			throw new UnsupportedOperationException();			
//		}
//
//		public void endSync() {
//			throw new UnsupportedOperationException();		
//		}
//	}
//
//	private class MockNotSupportMergeRepository implements ISyncAdapter {
//
//		public String getFriendlyName() {
//			return "MockNotSupportMerge";
//		}
//
//		public Item get(String id) {
//			return null;
//		}
//
//		public List<Item> getAll() {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getConflicts() {
//			return new ArrayList<Item>();
//		}
//
//		public void delete(String id) {
//		}
//
//		public void update(Item item) {
//		}
//
//		public void update(Item item, boolean resolveConflicts) {
//		}
//
//		public List<Item> merge(List<Item> items) {
//			throw new UnsupportedOperationException();
//		}
//
//		public void add(Item item) {
//		}
//
//		public List<Item> getAll(IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//
//		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
//			return new ArrayList<Item>();
//		}
//	}
}
