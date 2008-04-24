package com.feed.sync.model;

import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.model.IModelItem;
import com.feed.sync.model.NullModelItem;

public class NullModelItemTests {

		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfNullId()
		{
			new NullModelItem(null);
		}

		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfEmptyId()
		{
			new NullModelItem("");
		}

		@Test
		public void ShouldEqualHash()
		{
			IModelItem item1 = new NullModelItem("1");
			IModelItem item2 = new NullModelItem("1");

			Assert.assertEquals(item1.hashCode(), item2.hashCode());
		}

		@Test
		public void ShouldNotEqualDifferentId()
		{
			IModelItem item1 = new NullModelItem("1");
			IModelItem item2 = new NullModelItem("2");

			Assert.assertNotSame(item1, item2);
			Assert.assertNotSame(item1.hashCode(), item2.hashCode());
		}

		@Test
		public void ShouldNotEqualDifferentHash()
		{
			IModelItem item1 = new NullModelItem("1");
			IModelItem item2 = new NullModelItem("12");
			
			Assert.assertNotSame(item1.hashCode(), item2.hashCode());
		}

		@Test
		public void ShouldNotEqualNull()
		{
			IModelItem item1 = new NullModelItem("1");

			Assert.assertNotSame(item1, null);
		}
}
