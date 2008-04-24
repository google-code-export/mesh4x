package com.feed.sync.model;

import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.model.Related;
import com.feed.sync.model.RelatedType;

public class RelatedTests {

		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfNullLink()
		{
			new Related(null, RelatedType.Complete);
		}

		@Test
		public void ShouldSetProperties()
		{
			Related r = new Related("foo", RelatedType.Complete, "title");

			Assert.assertEquals("foo", r.getLink());
			Assert.assertEquals(RelatedType.Complete, r.getType());
			Assert.assertEquals("title", r.getTitle());
		}
}
