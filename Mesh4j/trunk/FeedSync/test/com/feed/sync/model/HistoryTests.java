package com.feed.sync.model;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import com.feed.sync.model.History;
import com.feed.sync.utils.DateHelper;
import com.feed.sync.utils.test.TestHelper;

public class HistoryTests {
	
		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfSequenceNotGreaterThanZero()
		{
			new History("foo", TestHelper.now(), 0);
		}

		@Test
		public void ShouldBeValidNullBy()
		{
			History h = new History(null, TestHelper.now(), 1);
			Assert.assertNull(h.getBy());
		}

		@Test
		public void ShouldBeValidNullWhen()
		{
			History h = new History("foo", null, 1);
			Assert.assertNull(h.getWhen());
		}

		@Test(expected=IllegalArgumentException.class)
		public void ShouldThrowIfBothByAndWhenNull()
		{
			new History(null, null, 1);
		}

		@Test
		public void ShouldSubsumeWithByEqualSequence()
		{
			History Hx = new History("kzu", null, 2);
			History Hy = new History("kzu", null, 2);

			Assert.assertTrue(Hx.IsSubsumedBy(Hy));
		}

		@Test
		public void ShouldSubsumeWithByGreaterSequence()
		{
			History Hx = new History("kzu", null, 2);
			History Hy = new History("kzu", null, 3);

			Assert.assertTrue(Hx.IsSubsumedBy(Hy));
		}

		@Test
		public void ShouldSubsumeWithoutByEqualWhenAndSequence()
		{
			Date when = TestHelper.now();
			History Hx = new History(null, when, 2);
			History Hy = new History(null, when, 2);

			Assert.assertTrue(Hx.IsSubsumedBy(Hy));
		}

		@Test
		public void ShouldNotSubsumeWithoutByOnXAndByOnYWithEqualWhenAndSequence()
		{
			Date when = TestHelper.now();

			History Hx = new History(null, when, 2);
			History Hy = new History("kzu", when, 2);

			Assert.assertFalse(Hx.IsSubsumedBy(Hy));
		}

		@Test
		public void ShouldNotSubsumeWithoutByAndDifferentWhen()
		{
			History Hx = new History(null, TestHelper.now(), 2);
			History Hy = new History(null, TestHelper.nowAddSeconds(10), 2);

			Assert.assertFalse(Hx.IsSubsumedBy(Hy));
		}

		@Test
		public void ShouldNotEqualNull()
		{
			History h1 = new History("foo");
			History h2 = null;

			this.assertNotEquals(h1, h2);
		}

		@Test
		public void ShouldEqualWithSameBy()
		{
			History h1 = new History("foo");
			History h2 = new History("foo");

			this.assertEquals(h1, h2);
		}

		@Test
		public void ShouldNotEqualWithDifferentBy()
		{
			History h1 = new History("foo");
			History h2 = new History("bar");

			this.assertNotEquals(h1, h2);
		}

		@Test
		public void ShouldEqualWithSameWhen()
		{
			Date now = TestHelper.now();
			History h1 = new History(now);
			History h2 = new History(now);

			this.assertEquals(h1, h2);
		}

		@Test
		public void ShouldNotEqualWithDifferentWhen()
		{
			History h1 = new History(TestHelper.now());
			History h2 = new History(TestHelper.nowAddSeconds(50));

			this.assertNotEquals(h1, h2);
		}

		@Test
		public void ShouldNotEqualWithSameNowButNullBy()
		{
			Date now = TestHelper.now();
			History h1 = new History("kzu", now);
			History h2 = new History(null, now);

			assertNotEquals(h1, h2);
		}

		@Test
		public void ShouldEqualWithSameSequenceAndBy()
		{
			History h1 = new History("kzu", null, 5);
			History h2 = new History("kzu", null, 5);

			assertEquals(h1, h2);
		}

		@Test
		public void ShouldEqualWithSameSequenceAndWhen()
		{
			Date now = TestHelper.now();
			History h1 = new History(null, now, 5);
			History h2 = new History(null, now, 5);

			assertEquals(h1, h2);
		}

		@Test
		public void ShouldHaveSameHashcodeWithSameBy()
		{
			History h1 = new History("kzu");
			History h2 = new History("kzu");

			Assert.assertEquals(h1.hashCode(), h2.hashCode());
		}

		@Test
		public void ShouldHaveSameHashcodeWithSameWhen()
		{
			Date now = TestHelper.now();
			History h1 = new History(now);
			History h2 = new History(now);

			Assert.assertEquals(h1.hashCode(), h2.hashCode());
		}

		@Test
		public void ShouldHaveSameHashcodeWithSameByWhenSequence()
		{
			Date now = TestHelper.now();
			History h1 = new History("kzu", now, 5);
			History h2 = new History("kzu", now, 5);

			Assert.assertEquals(h1.hashCode(), h2.hashCode());
		}

		@Test
		public void ShouldHaveDifferentHashcodeWithNullBy()
		{
			Date now = TestHelper.now();
			History h1 = new History("kzu", now);
			History h2 = new History(null, now);

			Assert.assertNotSame(h1.hashCode(), h2.hashCode());
		}

		@Test
		public void ShouldDefaultNullWhenAndSequenceOne()
		{
			History h = new History("Foo");
			Assert.assertEquals("Foo", h.getBy());
			Assert.assertEquals(null, h.getWhen());
			Assert.assertEquals(1, h.getSequence());
		}

		@Test
		public void ShouldDefaultNullByAndSequenceOne()
		{
			Date now = DateHelper.normalize(TestHelper.now());
			History h = new History(now);
			Assert.assertEquals(now, h.getWhen());
			Assert.assertEquals(null, h.getBy());
			Assert.assertEquals(1, h.getSequence());
		}

		@Test
		public void ShouldEqualClone()
		{
			History h1 = new History("foo", TestHelper.now(), 4);
			History h2 = h1.clone();

			assertEquals(h1, h2);
		}

		@Test
		public void ShouldEqualCloneable()
		{
			History h1 = new History("foo", TestHelper.now(), 4);
			History h2 = h1.clone();

			assertEquals(h1, h2);
		}

		@Test
		public void ShouldTestProperties()
		{
			// TODO TestProperties(new History("foo"));
		}

		@Test
		public void ShouldNormalizeWhenWithoutMilliseconds()
		{
			Date when = TestHelper.makeDate(2007, 6, 6, 6, 6, 6, 100);
			Date expected = TestHelper.makeDate(2007, 6, 6, 6, 6, 6, 0);

			History history = new History(when);

			Assert.assertNotSame(when, history.getWhen());
			Assert.assertTrue(when.getTime() != expected.getTime());
			Assert.assertTrue(expected.getTime() == history.getWhen().getTime());			
		}

		private void assertEquals(History h1, History h2)
		{
			Assert.assertEquals(h1, h2);
			Assert.assertTrue(h1.equals(h2));
// TODO	 (?):		Assert.assertTrue(h1 == h2);
//			Assert.assertFalse(h1 != h2);
		}

		private void assertNotEquals(History h1, History h2)
		{
			Assert.assertFalse(h1.equals(h2));
			Assert.assertNotSame(h1, h2);
//TODO (?):			Assert.assertFalse(h1 == h2);
//			Assert.assertTrue(h1 != h2);
		}
}
