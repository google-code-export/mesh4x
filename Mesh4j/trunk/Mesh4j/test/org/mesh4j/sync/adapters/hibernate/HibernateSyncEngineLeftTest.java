package org.mesh4j.sync.adapters.hibernate;

import java.io.File;

import org.junit.Assert;
import org.mesh4j.sync.AbstractSyncEngineTest;
import org.mesh4j.sync.ISyncAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.model.Item;
import org.mesh4j.sync.security.NullIdentityProvider;
import org.mesh4j.sync.test.utils.MockRepository;


public class HibernateSyncEngineLeftTest extends AbstractSyncEngineTest {

	@Override
	protected ISyncAdapter makeRightRepository(Item... items) {
		return new MockRepository(items);
	}

	@Override
	protected ISyncAdapter makeLeftRepository(Item... items) {
		HibernateSessionFactoryBuilder builder = new HibernateSessionFactoryBuilder();
		builder.addMapping(new File(this.getClass().getResource("User.hbm.xml").getFile()));
		builder.addMapping(new File(this.getClass().getResource("User_sync.hbm.xml").getFile()));
		builder.setPropertiesFile(new File(this.getClass().getResource("xx_hibernate.properties").getFile()));
		
		HibernateAdapter repo = new HibernateAdapter(builder, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE);
		
		repo.deleteAll();		
		Assert.assertEquals(0, repo.getAll().size());
		
		for (Item item : items) {
			repo.add(item);
		}
		Assert.assertEquals(items.length, repo.getAll().size());
		
		return repo;
	}

	@Override
	protected String getUserName(Item item) {
		return item.getContent().getPayload().element("name").getText();
	}


}
