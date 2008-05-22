package com.mesh4j.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.adapters.feed.XMLContent;
import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.model.IContent;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.MockRepository;
import com.mesh4j.sync.test.utils.TestHelper;

public class RepositoryTests {

	protected ISyncAdapter createRepository() {
		return new MockRepository();
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetNullId() {
		ISyncAdapter repository = createRepository();
		repository.get(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetEmptyId() {
		ISyncAdapter repository = createRepository();
		repository.get("");
	}

	@Test
	public void ShouldGetNullIfNotExists() {
		ISyncAdapter repository = createRepository();
		Item item = repository.get(TestHelper.newID());
		Assert.assertNull(item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowAddNullItem() {
		ISyncAdapter repository = createRepository();
		repository.add(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowDeleteNullId() {
		ISyncAdapter repository = createRepository();
		repository.delete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowDeleteEmptyId() {
		ISyncAdapter repository = createRepository();
		repository.delete("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowUpdateNullItem() {
		ISyncAdapter repository = createRepository();
		repository.update(null);
	}

	@Test
	public void ShouldAddAndGetItem() {
		ISyncAdapter repository = createRepository();
		XMLContent xml = new XMLContent(TestHelper.newID(), "foo", "bar", TestHelper.makeElement("<payload></payload>"));
		Sync sync = new Sync(xml.getId(), "kzu", TestHelper.now(), false);
		Item item = new Item(xml, sync);

		repository.add(item);

		Item saved = repository.get(xml.getId());

		Assert.assertNotNull(saved);
		
		XMLContent itemContent = (XMLContent) item.getContent();
		XMLContent savedContent = (XMLContent) saved.getContent();
		
		Assert.assertEquals(itemContent.getTitle(), savedContent.getTitle());
		Assert.assertEquals(itemContent.getDescription(), savedContent.getDescription());
		Assert.assertEquals(itemContent.getId(), savedContent.getId());
		Assert.assertTrue(item.getSync().equals(saved.getSync()));
	}

	@Test
	public void ShouldGetAllItems() {
		ISyncAdapter repository = createRepository();

		String by = "DeviceAuthor.Current";
		String id = TestHelper.newID();
		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by, TestHelper.now(), false));
		repository.add(item);

		id = TestHelper.newID();
		item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.now(), false));
		repository.add(item);

		List<Item> allItems = repository.getAll();
		Assert.assertEquals(2, allItems.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowAddDuplicateItemId() {
		ISyncAdapter repo = createRepository();

		String by = "DeviceAuthor.Current";
		String id = TestHelper.newID();
		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.now(), false));
		repo.add(item);

		item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.now(), false));
		repo.add(item);
	}

	@Test
	public void ShouldGetAllSinceDate() {
		ISyncAdapter repo = createRepository();
		String by = "DeviceAuthor.Current";

		String id = TestHelper.newID();
		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.nowSubtractDays(1), false));
		repo.add(item);

		id = TestHelper.newID();
		item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.now(), false));
		repo.add(item);

