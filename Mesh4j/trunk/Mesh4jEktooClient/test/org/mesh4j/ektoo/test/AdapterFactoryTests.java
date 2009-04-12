package org.mesh4j.ektoo.test;

import org.junit.Assert;
import org.junit.Test;
import org.mesh4j.sync.adapters.feed.ContentReader;
import org.mesh4j.sync.adapters.feed.ContentWriter;
import org.mesh4j.sync.adapters.feed.rss.RssSyndicationFormat;
import org.mesh4j.sync.adapters.http.HttpSyncAdapter;
import org.mesh4j.sync.id.generator.IdGenerator;
import org.mesh4j.sync.security.NullIdentityProvider;

public class AdapterFactoryTests {
	
	@Test
	public void shouldCreateHttpAdapter(){
		String url = "http://localhost:8080/mesh4x/feeds/myMesh/myFeed";
		HttpSyncAdapter adapter = new HttpSyncAdapter(url, RssSyndicationFormat.INSTANCE, NullIdentityProvider.INSTANCE, IdGenerator.INSTANCE, ContentWriter.INSTANCE, ContentReader.INSTANCE);
		Assert.assertEquals(6, adapter.getAll());
	}
}
