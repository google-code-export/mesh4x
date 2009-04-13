package org.mesh4j.sync.adapters.history;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.History;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.TestHelper;

public class FeedHistoryRepositoryTests {

	@Test
	public void shouldAddHistory(){
		String fileName = TestHelper.fileName("feedHistoryTest_"+IdGenerator.INSTANCE.newID()+".xml");
		FeedHistoryRepository repo = new FeedHistoryRepository("Oswego", "Hisotry changes", "localhost:8080//mesh4x/feeds/Epiinfo/Oswego/history", fileName, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, RssSyndicationFormat.INSTANCE);
		
		String syncId = IdGenerator.INSTANCE.newID();
		
		List<HistoryChange> changes = repo.getHistories(syncId);
		Assert.assertNotNull(changes);
		
		int size = changes.size();
		
		HistoryChange historyChange = new HistoryChange(syncId, new History("jmt", new Date(), 1), "<foo>bar</foo>", HistoryType.ADD);
		
		repo.addHistoryChange(historyChange);
		
		changes = repo.getHistories(syncId);
		Assert.assertNotNull(changes);
		Assert.assertEquals(size +1, changes.size());

		HistoryChange lastChange = changes.get(size);
		Assert.assertEquals(historyChange.getSyncId(), lastChange.getSyncId());
		Assert.assertEquals(historyChange.getHistoryType(),  lastChange.getHistoryType());
		Assert.assertEquals(historyChange.getSyncHistory(), lastChange.getSyncHistory());
		Assert.assertEquals(historyChange.getPayload(), lastChange.getPayload());
	}
}