		List<Item> allItems = repo.getAllSince(TestHelper
				.nowSubtractMinutes(10));
		Assert.assertEquals(1, allItems.size());
	}

	@Test
	public void ShouldGetAllIfNullSince() {
		ISyncAdapter repo = createRepository();
		String by = "DeviceAuthor.Current";

		String id = TestHelper.newID();
		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.nowSubtractDays(1), false));
		repo.add(item);

		id = TestHelper.newID();
		item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.now(), false));
		repo.add(item);

		List<Item> allItems = repo.getAllSince(null);
		Assert.assertEquals(2, allItems.size());
	}

	@Test
	public void ShouldGetAllIfNullWhen() {
		ISyncAdapter repo = createRepository();
		String by = "byUser";

		String id = TestHelper.newID();
		IContent modelItem =new XMLContent(id, "foo", "bar", TestHelper.makeElement("<payload />"));
		Sync sync = new Sync(id, by, null, false);
		Item item = new Item(modelItem, sync);
		repo.add(item);

		id = TestHelper.newID();
		modelItem = new XMLContent(id, "foo", "bar", TestHelper.makeElement("<payload />"));
		sync = new Sync(id, by, TestHelper.now(), false);
		item = new Item(modelItem, sync);
		repo.add(item);

		List<Item> allItems = repo.getAllSince(TestHelper.nowSubtractMinutes(10));
		Assert.assertEquals(2, allItems.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetAllNullFilter() {
		ISyncAdapter repository = createRepository();
		repository.getAll(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetAllSinceWithNullFilter() {
		ISyncAdapter repository = createRepository();
		repository.getAllSince(TestHelper.now(), null);
	}

	@Test
	public void ShouldGetAllPassFilter() {
		ISyncAdapter repo = createRepository();
		String by = "jmt";

		String id = TestHelper.newID();
		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id, by,
				TestHelper.now(), false));
		repo.add(item);

		IFilter<Item> filter = new IdFilter(id);

		String id2 = TestHelper.newID();
		item = new Item(new XMLContent(id2, "foo", "bar", TestHelper
				.makeElement("<payload />")), new Sync(id2, by,
				TestHelper.now(), false));
		repo.add(item);

		List<Item> allItems = repo.getAll(filter);
		Assert.assertEquals(1, allItems.size());
		Item saved = allItems.get(0);
		Assert.assertEquals(id, saved.getSync().getId());
	}

	@Test
	public void ShouldGetAllSinceRemoveMilliseconds() {
		Date created = TestHelper.makeDate(2007, 9, 18, 12, 56, 23, 0);
		Date since = TestHelper.makeDate(2007, 9, 18, 12, 56, 23, 500);

		XMLContent item = new XMLContent(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload />"));
		Sync sync = new Sync(item.getId(), "kzu", created, false);

		ISyncAdapter repo = createRepository();
		repo.add(new Item(item, sync));

		List<Item> items = repo.getAllSince(since);
		Assert.assertEquals(1, items.size());
	}

	@Test
	public void ShouldGetAllCallGetAllSinceWithNullSince() {
		SimpleRepository repo = new SimpleRepository();

		repo.getAll();

		Assert.assertEquals(null, repo.getSince());
	}

	@Test
	public void ShouldGetAllWithFilterPassToImplementation() {
		SimpleRepository repo = new SimpleRepository();

		IFilter<Item> filter = new NullFilter<Item>();
		repo.getAll(filter);

		Assert.assertEquals(filter, repo.getFilter());
	}

	@Test
	public void ShouldResolveConflicts() {
		MockRepository repo = new MockRepository();

		String id = TestHelper.newID();
		Sync sync = new Sync(id, "kzu", TestHelper.nowSubtractDays(1), false);

		Item item = new Item(new NullContent(id), sync.update("kzu", TestHelper.nowSubtractHours(2), false));
		sync.getConflicts().add(
				new Item(new XMLContent(TestHelper.newID(), "foo", "bar",
						TestHelper.makeElement("<payload/>")), sync.clone().update("kzu", TestHelper.nowSubtractHours(4),
								false)));

		repo.add(item);

		repo.update(item, true);

		item = repo.get(id);

		Assert.assertEquals(0, item.getSync().getConflicts().size());
	}

	@Test
	public void ShouldSaveUpdatedItemOnResolveConflicts() {
		ISyncAdapter repo = createRepository();
		String id = TestHelper.newID();
		String by = "jmt";

		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload/>")), new Sync(id, by,
				TestHelper.nowSubtractMinutes(5), false));
		repo.add(item);

		// Introduce a conflict.
		XMLContent xml = (XMLContent) item.getContent().clone();
		xml.setTitle("Conflict");
		Sync updatedSync = item.getSync().clone().update("Conflict", TestHelper.now(), false);
		item.getSync().getConflicts().add(new Item(xml, updatedSync));

		((XMLContent) item.getContent()).setTitle("Resolved");

		repo.update(item, true);

		Item storedItem = repo.get(item.getSyncId());

		Assert.assertEquals("Resolved", ((XMLContent) storedItem.getContent()).getTitle());
	}

	@Test
	public void ShouldSaveUpdatedItemOnResolveConflicts2() {
		ISyncAdapter repo = createRepository();
		String id = TestHelper.newID();
		String by = "jmt";

		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload/>")), new Sync(id, by,
				TestHelper.nowSubtractMinutes(5), false));
		repo.add(item);

		// Introduce a conflict.
		XMLContent xml = (XMLContent) item.getContent().clone();
		xml.setTitle("Conflict");
		Sync updatedSync = item.getSync().clone().update("Conflict", TestHelper.now(), false);
		item.getSync().getConflicts().add(new Item(xml, updatedSync));

		((XMLContent) item.getContent()).setTitle("Resolved");

		repo.update(item, true);

		Item storedItem = repo.get(item.getSyncId());
		
		Assert
				.assertEquals(
						"An update with resolve conflicts was issued, but the stored item Title was not the updated one from the resolved conflict.",
						"Resolved", ((XMLContent) storedItem.getContent()).getTitle());
	}

	@Test
	public void ShouldResolveConflictsPreserveDeletedState() {
		ISyncAdapter repo = createRepository();
		String id = TestHelper.newID();
		String by = "jmt";

		Item item = new Item(new XMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload/>")), new Sync(id, by, TestHelper.nowSubtractMinutes(5), false));
		repo.add(item);

		// Introduce a conflict.
		XMLContent xml = (XMLContent) item.getContent().clone();
		xml.setTitle("Conflict");
		Sync updatedSync = item.getSync().clone().update("Conflict",
				TestHelper.now(), false);
		item.getSync().getConflicts().add(new Item(xml, updatedSync));

		((XMLContent) item.getContent()).setTitle("Resolved");

		Sync deletedSync = item.getSync().clone().delete("Deleted", TestHelper.now());

		repo.update(new Item(item.getContent(), deletedSync), true);

		Item saved = repo.get(item.getSyncId());

		Assert
				.assertTrue(
						"An update to delete an item was issued. The repository should either return null or the Item.XmlItem or an instance of a NullXmlItem",
						saved.getContent() == null
								|| saved.getContent() instanceof NullContent);
		Assert
				.assertTrue(
						"An update to delete an item was issued but its Sync.Deleted was not true.",
						saved.getSync().isDeleted());
	}

	private class SimpleRepository extends AbstractSyncAdapter {
		private Date since;
		private IFilter<Item> filter;

		public IFilter<Item> getFilter() {
			return filter;
		}

		public Date getSince() {
			return since;
		}

		public Item get(String id) {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		protected List<Item> getAll(Date since, IFilter<Item> filter) {
			this.since = since;
			this.filter = filter;

			return new ArrayList<Item>();
		}

		public void add(Item item) {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		public void delete(String id) {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		public void update(Item item) {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		public String getFriendlyName() {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		@Override
		public String getAuthenticatedUser() {
			return "TEST_USER";
		}
	}
	
	private class IdFilter implements IFilter<Item>{
		private String itemId;
		
		public IdFilter(String id)
		{
			this.itemId = id;
		}
		public boolean applies(Item obj) {
			return this.itemId.equals(obj.getSyncId());
		}
		
	}

}
