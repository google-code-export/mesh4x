package com.mesh4j.sync.hibernate;

import org.junit.Assert;

import com.mesh4j.sync.AbstractSyncEngineTest;
import com.mesh4j.sync.Repository;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.test.utils.MockRepository;

public class HibernateSyncEngineLeftRepoTest extends AbstractSyncEngineTest {

	@Override
	protected Repository makeRightRepository(Item... items) {
		return new MockRepository(items);
	}

	@Override
	protected Repository makeLeftRepository(Item... items) {
		HibernateRepository repo = new HibernateRepository(this.getClass().getResource("User.hbm.xml").getFile());
		
		repo.deleteAll();		
		Assert.assertEquals(0, repo.getAll().size());
		
		for (Item item : items) {
			repo.add(item);
		}
		Assert.assertEquals(items.length, repo.getAll().size());
		
		return repo;
	}


}
