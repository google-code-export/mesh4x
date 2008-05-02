package com.mesh4j.sync.model;

import org.junit.Assert;
import org.junit.Test;

public class NullModelItemTests {

		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfNullId()
		{
			new NullContent(null);
		}

		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfEmptyId()
		{
			new NullContent("");
		}

		@Test
		public void ShouldEqualHash()
		{
			Content item1 = new NullContent("1");
			Content item2 = new NullContent("1");

			Assert.assertEquals(item1.hashCode(), item2.hashCode());
		}

		@Test
		public void ShouldNotEqualDifferentId()
		{
			Content item1 = new NullContent("1");
			Content item2 = new NullContent("2");

			Assert.assertNotSame(item1, item2);
			Assert.assertNotSame(item1.hashCode(), item2.hashCode());
		}

		@Test
		public void ShouldNotEqualDifferentHash()
		{
			Content item1 = new NullContent("1");
			Content item2 = new NullContent("12");
			
			Assert.assertNotSame(item1.hashCode(), item2.hashCode());
		}

		@Test
		public void ShouldNotEqualNull()
		{
			Content item1 = new NullContent("1");

			Assert.assertNotSame(item1, null);
		}
}
