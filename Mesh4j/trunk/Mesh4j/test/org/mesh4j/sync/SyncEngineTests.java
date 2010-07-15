package org.mesh4j.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.XMLContent;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.merge.MergeResult;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.model.Sync;
import org.mesh4j.sync.observer.IObserverSyncProcess;
import org.mesh4j.sync.test.utils.MockRepository;
import org.mesh4j.sync.utils.XMLHelper;


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
	public void ShouldCallSyncAwareIfRepositorySupportsIt() {
		MockSyncAwareRepository left = new MockSyncAwareRepository();
		MockSyncAwareRepository right = new MockSyncAwareRepository();
		SyncEngine engine = new SyncEngine(left, right);

		engine.synchronize();

		Assert.assertTrue(left.beginSyncCalled());
		Assert.assertTrue(left.endSyncCalled());
		Assert.assertTrue(right.beginSyncCalled());
		Assert.assertTrue(right.endSyncCalled());
	}
	
	@Test
	public void ShouldThrowExceptionIfSycnEngineCallMergeIfRepositoryDoNotSupportsIt() {
		MockNotSupportMergeRepository left = new MockNotSupportMergeRepository();
		MockNotSupportMergeRepository right = new MockNotSupportMergeRepository();
		SyncEngine engine = new SyncEngine(left, right);

		engine.synchronize();
	}
	
	@Test
	public void ShouldThrowExceptionIfSycnEngineCallSyncAwareIfRepositoryDoNotSupportsIt() {
		MockNotSupportSyncAwareRepository left = new MockNotSupportSyncAwareRepository();
		MockNotSupportSyncAwareRepository right = new MockNotSupportSyncAwareRepository();
		SyncEngine engine = new SyncEngine(left, right);

		engine.synchronize();
	}
	
	
	@Test
	public void ShouldCallImportPreviewHandler() {

		MockPreviewImportHandler previewHandler = new MockPreviewImportHandler();

		SyncEngine engine = new SyncEngine(new MockRepository("left"), new MockRepository("right"));
		engine.setPreviewer(previewHandler);
		
		engine.setPreviewBehavior(PreviewBehavior.Left);
		engine.synchronize();
		Assert.assertTrue(previewHandler.previewWasCalled("left"));
		Assert.assertFalse(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.setPreviewBehavior(PreviewBehavior.Right);
		engine.synchronize();
		Assert.assertFalse(previewHandler.previewWasCalled("left"));
		Assert.assertTrue(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.setPreviewBehavior(PreviewBehavior.Both);
		engine.synchronize();
		Assert.assertTrue(previewHandler.previewWasCalled("left"));
		Assert.assertTrue(previewHandler.previewWasCalled("right"));

		previewHandler.reset();
		engine.setPreviewBehavior(PreviewBehavior.None);
		engine.synchronize();
		Assert.assertFalse(previewHandler.previewWasCalled("left"));
		Assert.assertFalse(previewHandler.previewWasCalled("right"));

	}

	@Test
	public void shouldTraceSync() throws InterruptedException{
		MockObserverSyncProcess trace = new MockObserverSyncProcess();

		String id = IdGenerator.INSTANCE.newID();
		XMLContent content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item itemSourceAdded = new Item(content, new Sync(id, "jmt", new Date(), false));
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item baseItemSourceDeleted = new Item(content, new Sync(id, "jmt", new Date(), false));
		Item itemSourceDeleted = baseItemSourceDeleted.clone();
		itemSourceDeleted.getSync().delete("jmt2", new Date());
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item baseItemSourceUpdated = new Item(content, new Sync(id, "jmt", new Date(), false));
		Item itemSourceUpdated = baseItemSourceUpdated.clone();
		itemSourceUpdated.getSync().update("jmt1", new Date());
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item baseItemSourceConflict = new Item(content, new Sync(id, "jmt", new Date(), false));
		Item itemSourceConflict = baseItemSourceConflict.clone();
		itemSourceConflict.getSync().update("jmt1", new Date());

		Thread.sleep(1000);
		Item itemSourceTargetConflict = baseItemSourceConflict.clone();
		itemSourceTargetConflict.getSync().update("jmt7", new Date());
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item itemTargetAdded = new Item(content, new Sync(id, "jmt", new Date(), false));
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item baseItemTargetDeleted = new Item(content, new Sync(id, "jmt", new Date(), false));
		Item itemTargetDeleted = baseItemTargetDeleted.clone();
		itemTargetDeleted.getSync().delete("jmt2", new Date());
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item baseItemTargetUpdated = new Item(content, new Sync(id, "jmt", new Date(), false));
		Item itemTargetUpdated = baseItemTargetUpdated.clone();
		itemTargetUpdated.getSync().update("jmt1", new Date());
		
		id = IdGenerator.INSTANCE.newID();
		content = new XMLContent(id, "title", "desc", XMLHelper.parseElement("<user>"+id+"</user>"));
		Item baseItemTargetConflict = new Item(content, new Sync(id, "jmt", new Date(), false));
		Item itemTargetConflict = baseItemTargetConflict.clone();
		itemTargetConflict.getSync().update("jmt1", new Date());
		
		Thread.sleep(1000);
		Item itemTargetSourceConflict = baseItemTargetConflict.clone();
		itemTargetSourceConflict.getSync().update("jmt5", new Date());

		MockRepository source = new MockRepository("source");
		source.add(itemSourceAdded);
		source.add(itemSourceDeleted);
		source.add(itemSourceUpdated);
		source.add(itemSourceConflict);
		source.add(baseItemTargetUpdated);
		source.add(baseItemTargetDeleted);
		source.add(itemTargetSourceConflict);
		
		MockRepository target = new MockRepository("target");
		target.add(itemTargetAdded);
		target.add(itemTargetDeleted);
		target.add(itemTargetUpdated);
		target.add(itemTargetConflict);
		target.add(baseItemSourceUpdated);
		target.add(baseItemSourceDeleted);
		target.add(itemSourceTargetConflict);
		
		SyncEngine engine = new SyncEngine(source, target);
		engine.registerSyncTraceObserver(trace);
		engine.synchronize();
		
		trace.printAll();
	}
	
	
	private class MockMergeRepository implements ISyncAdapter, ISupportMerge {

		private boolean mergeCalled = false;

		public String getFriendlyName() {
			return "MockMerge";
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
	protected ISyncAdapter makeLeftRepository(Item... items) {
		return new MockRepository(items);
	}
	
	@Override
	protected ISyncAdapter makeRightRepository(Item... items) {
		return new MockRepository(items);
	}

	private class MockPreviewImportHandler implements IPreviewImportHandler {

		private Set<String> repositories = new HashSet<String>();

		@Override
		public List<MergeResult> preview(ISyncAdapter targetRepository,
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
	
	private class MockSyncAwareRepository implements ISyncAdapter, ISyncAware {

		private boolean beginSyncCalled = false;
		private boolean endSyncCalled = false;

		public String getFriendlyName() {
			return "MockSyncAware";
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

		public void add(Item item) {
		}

		public List<Item> getAll(IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public boolean beginSyncCalled() {
			return beginSyncCalled;
		}
		
		public boolean endSyncCalled() {
			return endSyncCalled;
		}

		@Override
		public void beginSync() {
			beginSyncCalled = true;			
		}

		@Override
		public void endSync() {
			endSyncCalled = true;			
		}
	}
	
	private class MockNotSupportSyncAwareRepository implements ISyncAdapter{

		public String getFriendlyName() {
			return "MockNoSyncAware";
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

		public void add(Item item) {
		}

		public List<Item> getAll(IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public void beginSync() {
			throw new UnsupportedOperationException();			
		}

		public void endSync() {
			throw new UnsupportedOperationException();		
		}

	}

	private class MockNotSupportMergeRepository implements ISyncAdapter {

		public String getFriendlyName() {
			return "MockNotSupportMerge";
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
			throw new UnsupportedOperationException();
		}

		public void add(Item item) {
		}

		public List<Item> getAll(IFilter<Item> filter) {
			return new ArrayList<Item>();
		}

		public List<Item> getAllSince(Date since, IFilter<Item> filter) {
			return new ArrayList<Item>();
		}
	}

	private class MockObserverSyncProcess implements IObserverSyncProcess{
		
		private int sourceAdd = 0;
		private int targetAdd = 0;		
		private int sourceDelete = 0;
		private int targetDelete = 0;		
		private int sourceUpdate = 0;
		private int targetUpdate = 0;
		private int sourceConflicts = 0;
		private int targetConflicts = 0;
		
		@Override
		public void notifyAddItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items) {
			System.out.println("save: " + adapter.getFriendlyName() + " item: " + result.getProposed().getSyncId() + "  ("+ (items.indexOf(result)+1) +" of " + items.size() +")");
			if(syncEngine.isSource(adapter)){
				sourceAdd = sourceAdd +1;
			}else{
				targetAdd = targetAdd +1;
			}
		}

		public void printAll() {
			System.out.println("Source Add "+ sourceAdd);
			System.out.println("Source delete "+ sourceDelete);
			System.out.println("Source Update "+ sourceUpdate);
			System.out.println("Source Conflicts "+ sourceConflicts);
											
			System.out.println("Target Add "+ targetAdd);
			System.out.println("Target delete "+ targetDelete);
			System.out.println("Target Update "+ targetUpdate);
			System.out.println("Target Conflicts "+ targetConflicts);
			
		}

		@Override
		public void notifyBeginSync(SyncEngine syncEngine) {
			System.out.println("Begin sync");				
		}

		@Override
		public void notifyEndSync(SyncEngine syncEngine, List<Item> conflicts) {
			System.out.println("End sync conflicts: " + conflicts.size());
		}

		@Override
		public void notifyGetAll(SyncEngine syncEngine, ISyncAdapter adapter, Date since) {
			System.out.println("Get all: " + adapter.getFriendlyName() + " since: " + since);				
		}

		@Override
		public void notifyGetAndMergeItem(SyncEngine syncEngine, ISyncAdapter adapter, Item incoming, List<Item> items) {
			System.out.println("get for merge: " + adapter.getFriendlyName() + " item: " + incoming.getSyncId() + "  ("+ (items.indexOf(incoming)+1) +" of " + items.size() +")");
		}

		@Override
		public void notifyMerge(SyncEngine syncEngine, ISyncAdapter adapter, List<Item> itemsToMerge) {
			System.out.println("Automatic merge items: " + adapter.getFriendlyName() + " items: " + itemsToMerge.size());				
		}

		@Override
		public void notifyUpdateItem(SyncEngine syncEngine, ISyncAdapter adapter, MergeResult result, List<MergeResult> items) {
			System.out.println("save: " + adapter.getFriendlyName() + " item: " + result.getProposed().getSyncId() + "  ("+ (items.indexOf(result)+1) +" of " + items.size() +")");
//			if(result.getProposed().isDeleted()){
//				System.out.println("delete: " + result.getProposed().getSyncId());
//			} else {
//				if(result.getOperation().isConflict()){
//					System.out.println("update for conflict: " + result.getProposed().getSyncId());
//				} else {
//					System.out.println("update: " + result.getProposed().getSyncId());
//				}
//			}
			
			if(syncEngine.isSource(adapter)){
				if(result.getProposed().isDeleted()){
					sourceDelete = sourceDelete + 1;
				} else {
					if(result.getOperation().isConflict()){
						sourceConflicts = sourceConflicts +1;
					}else{
						sourceUpdate = sourceUpdate +1;
					}
				}
			}else{
				if(result.getProposed().isDeleted()){
					targetDelete = targetDelete + 1;
				} else {
					if(result.getOperation().isConflict()){
						targetConflicts = targetConflicts +1;
					} else {
						targetUpdate = targetUpdate +1;
					}
				}
			}
		}
	}
}
