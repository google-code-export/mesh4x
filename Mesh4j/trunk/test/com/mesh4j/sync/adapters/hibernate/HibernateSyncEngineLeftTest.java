package com.mesh4j.sync.adapters.hibernate;

import org.junit.Assert;

import com.mesh4j.sync.AbstractSyncEngineTest;
import com.mesh4j.sync.IRepositoryAdapter;
import com.mesh4j.sync.model.Item;
import com.mesh4j.sync.security.NullSecurity;
import com.mesh4j.sync.test.utils.MockRepository;

public class HibernateSyncEngineLeftTest extends AbstractSyncEngineTest {

	@Override
	protected IRepositoryAdapter makeRightRepository(Item... items) {
		return new MockRepository(items);
	}

	@Override
	protected IRepositoryAdapter makeLeftRepository(Item... items) {
		HibernateAdapter repo = new HibernateAdapter(this.getClass().getResource("User.hbm.xml").getFile(), NullSecurity.INSTANCE);
		
		repo.deleteAll();		
		Assert.assertEquals(0, repo.getAll().size());
		
		for (Item item : items) {
			repo.add(item);
		}
		Assert.assertEquals(items.length, repo.getAll().size());
		
		return repo;
	}


}
