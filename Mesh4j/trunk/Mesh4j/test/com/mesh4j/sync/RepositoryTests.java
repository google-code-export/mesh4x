package com.mesh4j.sync;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.mesh4j.sync.behavior.Behaviors;
import com.mesh4j.sync.feed.ItemXMLContent;
import com.mesh4j.sync.filter.NullFilter;
import com.mesh4j.sync.model.Content;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.model.NullContent;
import com.mesh4j.sync.model.Sync;
import com.mesh4j.sync.test.utils.MockRepository;
import com.mesh4j.sync.test.utils.TestHelper;

public class RepositoryTests {

	protected Repository createRepository() {
		return new MockRepository();
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetNullId() {
		Repository repository = createRepository();
		repository.get(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetEmptyId() {
		Repository repository = createRepository();
		repository.get("");
	}

	@Test
	public void ShouldGetNullIfNotExists() {
		Repository repository = createRepository();
		Item item = repository.get(TestHelper.newID());
		Assert.assertNull(item);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowAddNullItem() {
		Repository repository = createRepository();
		repository.add(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowDeleteNullId() {
		Repository repository = createRepository();
		repository.delete(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowDeleteEmptyId() {
		Repository repository = createRepository();
		repository.delete("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowUpdateNullItem() {
		Repository repository = createRepository();
		repository.update(null);
	}

	@Test
	public void ShouldAddAndGetItem() {
		Repository repository = createRepository();
		ItemXMLContent xml = new ItemXMLContent(TestHelper.newID(), "foo", "bar", TestHelper.makeElement("<payload></payload>"));
		Sync sync = getBehaviors().create(xml.getId(), "kzu", TestHelper.now(),
				false);
		Item item = new Item(xml, sync);

		repository.add(item);

		Item saved = repository.get(xml.getId());

		Assert.assertNotNull(saved);
		
		ItemXMLContent itemContent = (ItemXMLContent) item.getContent();
		ItemXMLContent savedContent = (ItemXMLContent) saved.getContent();
		
		Assert.assertEquals(itemContent.getTitle(), savedContent.getTitle());
		Assert.assertEquals(itemContent.getDescription(), savedContent.getDescription());
		Assert.assertEquals(itemContent.getId(), savedContent.getId());
		Assert.assertTrue(item.getSync().equals(saved.getSync()));
	}

	private Behaviors getBehaviors() {
		return Behaviors.INSTANCE;
	}

	@Test
	public void ShouldGetAllItems() {
		Repository repository = createRepository();

		String by = "DeviceAuthor.Current";
		String id = TestHelper.newID();
		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repository.add(item);

		id = TestHelper.newID();
		item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repository.add(item);

		List<Item> allItems = repository.getAll();
		Assert.assertEquals(2, allItems.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowAddDuplicateItemId() {
		Repository repo = createRepository();

		String by = "DeviceAuthor.Current";
		String id = TestHelper.newID();
		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repo.add(item);

		item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repo.add(item);
	}

	@Test
	public void ShouldGetAllSinceDate() {
		Repository repo = createRepository();
		String by = "DeviceAuthor.Current";

		String id = TestHelper.newID();
		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.nowSubtractDays(1), false));
		repo.add(item);

		id = TestHelper.newID();
		item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repo.add(item);

		List<Item> allItems = repo.getAllSince(TestHelper
				.nowSubtractMinutes(10));
		Assert.assertEquals(1, allItems.size());
	}

	@Test
	public void ShouldGetAllIfNullSince() {
		Repository repo = createRepository();
		String by = "DeviceAuthor.Current";

		String id = TestHelper.newID();
		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.nowSubtractDays(1), false));
		repo.add(item);

		id = TestHelper.newID();
		item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repo.add(item);

		List<Item> allItems = repo.getAllSince(null);
		Assert.assertEquals(2, allItems.size());
	}

	@Test
	public void ShouldGetAllIfNullWhen() {
		Repository repo = createRepository();
		String by = "byUser";

		String id = TestHelper.newID();
		Content modelItem =new ItemXMLContent(id, "foo", "bar", TestHelper.makeElement("<payload />"));
		Sync sync = getBehaviors().create(id, by, null, false);
		Item item = new Item(modelItem, sync);
		repo.add(item);

		id = TestHelper.newID();
		modelItem = new ItemXMLContent(id, "foo", "bar", TestHelper.makeElement("<payload />"));
		sync = getBehaviors().create(id, by, TestHelper.now(), false);
		item = new Item(modelItem, sync);
		repo.add(item);

		List<Item> allItems = repo.getAllSince(TestHelper.nowSubtractMinutes(10));
		Assert.assertEquals(2, allItems.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetAllNullFilter() {
		Repository repository = createRepository();
		repository.getAll(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void ShouldThrowGetAllSinceWithNullFilter() {
		Repository repository = createRepository();
		repository.getAllSince(TestHelper.now(), null);
	}

	@Test
	public void ShouldGetAllPassFilter() {
		Repository repo = createRepository();
		String by = "jmt";

		String id = TestHelper.newID();
		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id, by,
				TestHelper.now(), false));
		repo.add(item);

		Filter<Item> filter = new IdFilter(id);

		String id2 = TestHelper.newID();
		item = new Item(new ItemXMLContent(id2, "foo", "bar", TestHelper
				.makeElement("<payload />")), getBehaviors().create(id2, by,
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

		ItemXMLContent item = new ItemXMLContent(TestHelper.newID(), "foo", "bar", TestHelper
				.makeElement("<payload />"));
		Sync sync = getBehaviors().create(item.getId(), "kzu", created, false);

		Repository repo = createRepository();
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

		Filter<Item> filter = new NullFilter<Item>();
		repo.getAll(filter);

		Assert.assertEquals(filter, repo.getFilter());
	}

	@Test
	public void ShouldResolveConflicts() {
		MockRepository repo = new MockRepository();

		String id = TestHelper.newID();
		Sync sync = getBehaviors().create(id, "kzu", TestHelper.nowSubtractDays(1),
				false);

		Item item = new Item(new NullContent(id), getBehaviors().update(sync,
				"kzu", TestHelper.nowSubtractHours(2), false));
		sync.getConflicts().add(
				new Item(new ItemXMLContent(TestHelper.newID(), "foo", "bar",
						TestHelper.makeElement("<payload/>")), getBehaviors()
						.update(sync, "kzu", TestHelper.nowSubtractHours(4),
								false)));

		repo.add(item);

		repo.update(item, true);

		item = repo.get(id);

		Assert.assertEquals(0, item.getSync().getConflicts().size());
	}

	@Test
	public void ShouldSaveUpdatedItemOnResolveConflicts() {
		Repository repo = createRepository();
		String id = TestHelper.newID();
		String by = "jmt";

		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload/>")), getBehaviors().create(id, by,
				TestHelper.nowSubtractMinutes(5), false));
		repo.add(item);

		// Introduce a conflict.
		ItemXMLContent xml = (ItemXMLContent) item.getContent().clone();
		xml.setTitle("Conflict");
		Sync updatedSync = getBehaviors().update(item.getSync(), "Conflict",
				TestHelper.now(), false);
		item.getSync().getConflicts().add(new Item(xml, updatedSync));

		((ItemXMLContent) item.getContent()).setTitle("Resolved");

		repo.update(item, true);

		Item storedItem = repo.get(item.getSyncId());

		Assert.assertEquals("Resolved", ((ItemXMLContent) storedItem.getContent()).getTitle());
	}

	@Test
	public void ShouldSaveUpdatedItemOnResolveConflicts2() {
		Repository repo = createRepository();
		String id = TestHelper.newID();
		String by = "jmt";

		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload/>")), getBehaviors().create(id, by,
				TestHelper.nowSubtractMinutes(5), false));
		repo.add(item);

		// Introduce a conflict.
		ItemXMLContent xml = (ItemXMLContent) item.getContent().clone();
		xml.setTitle("Conflict");
		Sync updatedSync = getBehaviors().update(item.getSync(), "Conflict",
				TestHelper.now(), false);
		item.getSync().getConflicts().add(new Item(xml, updatedSync));

		((ItemXMLContent) item.getContent()).setTitle("Resolved");

		repo.update(item, true);

		Item storedItem = repo.get(item.getSyncId());
		
		Assert
				.assertEquals(
						"An update with resolve conflicts was issued, but the stored item Title was not the updated one from the resolved conflict.",
						"Resolved", ((ItemXMLContent) storedItem.getContent()).getTitle());
	}

	@Test
	public void ShouldResolveConflictsPreserveDeletedState() {
		Repository repo = createRepository();
		String id = TestHelper.newID();
		String by = "jmt";

		Item item = new Item(new ItemXMLContent(id, "foo", "bar", TestHelper
				.makeElement("<payload/>")), getBehaviors().create(id, by,
				TestHelper.nowSubtractMinutes(5), false));
		repo.add(item);

		// Introduce a conflict.
		ItemXMLContent xml = (ItemXMLContent) item.getContent().clone();
		xml.setTitle("Conflict");
		Sync updatedSync = getBehaviors().update(item.getSync(), "Conflict",
				TestHelper.now(), false);
		item.getSync().getConflicts().add(new Item(xml, updatedSync));

		((ItemXMLContent) item.getContent()).setTitle("Resolved");

		Sync deletedSync = getBehaviors().delete(item.getSync(), "Deleted",
				TestHelper.now());

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

	private class SimpleRepository extends AbstractRepository {
		private Date since;
		private Filter<Item> filter;

		public boolean supportsMerge() {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		public Filter<Item> getFilter() {
			return filter;
		}

		public Date getSince() {
			return since;
		}

		public Item get(String id) {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		protected List<Item> getAll(Date since, Filter<Item> filter) {
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

		public List<Item> merge(List<Item> items) {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		public String getFriendlyName() {
			throw new UnsupportedOperationException(
					"The method or operation is not implemented.");
		}

		protected String getCurrentAuthor() {
			return "JMT";
		}
	}
	
	private class IdFilter implements Filter<Item>{
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
